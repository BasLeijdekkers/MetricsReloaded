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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.dependency.DependencyMap;
import com.sixrr.stockmetrics.dependency.DependentsMap;

import java.util.Set;

public class DistanceCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numClassesPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numAbstractClassesPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numExternalDependentsPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numExternalDependenciesPerPackage = new BucketedCount<>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numExternalDependentsPerPackage.getBuckets();
        for (PsiPackage aPackage : packages) {
            final double numClasses = (double) numClassesPerPackage.getBucketValue(aPackage);
            final double numAbstractClasses = (double) numAbstractClassesPerPackage.getBucketValue(aPackage);
            final double numExternalDependents = (double) numExternalDependentsPerPackage.getBucketValue(aPackage);
            final double numExternalDependencies = (double) numExternalDependenciesPerPackage.getBucketValue(aPackage);
            final double instability =
                    calculateFraction(numExternalDependencies, numExternalDependencies + numExternalDependents);
            final double abstractness = calculateFraction(numAbstractClasses, numClasses);
            final double distance = Math.abs(1.0 - instability - abstractness);
            postMetric(aPackage, distance);
        }
    }

    private static double calculateFraction(double numerator, double denominator) {
        if (denominator == 0.0) {
            return 1.0;
        }
        return numerator / denominator;
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (ClassUtils.isAnonymous(aClass)) {
                return;
            }
            final PsiPackage currentPackage = ClassUtils.findPackage(aClass);
            if (currentPackage == null) {
                return;
            }
            if (aClass.isInterface() || aClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                numAbstractClassesPerPackage.incrementBucketValue(currentPackage);
            }
            numClassesPerPackage.incrementBucketValue(currentPackage);
            numExternalDependentsPerPackage.createBucket(currentPackage);
            final DependentsMap dependentsMap = getDependentsMap();
            final Set<PsiPackage> packageDependents = dependentsMap.calculatePackageDependents(aClass);
            numExternalDependentsPerPackage.incrementBucketValue(currentPackage, packageDependents.size());
            numExternalDependenciesPerPackage.createBucket(currentPackage);
            final DependencyMap dependencyMap = getDependencyMap();
            final Set<PsiPackage> packageDependencies = dependencyMap.calculatePackageDependencies(aClass);
            numExternalDependenciesPerPackage.incrementBucketValue(currentPackage, packageDependencies.size());
        }
    }
}
