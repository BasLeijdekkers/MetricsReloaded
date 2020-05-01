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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiPackage;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.dependency.DependencyMap;

import java.util.Set;

public class EfferentCouplingCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numExternalDependenciesPerPackage = new BucketedCount<>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numExternalDependenciesPerPackage.getBuckets();
        for (PsiPackage aPackage : packages) {
            final int numExternalDependencies = numExternalDependenciesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numExternalDependencies);
        }
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
            final PsiPackage referencingPackage = ClassUtils.findPackage(aClass);
            if (referencingPackage == null) {
                return;
            }
            numExternalDependenciesPerPackage.createBucket(referencingPackage);
            final DependencyMap dependencyMap = getDependencyMap();
            final Set<PsiPackage> packageDependencies = dependencyMap.calculatePackageDependencies(aClass);
            for (PsiPackage referencedPackage : packageDependencies) {
                if (referencedPackage == referencingPackage) {
                    // skip internal references
                    continue;
                }
                final int strength = dependencyMap.getStrengthForPackageDependency(aClass, referencedPackage);
                numExternalDependenciesPerPackage.incrementBucketValue(referencingPackage, strength);
            }
        }
    }
}
