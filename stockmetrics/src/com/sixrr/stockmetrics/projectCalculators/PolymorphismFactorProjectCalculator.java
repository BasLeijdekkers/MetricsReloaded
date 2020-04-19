/*
 * Copyright 2005, Sixth and Red River Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.*;
import com.intellij.util.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates the Polymorphism Factor (PF or POF) of a project
 * With regard to how this measure is calculated ...
 *  Simply stated, PF is a measure of:
 *   the number of method overrides (number of times a subclass @Overrides a method)
 *   divided by
 *   the number of possible method overrides (the number of relevant, non-override methods
 *                                            multiplied by the number of relevant sub-classes)
 *   OR numOverridingMethods / numOverridePotentials
 *  To make this measure meaningful, the following constraints have been enforced:
 *  - A class is only considered if its source code is part of the project; no library or SDK classes
 *  - Enums (which cannot be overridden) are excluded
 *  - Interfaces (whose implementation isn't really Polymorphism) are excluded
 *  - Static and final methods (which cannot be overridden) are excluded
 *  - Constructors (which aren't overridden) are excluded
 *  - Getters and setters (for which overrides don't usually make sense) are excluded
 *  - For public and protected methods, all project subclasses are considered
 *  - For package-private methods, only subclasses within the same package are considered
 * Further reading:
 * <a>http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.115.2709&rep=rep1&type=pdf</a>
 *
 * (Note: if a user selects a custom (directory or module) scope, this will affect the list of classes
 *  visited; however, the sub- and super-classes checked will be those from the entire project
 *  It could be a useful feature to modify this behavior so that only the custom scope classes are considered)
 */
public class PolymorphismFactorProjectCalculator extends ProjectCalculator {

    private int numOverridingMethods;
    private int numOverridePotentials;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    @Override
    public void endMetricsRun() {
        postMetric(numOverridingMethods, numOverridePotentials);
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        private final String[] GETTER_SETTER_PREFIXES = {"get", "set", "is", "has", "can", "should"};
        private static final String OBJECT = "Object";
        private static final String JAVA_SRC = "JAVA";

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (isRelevantClass(aClass)) {
                final PsiMethod[] methods = aClass.getMethods();
                for (PsiMethod method : methods) {
                    if (isRelevantMethod(method)) {
                        final PsiMethod[] superMethods = getRelevantSuperMethods(method);
                        if (superMethods.length > 0) {
                            //this method is an override; increment the numerator
                            numOverridingMethods++;
                        } else {
                            //not an override method; check to see if there are potential subclass overrides
                            numOverridePotentials += getRelevantSubclassCount(aClass, method);
                        }
                    }
                }
            }
        }

        /**
         * Decide whether the method in question is relevant for scoring
         * Constructors, private, static, or final methods, getters/setters,
         *  and classes from non-project-source classes aren't relevant
         *
         * @param method method to check
         * @return whether or not the method should be used for scoring
         */
        private boolean isRelevantMethod(final PsiMethod method) {
            return !(method != null
                    && method.isConstructor()
                    || hasPrivateAccess(method)
                    || isStatic(method)
                    || isFinal(method)
                    || isGetterSetter(method))
                    && isWithinProjectSource(method)
                    ;
        }

        private boolean hasPrivateAccess(final PsiMethod method) {
            return method != null
                    && method.getModifierList().hasModifierProperty(PsiModifier.PRIVATE);
        }

        private boolean isStatic(final PsiMethod method) {
            return method != null
                    && method.getModifierList().hasModifierProperty(PsiModifier.STATIC);
        }

        private boolean isFinal(final PsiMethod method) {
            return method != null
                    && method.getModifierList().hasModifierProperty(PsiModifier.FINAL);
        }

        /**
         * Check to see if method is a getter/setter
         * Note: we could assume that all getters/setters conform to JavaBean naming ...
         *   for getters: getProperty (or isProperty for booleans)
         *   for setters: setProperty
         * But we should also probably consider the extension of alternate verbs for booleans ...
         *   including 'has', 'can', and 'should'
         *
         * @param method method to check
         * @return whether or not the method appears to be a getter or setter
         */
        private boolean isGetterSetter(final PsiMember method) {
            boolean isGetterSetter = false;
            if (method != null && method.getContainingClass() != null) {
                //look through the fields/properties and see if the current method looks like a getter/setter
                for (PsiField field : method.getContainingClass().getAllFields()) {
                    for (String prefix : GETTER_SETTER_PREFIXES) {
                        if (method.getName() != null
                                && method.getName().equalsIgnoreCase(prefix + field.getName())) {
                            isGetterSetter = true;
                            break;
                        }
                    }
                }
            }
            return isGetterSetter;
        }

