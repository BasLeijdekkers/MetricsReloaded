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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;

import java.util.Set;

public class NumTestMethodsPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numTestMethodsPerPackages = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numTestMethodsPerPackages.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numTestMethods = numTestMethodsPerPackages.getBucketValue(aPackage);

            postMetric(aPackage, numTestMethods);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            if (aPackage == null) {
                return;
            }
            numTestMethodsPerPackages.createBucket(aPackage);
        }

        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiClass aClass = method.getContainingClass();
            if (!TestUtils.isJUnitTestMethod(method)) {
                return;
            }
            final PsiPackage aPackage = ClassUtils.findPackage(aClass);
            if (aPackage == null) {
                return;
            }
            numTestMethodsPerPackages.incrementBucketValue(aPackage, 1);
        }
    }
}
