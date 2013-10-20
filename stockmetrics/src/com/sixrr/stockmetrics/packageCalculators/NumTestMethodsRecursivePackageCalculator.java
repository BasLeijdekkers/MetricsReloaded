/*
 * Copyright 2005-2013 Sixth and Red River Software, Bas Leijdekkers
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
import com.sixrr.metrics.utils.TestUtils;

import java.util.Set;

public class NumTestMethodsRecursivePackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numTestMethodsPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numTestMethodsPerPackage.getBuckets();
        for (final PsiPackage packageName : packages) {
            final int numCommentLines = numTestMethodsPerPackage.getBucketValue(packageName);
            postMetric(packageName, (double) numCommentLines);
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
                numTestMethodsPerPackage.createBucket(aPackage);
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiClass aClass = method.getContainingClass();
            if (TestUtils.isJUnitTestMethod(method)) {
                final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(aClass);
                for (PsiPackage aPackage : packages) {
                    numTestMethodsPerPackage.incrementBucketValue(aPackage, 1);
                }
            }
        }
    }
}