        /**
         * Get super-methods that are relevant for scoring
         *
         * @param method the base method
         * @return an array of super-methods from relevant classes
         */
        private PsiMethod[] getRelevantSuperMethods(final PsiMethod method) {
            final List<PsiMethod> relevantMethods = new ArrayList<>();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final PsiMethod[] superMethods = method.findSuperMethods();
                    for (final PsiMethod superMethod : superMethods) {
                        final PsiClass parentClass = superMethod.getContainingClass();
                        if (isRelevantClass(parentClass)) {
                            relevantMethods.add(superMethod);
                        }
                    }
                }
            };
            final ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcess(runnable, null);
            return relevantMethods.toArray(new PsiMethod[relevantMethods.size()]);
        }

        /**
         * Get subclasses that are relevant for scoring
         *
         * @param aClass the base class
         * @param method the base method
         * @return the number of relevant subclasses to be used for scoring
         */
        private int getRelevantSubclassCount(final PsiClass aClass, final PsiMethod method) {
            final int[] numSubclasses = new int[1];
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final Project project = executionContext.getProject();
                    //limit search to current project
                    final GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
                    final Query<PsiClass> subClasses = ClassInheritorsSearch.search(
                            aClass, projectScope, true, true, true);
                    for (final PsiClass subClass : subClasses) {
                        boolean relevant = false;
                        if (isRelevantClass(subClass)) {
                            //check to see if the method is either public or private
                            //if not, we know it's package-private, because we've already checked for private
                            if (!isPublicOrProtected(method)) {
                                //method is package-private; only consider subclasses in same package
                                if (inSamePackage(aClass, subClass)) {
                                    relevant = true;
                                }
                            }
                            else {
                                relevant = true;
                            }
                        }
                        if (relevant) {
                            numSubclasses[0]++;
                        }
                    }
                }
            };
            final ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcess(runnable, null);
            return numSubclasses[0];
        }

        /**
         * Decide whether the class in question is relevant for scoring
         * Enums, interfaces, Object, and non-project-source classes aren't relevant
         *
         * @param aClass the class to check
         * @return whether or not the class should be used for scoring
         */
        private boolean isRelevantClass(PsiClass aClass) {
            return !(aClass == null
                    || aClass.isEnum()
                    || aClass.isInterface()
                    || isObjectClass(aClass))
                    && isWithinProjectSource(aClass)
                    ;
        }

        private boolean isObjectClass(final PsiClass aClass) {
            return aClass != null
                    && aClass.getName() != null
                    && aClass.getName().equalsIgnoreCase(OBJECT);
        }

        private boolean isWithinProjectSource(final PsiMethod method) {
            boolean withinSource = false;
            if (method != null) {
                withinSource = isWithinProjectSource(method.getContainingClass());
            }
            return withinSource;
        }
        private boolean isWithinProjectSource(final PsiClass aClass) {
            boolean withinSource = false;
            if (aClass != null) {
                final PsiFile classFile = aClass.getContainingFile();
                final FileType fileType = classFile.getFileType();
                if (fileType.getName().equalsIgnoreCase(JAVA_SRC)) {
                    withinSource = true;
                }
            }
            return withinSource;
        }

        /**
         * Checks to see if the two classes are in the same package
         *
         * @param aClass main class
         * @param subClass subclass to compare
         * @return whether or not the two classes are in the same package
         */
        private boolean inSamePackage(final PsiClass aClass, final PsiClass subClass) {
            boolean samePackage = false;
            if (aClass != null && subClass != null) {
                final PsiJavaFile aClassFile = (PsiJavaFile) aClass.getContainingFile();
                final PsiJavaFile subClassFile = (PsiJavaFile) subClass.getContainingFile();
                final String aClassPackage = aClassFile.getPackageName();
                final String subClassPackage = subClassFile.getPackageName();
                if (aClassPackage.equals(subClassPackage)) {
                    samePackage = true;
                }
            }
            return samePackage;
        }

        private boolean isPublicOrProtected(final PsiMethod method) {
            return method != null
                    && (method.getModifierList().hasModifierProperty(PsiModifier.PROTECTED)
                    || method.getModifierList().hasModifierProperty(PsiModifier.PUBLIC));
        }
    }

}
