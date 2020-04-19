/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.psi.*;
import com.sixrr.metrics.utils.Bag;
import com.sixrr.metrics.utils.ClassUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DependencyMapImpl implements DependencyMap, DependentsMap {

    private final Map<PsiClass, Bag<PsiClass>> dependencies = new HashMap<>();
    private final Map<PsiClass, Set<PsiClass>> transitiveDependencies = new HashMap<>();
    private final Map<PsiPackage, Set<PsiPackage>> transitivePackageDependencies = new HashMap<>();
    private final Map<PsiClass, Set<PsiClass>> stronglyConnectedComponents = new HashMap<>();
    private final Map<PsiClass, Integer> levelOrders = new HashMap<>();
    private final Map<PsiClass, Integer> adjustedLevelOrders = new HashMap<>();
    private final Map<PsiClass, Bag<PsiPackage>> packageDependencies = new HashMap<>();
    private final Map<PsiPackage, Bag<PsiPackage>> packageToPackageDependencies = new HashMap<>();
    private final Map<PsiPackage, Set<PsiPackage>> stronglyConnectedPackageComponents = new HashMap<>();
    private final Map<PsiPackage, Integer> packageLevelOrders = new HashMap<>();
    private final Map<PsiPackage, Integer> packageAdjustedLevelOrders = new HashMap<>();

    private final Map<PsiClass, Bag<PsiClass>> dependents = new HashMap<>();
    private final Map<PsiClass, Bag<PsiPackage>> packageDependents = new HashMap<>();
    private final Map<PsiPackage, Bag<PsiPackage>> packageToPackageDependents = new HashMap<>();
    private final Map<PsiClass, Set<PsiClass>> transitiveDependents = new HashMap<>();
    private final Map<PsiPackage, Set<PsiPackage>> transitivePackageDependents = new HashMap<>();

    @Override
    public Set<PsiClass> calculateDependents(PsiClass aClass) {
        final Bag<PsiClass> existing = dependents.get(aClass);
        if (existing != null) {
            return existing.getContents();
        }
        return Collections.emptySet();
    }

    @Override
    public int getStrengthForDependent(PsiClass aClass, PsiClass dependentClass) {
        final Bag<PsiClass> dependentsForClass = dependents.get(aClass);
        return dependentsForClass.getCountForObject(dependentClass);
    }

    @Override
    public Set<PsiPackage> calculatePackageDependents(PsiClass aClass) {
        final Bag<PsiPackage> existing = packageDependents.get(aClass);
        if (existing == null) {
            return Collections.emptySet();
        }
        return existing.getContents();
    }

    @Override
    public Set<PsiPackage> calculatePackageToPackageDependents(PsiPackage aPackage) {
        final Bag<PsiPackage> existing = packageToPackageDependents.get(aPackage);
        if (existing == null) {
            return Collections.emptySet();
        }
        return existing.getContents();
    }

    @Override
    public int getStrengthForPackageDependent(PsiClass aClass, PsiPackage dependentPackage) {
        final Bag<PsiPackage> dependentsForClass = packageDependents.get(aClass);
        return dependentsForClass.getCountForObject(dependentPackage);
    }

    @Override
    public Set<PsiClass> calculateTransitiveDependents(PsiClass aClass) {
        final Set<PsiClass> out = transitiveDependents.get(aClass);
        if (out != null) {
            return out;
        }

        final List<PsiClass> pendingClasses = new ArrayList<>();
        pendingClasses.add(aClass);
        final Set<PsiClass> allDependents = new HashSet<>();
        while (!pendingClasses.isEmpty()) {
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

    @Override
    public Set<PsiPackage> calculateTransitivePackageDependents(PsiPackage aPackage) {
        final Set<PsiPackage> out = transitivePackageDependents.get(aPackage);
        if (out != null) {
            return out;
        }
        final List<PsiPackage> pendingPackages = new ArrayList<>();
        pendingPackages.add(aPackage);
        final Set<PsiPackage> allDependents = new HashSet<>();
        while (!pendingPackages.isEmpty()) {
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
        transitivePackageDependents.put(aPackage, allDependents);
        return allDependents;
    }

    @Override
    public Set<PsiClass> calculateDependencies(PsiClass aClass) {
        if (dependencies.containsKey(aClass)) {
            final Bag<PsiClass> dependenciesForClass = dependencies.get(aClass);
            return dependenciesForClass.getContents();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<PsiClass> calculateTransitiveDependencies(PsiClass aClass) {
        final Set<PsiClass> out = transitiveDependencies.get(aClass);
        if (out != null) {
            return out;
        }

        final List<PsiClass> pendingClasses = new ArrayList<>();
        pendingClasses.add(aClass);
        final Set<PsiClass> allDependencies = new HashSet<>();
        while (!pendingClasses.isEmpty()) {
            final PsiClass pendingClass = pendingClasses.get(0);
            pendingClasses.remove(0);
            if (!allDependencies.contains(pendingClass)) {
                if (transitiveDependencies.containsKey(pendingClass)) {
                    allDependencies.addAll(transitiveDependencies.get(pendingClass));
                } else {
                    allDependencies.add(pendingClass);
                    if (pendingClass != null) {
                        final Set<PsiClass> indirectDependencies = calculateDependencies(pendingClass);
                        pendingClasses.addAll(indirectDependencies);
                    }
                }
            }
        }
        transitiveDependencies.put(aClass, allDependencies);
        return allDependencies;
    }

    @Override
    public Set<PsiClass> calculateStronglyConnectedComponents(PsiClass aClass) {
        final Set<PsiClass> out = stronglyConnectedComponents.get(aClass);
        if (out != null) {
            return out;
        }
        final Set<PsiClass> transitiveDeps = calculateTransitiveDependencies(aClass);
        final Set<PsiClass> component = new HashSet<>();
        component.add(aClass);
        for (final PsiClass dependencyClass : transitiveDeps) {
            if (dependencyClass != null) {
                final Set<PsiClass> dependencyDependencies = calculateTransitiveDependencies(dependencyClass);
                if (transitiveDeps.size() == dependencyDependencies.size()) {
                    component.add(dependencyClass);
                }
            }
        }
        for (final PsiClass componentElement : component) {
            stronglyConnectedComponents.put(componentElement, component);
        }
        return component;
    }

    @Override
    public int calculateLevelOrder(PsiClass aClass) {
        final Integer out = levelOrders.get(aClass);
        if (out != null) {
            return out.intValue();
        }
        final Set<PsiClass> dependentClasses = calculateDependencies(aClass);
        final Set<PsiClass> mutuallyDependentClasses = calculateStronglyConnectedComponents(aClass);
        int levelOrder = 0;
        for (final PsiClass dependentClass : dependentClasses) {
            if (!mutuallyDependentClasses.contains(dependentClass)) {
                if (dependentClass != null) {
                    final int dependentLevelOrder = calculateLevelOrder(dependentClass);
                    levelOrder = Math.max(levelOrder, dependentLevelOrder);
                }
            }
        }
        levelOrder += 1;
        for (final PsiClass stronglyConnectedClass : mutuallyDependentClasses) {
            levelOrders.put(stronglyConnectedClass, Integer.valueOf(levelOrder));
        }
        return levelOrder;
    }

    @Override
    public int calculateAdjustedLevelOrder(PsiClass aClass) {
        final Integer out = adjustedLevelOrders.get(aClass);
        if (out != null) {
            return out.intValue();
        }
        final Set<PsiClass> dependentClasses = calculateDependencies(aClass);
        final Set<PsiClass> mutuallyDependentClasses = calculateStronglyConnectedComponents(aClass);
        int levelOrder = 0;
        for (final PsiClass dependentClass : dependentClasses) {
            if (!mutuallyDependentClasses.contains(dependentClass)) {
                if (dependentClass != null) {
                    final int dependentLevelOrder = calculateAdjustedLevelOrder(dependentClass);
                    levelOrder = Math.max(levelOrder, dependentLevelOrder);
                }
            }
        }
        levelOrder += mutuallyDependentClasses.size();
        for (final PsiClass stronglyConnectedClass : mutuallyDependentClasses) {
            adjustedLevelOrders.put(stronglyConnectedClass, Integer.valueOf(levelOrder));
        }
        return levelOrder;
    }

    @Override
    public Set<PsiPackage> calculatePackageDependencies(PsiClass aClass) {
        final Bag<PsiPackage> existing = packageDependencies.get(aClass);
        if (existing != null) {
            return existing.getContents();
        }
        return Collections.emptySet();
    }

    @Override
    public Set<PsiPackage> calculateTransitivePackageDependencies(PsiPackage aPackage) {
        final Set<PsiPackage> out = transitivePackageDependencies.get(aPackage);
        if (out != null) {
            return out;
        }
        final List<PsiPackage> pendingPackages = new ArrayList<>();
        pendingPackages.add(aPackage);
        final Set<PsiPackage> allDependencies = new HashSet<>();
        while (!pendingPackages.isEmpty()) {
            final PsiPackage dependencyPackageName = pendingPackages.get(0);
            pendingPackages.remove(0);
            if (!allDependencies.contains(dependencyPackageName)) {
                if (transitivePackageDependencies.containsKey(dependencyPackageName)) {
                    allDependencies.addAll(transitivePackageDependencies.get(dependencyPackageName));
                } else {
                    allDependencies.add(dependencyPackageName);
                    final Set<PsiPackage> indirectDependencies =
                            calculatePackageToPackageDependencies(dependencyPackageName);
                    pendingPackages.addAll(indirectDependencies);
                }
            }
        }
        transitivePackageDependencies.put(aPackage, allDependencies);
        return allDependencies;
    }

    @Override
    public Set<PsiPackage> calculateStronglyConnectedPackageComponents(PsiPackage aPackage) {
        final Set<PsiPackage> out = stronglyConnectedPackageComponents.get(aPackage);
        if (out != null) {
            return out;
        }
        final Set<PsiPackage> transitiveDeps = calculateTransitivePackageDependencies(aPackage);
        final Set<PsiPackage> component = new HashSet<>();
        component.add(aPackage);
        for (final PsiPackage dependencyPackage : transitiveDeps) {
            final Set<PsiPackage> dependencyDependencies = calculateTransitivePackageDependencies(dependencyPackage);
            if (transitiveDeps.size() == dependencyDependencies.size()) {
                component.add(dependencyPackage);
            }
        }
        for (final PsiPackage componentElement : component) {
            stronglyConnectedPackageComponents.put(componentElement, component);
        }
        return component;
    }

    @Override
    public int calculatePackageLevelOrder(PsiPackage aPackage) {
        final Integer out = packageLevelOrders.get(aPackage);
        if (out != null) {
            return out.intValue();
        }
        final Set<PsiPackage> dependentPackages = calculatePackageToPackageDependencies(aPackage);
        final Set<PsiPackage> mutuallyDependentPackages = calculateStronglyConnectedPackageComponents(aPackage);
        int levelOrder = 0;
        for (final PsiPackage dependentPackage : dependentPackages) {
            if (!mutuallyDependentPackages.contains(dependentPackage)) {
                final int dependentLevelOrder = calculatePackageLevelOrder(dependentPackage);
                levelOrder = Math.max(levelOrder, dependentLevelOrder);
            }
        }
        levelOrder += 1;
        for (final PsiPackage mutuallyDependentPackage : mutuallyDependentPackages) {
            packageLevelOrders.put(mutuallyDependentPackage, Integer.valueOf(levelOrder));
        }
        return levelOrder;
    }

    @Override
    public int calculatePackageAdjustedLevelOrder(PsiPackage aPackage) {
        final Integer out = packageAdjustedLevelOrders.get(aPackage);
        if (out != null) {
            return out.intValue();
        }
        final Set<PsiPackage> dependentPackages = calculatePackageToPackageDependencies(aPackage);
        final Set<PsiPackage> mutuallyDependentPackages = calculateStronglyConnectedPackageComponents(aPackage);
        int levelOrder = 0;
        for (final PsiPackage dependentPackage : dependentPackages) {
            if (!mutuallyDependentPackages.contains(dependentPackage)) {
                final int dependentLevelOrder = calculatePackageAdjustedLevelOrder(dependentPackage);
                levelOrder = Math.max(levelOrder, dependentLevelOrder);
            }
        }
        levelOrder += mutuallyDependentPackages.size();
        for (final PsiPackage stronglyConnectedPackage : mutuallyDependentPackages) {
            packageAdjustedLevelOrders.put(stronglyConnectedPackage, Integer.valueOf(levelOrder));
        }
        return levelOrder;
    }

    @Override
    public int getStrengthForDependency(PsiClass aClass, PsiClass dependencyClass) {
        final Bag<PsiClass> dependenciesForClass = dependencies.get(aClass);
        return dependenciesForClass.getCountForObject(dependencyClass);
    }

    @Override
    public int getStrengthForPackageDependency(PsiClass aClass, PsiPackage dependencyPackage) {
        final Bag<PsiPackage> dependenciesForClass = packageDependencies.get(aClass);
        return dependenciesForClass.getCountForObject(dependencyPackage);
    }

    @Override
    public Set<PsiPackage> calculatePackageToPackageDependencies(PsiPackage aPackage) {
        if (packageToPackageDependencies.containsKey(aPackage)) {
            final Bag<PsiPackage> dependenciesForPackage = packageToPackageDependencies.get(aPackage);
            return dependenciesForPackage.getContents();
        } else {
            return Collections.emptySet();
        }
    }

    public void build(PsiElement element) {
        final DependenciesVisitor visitor = new DependenciesVisitor();
        element.accept(visitor);
    }

    private class DependenciesVisitor extends JavaRecursiveElementVisitor {

        private final Stack<PsiClass> classStack = new Stack<>();
        private PsiClass currentClass = null;

        @Override
        public void visitClass(PsiClass aClass) {
            if (!ClassUtils.isAnonymous(aClass)) {
                classStack.push(currentClass);
                currentClass = aClass;
                addDependencyForTypes(aClass.getSuperTypes());
                addDependencyForTypeParameters(aClass.getTypeParameters());
            }
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass)) {
                currentClass = classStack.pop();
            }
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            addDependencyForClass(method.getContainingClass());
            addDependencyForTypes(expression.getTypeArguments());
        }

        @Override
        public void visitReferenceExpression(PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);
            final PsiElement element = expression.resolve();
            if (element == null) {
                return;
            }
            if (element instanceof PsiField) {
                final PsiField field = (PsiField) element;
                addDependencyForClass(field.getContainingClass());
            } else if (element instanceof PsiClass) {
                addDependencyForClass((PsiClass) element);
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            addDependencyForType(method.getReturnType());
            addDependencyForTypeParameters(method.getTypeParameters());
            final PsiReferenceList throwsList = method.getThrowsList();
            addDependencyForTypes(throwsList.getReferencedTypes());
        }

        @Override
        public void visitNewExpression(PsiNewExpression expression) {
            super.visitNewExpression(expression);
            addDependencyForType(expression.getType());
            addDependencyForTypes(expression.getTypeArguments());
        }

        @Override
        public void visitVariable(PsiVariable variable) {
            super.visitVariable(variable);
            addDependencyForType(variable.getType());
        }

        @Override
        public void visitClassObjectAccessExpression(PsiClassObjectAccessExpression exp) {
            super.visitClassObjectAccessExpression(exp);
            final PsiTypeElement operand = exp.getOperand();
            addDependencyForType(operand.getType());
        }

        @Override
        public void visitInstanceOfExpression(PsiInstanceOfExpression exp) {
            super.visitInstanceOfExpression(exp);
            final PsiTypeElement checkType = exp.getCheckType();
            if (checkType == null) {
                return;
            }
            addDependencyForType(checkType.getType());
        }

        @Override
        public void visitTypeCastExpression(PsiTypeCastExpression exp) {
            super.visitTypeCastExpression(exp);
            final PsiTypeElement castType = exp.getCastType();
            if (castType == null) {
                return;
            }
            addDependencyForType(castType.getType());
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
            super.visitLambdaExpression(expression);
            addDependencyForType(expression.getFunctionalInterfaceType());
        }

        private void addDependencyForTypeParameters(PsiTypeParameter[] parameters) {
            for (PsiTypeParameter parameter : parameters) {
                final PsiReferenceList extendsList = parameter.getExtendsList();
                addDependencyForTypes(extendsList.getReferencedTypes());
            }
        }

        private void addDependencyForTypes(PsiType[] types) {
            for (PsiType type : types) {
                addDependencyForType(type);
            }
        }

        private void addDependencyForType(@Nullable PsiType type) {
            if (type == null) {
                return;
            }
            final PsiType baseType = type.getDeepComponentType();
            if (!(baseType instanceof PsiClassType)) {
                if (baseType instanceof PsiWildcardType) {
                    final PsiWildcardType wildcardType = (PsiWildcardType) baseType;
                    addDependencyForType(wildcardType.getBound());
                }
                return;
            }
            final PsiClassType classType = (PsiClassType) baseType;
            addDependencyForTypes(classType.getParameters());
            addDependencyForClass(classType.resolve());
        }

        private void addDependencyForClass(PsiClass referencedClass) {
            if (currentClass == null || referencedClass == null || referencedClass.equals(currentClass)) {
                return;
            }
            if (referencedClass instanceof PsiCompiledElement || referencedClass instanceof PsiAnonymousClass ||
                    referencedClass instanceof PsiTypeParameter) {
                return;
            }
            add(currentClass, referencedClass, dependencies);
            add(referencedClass, currentClass, dependents);

            final PsiPackage dependencyPackage = ClassUtils.findPackage(referencedClass);
            if (dependencyPackage != null) {
                add(currentClass, dependencyPackage, packageDependencies);
            }

            final PsiPackage aPackage = ClassUtils.findPackage(currentClass);
            if (aPackage != null) {
                add(referencedClass, aPackage, packageDependents);
            }

            if (aPackage == null || dependencyPackage == null || aPackage.equals(dependencyPackage)) {
                return;
            }
            add(aPackage, dependencyPackage, packageToPackageDependencies);
            add(dependencyPackage, aPackage, packageToPackageDependents);
        }

        private <K, V> void add(K k, V v, Map<K, Bag<V>> map) {
            Bag<V> bag = map.get(k);
            if (bag == null) {
                bag = new Bag<>();
                map.put(k, bag);
            }
            bag.add(v);
        }
    }
}
