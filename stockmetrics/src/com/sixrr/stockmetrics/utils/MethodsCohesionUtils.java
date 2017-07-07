/*
 * Copyright 2005-2017 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.utils;

import com.intellij.psi.*;
import com.sixrr.stockmetrics.classCalculators.LackOfCohesionOfMethodsClassCalculator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class MethodsCohesionUtils {
    private MethodsCohesionUtils() {
    }

    @NonNls
    private static final Set<String> boilerplateMethods = new HashSet<String>();

    static {
        //noinspection HardCodedStringLiteral
        Collections.addAll(boilerplateMethods, "toString", "equals", "hashCode", "finalize", "clone", "readObject",
                "writeObject");
    }

    /**
     * Find all applicable methods in class. Method is considered applicable if it is not constructor or boilerplate.
     * @param aClass Input class
     * @return Set of applicable methods from input class
     */
    @NotNull
    public static Set<PsiMethod> getApplicableMethods(@NotNull final PsiClass aClass) {
        final PsiMethod[] methods = aClass.getMethods();
        final Set<PsiMethod> applicableMethods = new HashSet<PsiMethod>();
        for (PsiMethod method : methods) {
            final String methodName = method.getName();
            if (!method.isConstructor() && !boilerplateMethods.contains(methodName)) {
                applicableMethods.add(method);
            }
        }
        return applicableMethods;
    }

    public static Set<Set<PsiMethod>> calculateComponents(Set<PsiMethod> applicableMethods,
                                                           Map<PsiMethod, Set<PsiField>> fieldsPerMethod,
                                                           Map<PsiMethod, Set<PsiMethod>> linkedMethods) {
        final Set<Set<PsiMethod>> components = new HashSet<Set<PsiMethod>>();
        while (applicableMethods.size() > 0) {
            final Set<PsiMethod> component = new HashSet<PsiMethod>();
            final Set<PsiField> fieldsUsed = new HashSet<PsiField>();
            final PsiMethod testMethod = applicableMethods.iterator().next();
            applicableMethods.remove(testMethod);
            component.add(testMethod);
            fieldsUsed.addAll(fieldsPerMethod.get(testMethod));
            while (true) {
                final Set<PsiMethod> methodsToAdd = new HashSet<PsiMethod>();
                for (PsiMethod method : applicableMethods) {
                    if (SetUtil.hasIntersec(fieldsPerMethod.get(method), fieldsUsed) ||
                            SetUtil.hasIntersec(linkedMethods.get(method), component)) {
                        methodsToAdd.add(method);
                        fieldsUsed.addAll(fieldsPerMethod.get(method));
                    }
                }
                if (methodsToAdd.size() == 0) {
                    break;
                }
                applicableMethods.removeAll(methodsToAdd);
                component.addAll(methodsToAdd);
            }
            components.add(component);
        }
        return components;
    }

    public static Map<PsiMethod, Set<PsiField>> calculateFieldUsage(Set<PsiMethod> applicableMethods) {
        final Map<PsiMethod, Set<PsiField>> fieldsPerMethod = new HashMap<PsiMethod, Set<PsiField>>();
        for (PsiMethod method : applicableMethods) {
            final Set<PsiField> fields = calculateUsedFields(method);
            fieldsPerMethod.put(method, fields);
        }
        return fieldsPerMethod;
    }

    public static Set<PsiField> calculateUsedFields(PsiMethod method) {
        final FieldsUsedVisitor visitor = new FieldsUsedVisitor();
        method.accept(visitor);
        return visitor.getFieldsUsed();
    }



    public static Map<PsiMethod, Set<PsiMethod>> calculateMethodLinkage(Set<PsiMethod> applicableMethods) {
        final Map<PsiMethod, Set<PsiMethod>> linkages = new HashMap<PsiMethod, Set<PsiMethod>>();
        for (PsiMethod method : applicableMethods) {
            final Set<PsiMethod> linkedMethods = calculateLinkedMethods(method, applicableMethods);
            linkages.put(method, linkedMethods);
        }
        //add the transpose, since linkage is undirected
        for (PsiMethod method : applicableMethods) {
            final Set<PsiMethod> linkedMethods = linkages.get(method);
            for (PsiMethod linkedMethod : linkedMethods) {
                linkages.get(linkedMethod).add(method);
            }
        }
        return linkages;
    }

    public static Set<PsiMethod> calculateLinkedMethods(PsiMethod method, Set<PsiMethod> applicableMethods) {
        final MethodsUsedVisitor visitor = new MethodsUsedVisitor(applicableMethods);
        method.accept(visitor);
        return visitor.getMethodsUsed();
    }

    private static class MethodsUsedVisitor extends JavaRecursiveElementVisitor {
        private final Set<PsiMethod> applicableMethods;
        private final Set<PsiMethod> methodsUsed = new HashSet<PsiMethod>();

        MethodsUsedVisitor(Set<PsiMethod> applicableMethods) {
            this.applicableMethods = applicableMethods;
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression callExpression) {
            super.visitMethodCallExpression(callExpression);
            final PsiMethod testMethod = callExpression.resolveMethod();
            if (applicableMethods.contains(testMethod)) {
                methodsUsed.add(testMethod);
            }
        }

        @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
        public Set<PsiMethod> getMethodsUsed() {
            return methodsUsed;
        }
    }

    private static class FieldsUsedVisitor extends JavaRecursiveElementVisitor {
        private final Set<PsiField> fieldsUsed = new HashSet<PsiField>();

        FieldsUsedVisitor() {
        }

        @Override
        public void visitReferenceExpression(PsiReferenceExpression referenceExpression) {
            super.visitReferenceExpression(referenceExpression);
            final PsiElement referent = referenceExpression.resolve();
            if (!(referent instanceof PsiField)) {
                return;
            }
            final PsiField field = (PsiField) referent;
            fieldsUsed.add(field);
        }

        @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
        public Set<PsiField> getFieldsUsed() {
            return fieldsUsed;
        }
    }
}
