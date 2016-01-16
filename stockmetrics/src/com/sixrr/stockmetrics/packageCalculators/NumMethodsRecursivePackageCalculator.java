/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

import java.util.Set;

public class NumMethodsRecursivePackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numMethodsPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numMethodsPerPackage.getBuckets();
        for (final PsiPackage packageName : packages) {
            final int numClasses = numMethodsPerPackage.getBucketValue(packageName);
            postMetric(packageName, numClasses);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(file);
            for (PsiPackage aPackage : packages) {
                numMethodsPerPackage.createBucket(aPackage);
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(method);
            for (final PsiPackage aPackage : packages) {
                numMethodsPerPackage.incrementBucketValue(aPackage);
            }
        }
    }
}
