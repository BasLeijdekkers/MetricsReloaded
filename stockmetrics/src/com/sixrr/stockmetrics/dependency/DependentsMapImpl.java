/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.dependency;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.Bag;
import com.sixrr.metrics.utils.ClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DependentsMapImpl implements DependentsMap {
    private final Map<PsiClass, Bag<PsiClass>> dependents = new HashMap<PsiClass, Bag<PsiClass>>(1024);
    private final Map<PsiClass, Bag<PsiPackage>> packageDependents = new HashMap<PsiClass, Bag<PsiPackage>>(1024);
    private final Map<PsiPackage, Bag<PsiPackage>> packageToPackageDependents =
            new HashMap<PsiPackage, Bag<PsiPackage>>(
                    1024);
    private final Map<PsiClass, Set<PsiClass>> transitiveDependents = new HashMap<PsiClass, Set<PsiClass>>(1024);
    private final Map<PsiPackage, Set<PsiPackage>> transitivePackageDependents =
            new HashMap<PsiPackage, Set<PsiPackage>>(
                    1024);

    public Set<PsiClass> calculateDependents(PsiClass aClass) {
        final Bag<PsiClass> existing = dependents.get(aClass);
        if (existing != null) {
            return existing.getContents();
        }
        return Collections.emptySet();
    }

    public int getStrengthForDependent(PsiClass aClass, PsiClass dependentClass) {
        final Bag<PsiClass> dependentsForClass = dependents.get(aClass);
        return dependentsForClass.getCountForObject(dependentClass);
    }

    public Set<PsiPackage> calculatePackageDependents(PsiClass aClass) {
        final Bag<PsiPackage> existing = packageDependents.get(aClass);
        if (existing == null) {
            return Collections.emptySet();
        }
        return existing.getContents();
    }

    public Set<PsiPackage> calculatePackageToPackageDependents(PsiPackage packageName) {
        final Bag<PsiPackage> existing = packageToPackageDependents.get(packageName);
        if (existing == null) {
            return Collections.emptySet();
        }
        return existing.getContents();
    }

    public int getStrengthForPackageDependent(PsiClass aClass, PsiPackage dependentPackage) {
        final Bag<PsiPackage> dependentsForClass = packageDependents.get(aClass);
        return dependentsForClass.getCountForObject(dependentPackage);
    }

    public Set<PsiClass> calculateTransitiveDependents(PsiClass aClass) {
        final Set<PsiClass> out = transitiveDependents.get(aClass);

        if (out != null) {
            return out;
        }

        final List<PsiClass> pendingClasses = new ArrayList<PsiClass>();
        pendingClasses.add(aClass);
        final Set<PsiClass> allDependents = new HashSet<PsiClass>();
        while (pendingClasses.size() > 0) {
            final PsiClass dependentClass = pendingClasses.get(0);
            pendingClasses.remove(0);
            if (!allDependents.contains(dependentClass)) {
                if (transitiveDependents.containsKey(dependentClass)) {
                    allDependents.addAll(transitiveDependents.get(dependentClass));
                } else {
                    allDependents.add(dependentClass);
                    if (dependentClass != null) {
                        final Set<PsiClass> indirectDependents = calculateDependents(dependentClass);
                        pendingClasses.addAll(indirectDependents);
                    }
                }
            }
        }
        transitiveDependents.put(aClass, allDependents);
        return allDependents;
    }

    private PsiClass findClass(PsiManager psiManager, String dependentClassName) {
        try {
            final Project project = psiManager.getProject();
            final GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
            return psiFacade.findClass(dependentClassName, scope);
        } catch (Exception ignore) {
            return null;
        }
    }

    public Set<PsiPackage> calculateTransitivePackageDependents(PsiPackage packageName) {
        final Set<PsiPackage> out = transitivePackageDependents.get(packageName);
        if (out != null) {
            return out;
        }
        final List<PsiPackage> pendingPackages = new ArrayList<PsiPackage>();
        pendingPackages.add(packageName);
        final Set<PsiPackage> allDependents = new HashSet<PsiPackage>();
        while (pendingPackages.size() > 0) {
            final PsiPackage dependentPackage = pendingPackages.get(0);
            pendingPackages.remove(0);
            if (!allDependents.contains(dependentPackage)) {
                if (transitivePackageDependents.containsKey(dependentPackage)) {
                    allDependents.addAll(transitivePackageDependents.get(dependentPackage));
                } else {
                    allDependents.add(dependentPackage);
                    final Set<PsiPackage> indirectDependents = calculatePackageToPackageDependents(dependentPackage);
                    pendingPackages.addAll(indirectDependents);
                }
            }
        }
        transitivePackageDependents.put(packageName, allDependents);
        return allDependents;
    }

    public void build(PsiClass aClass) {
        final DependenciesVisitor visitor = new DependenciesVisitor();
        aClass.accept(visitor);
    }

    private void addDependent(PsiClass aClass, PsiClass dependentClass, PsiPackage dependentPackage) {
        final PsiPackage aPackage = ClassUtils.findPackage(aClass);
        Bag<PsiClass> dependentsForClass = dependents.get(aClass);
        if (dependentsForClass == null) {
            dependentsForClass = new Bag<PsiClass>();
            dependents.put(aClass, dependentsForClass);
        }
        dependentsForClass.add(dependentClass);
        Bag<PsiPackage> packageDependentsForClass = packageDependents.get(aClass);
        if (packageDependentsForClass == null) {
            packageDependentsForClass = new Bag<PsiPackage>();
            packageDependents.put(aClass, packageDependentsForClass);
        }
        packageDependentsForClass.add(dependentPackage);
        Bag<PsiPackage> packageDependentsForPackage = packageToPackageDependents.get(aPackage);
        if (packageDependentsForPackage == null) {
            packageDependentsForPackage = new Bag<PsiPackage>();
            packageToPackageDependents.put(aPackage, packageDependentsForPackage);
        }
        packageDependentsForPackage.add(dependentPackage);
    }

    private class DependenciesVisitor extends JavaRecursiveElementVisitor {
        private final Stack<PsiClass> classStack = new Stack<PsiClass>();
        private PsiClass currentClass = null;
        private PsiPackage currentPackage = null;

        public void visitClass(PsiClass aClass) {
            if (!ClassUtils.isAnonymous(aClass)) {
                classStack.push(currentClass);
                currentClass = aClass;
                currentPackage = ClassUtils.findPackage(currentClass);
                final PsiType[] superTypes = aClass.getSuperTypes();
                for (final PsiType superType : superTypes) {
                    addDependency(superType);
                }
                final PsiTypeParameter[] parameters = aClass.getTypeParameters();
                for (PsiTypeParameter parameter : parameters) {
                    addDependencyForClass(parameter);
                }
            }
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass)) {
                currentClass = classStack.pop();
                currentPackage = ClassUtils.findPackage(currentClass);
            }
        }

        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null) {
                return;
            }
            final Project project = expression.getProject();
            final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
            final PsiElementFactory elementFactory = psiFacade.getElementFactory();
            final PsiClassType type = elementFactory.createType(containingClass);
            addDependency(type);
            final PsiType[] arguments = expression.getTypeArguments();
            for (PsiType argument : arguments) {
                addDependency(argument);
            }
        }

        public void visitReferenceExpression(PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);

            final PsiElement element = expression.resolve();
            if (element == null) {
                return;
            }
            if (element instanceof PsiField) {
                final PsiClass containingClass = ((PsiMember) element).getContainingClass();
                addDependencyForClass(containingClass);
            } else if (element instanceof PsiClass) {
                addDependencyForClass((PsiClass) element);
            }
        }

        public void visitField(PsiField field) {
            super.visitField(field);
            final PsiType type = field.getType();
            addDependency(type);
        }

        public void visitLocalVariable(PsiLocalVariable var) {
            super.visitLocalVariable(var);
            final PsiType type = var.getType();
            addDependency(type);
        }

        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiType returnType = method.getReturnType();
            addDependency(returnType);
            addDependenciesForParameters(method);
            addDependencyForThrowsClause(method);
        }

        private void addDependencyForThrowsClause(PsiMethod method) {
            final PsiReferenceList throwsList = method.getThrowsList();
            final PsiClassType[] throwsTypes = throwsList.getReferencedTypes();
            for (final PsiClassType throwsType : throwsTypes) {
                addDependency(throwsType);
            }
        }

        private void addDependenciesForParameters(PsiMethod method) {
            final PsiParameterList parameterList = method.getParameterList();
            final PsiParameter[] parameters = parameterList.getParameters();
            for (final PsiParameter parameter : parameters) {
                final PsiType paramType = parameter.getType();
                addDependency(paramType);
            }
        }

        public void visitNewExpression(PsiNewExpression expression) {
            super.visitNewExpression(expression);
            final PsiType classType = expression.getType();
            addDependency(classType);
            final PsiType[] arguments = expression.getTypeArguments();
            for (PsiType argument : arguments) {
                addDependency(argument);
            }
        }

        public void visitClassObjectAccessExpression(PsiClassObjectAccessExpression exp) {
            super.visitClassObjectAccessExpression(exp);
            final PsiTypeElement operand = exp.getOperand();
            final PsiType classType = operand.getType();
            addDependency(classType);
        }

        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
            final PsiParameter[] catchBlockParameters = statement.getCatchBlockParameters();
            for (final PsiParameter param : catchBlockParameters) {
                final PsiType catchType = param.getType();
                addDependency(catchType);
            }
        }

        public void visitInstanceOfExpression(PsiInstanceOfExpression exp) {
            super.visitInstanceOfExpression(exp);
            final PsiTypeElement checkType = exp.getCheckType();
            if (checkType == null) {
                return;
            }
            final PsiType classType = checkType.getType();
            addDependency(classType);
        }

        public void visitTypeCastExpression(PsiTypeCastExpression exp) {
            super.visitTypeCastExpression(exp);
            final PsiTypeElement castType = exp.getCastType();
            if (castType == null) {
                return;
            }
            final PsiType classType = castType.getType();
            addDependency(classType);
        }

        private void addDependency(@Nullable PsiType type) {
            if (type == null) {
                return;
            }
            final PsiType baseType = type.getDeepComponentType();
            if (!(baseType instanceof PsiClassType)) {
                return;
            }
            final PsiClassType classType = (PsiClassType) baseType;
            final PsiType[] parameters = classType.getParameters();
            for (PsiType parameter : parameters) {
                addDependency(parameter);
            }
            final PsiClass referencedClass = classType.resolve();
            addDependencyForClass(referencedClass);
        }

        private void addDependencyForClass(PsiClass referencedClass) {
            if (currentClass == null) {
                return;
            }
            if (referencedClass == null) {
                return;
            }
            if (referencedClass.equals(currentClass)) {
                return;
            }
            @NonNls final String referencedClassName = referencedClass.getQualifiedName();
            if ("_Dummy_.__Array__".equals(referencedClassName)) {
                return;
            }
            final PsiJavaFile classFile =
                    PsiTreeUtil.getParentOfType(referencedClass, PsiJavaFile.class);
            if (classFile == null) {
                return;
            }
            @NonNls final String classFileName = classFile.getName();
            if (!classFileName.endsWith(".java")) {
                return;
            }
            addDependent(referencedClass, currentClass, currentPackage);
        }
    }
}
