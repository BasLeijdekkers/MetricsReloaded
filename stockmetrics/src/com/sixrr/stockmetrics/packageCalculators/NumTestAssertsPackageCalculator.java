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
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;

import java.util.Set;

public class NumTestAssertsPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numTestAssertsPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numTestAssertsPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numTestMethods = numTestAssertsPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, (double) numTestMethods);
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
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            if (aPackage == null) {
                return;
            }
            numTestAssertsPerPackage.createBucket(aPackage);
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (!TestUtils.isJUnitAssertCall(expression)) {
                return;
            }
            final PsiClass aClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            final PsiPackage aPackage = ClassUtils.findPackage(aClass);
            if (aPackage == null) {
                return;
            }
            numTestAssertsPerPackage.incrementBucketValue(aPackage, 1);
        }
    }
}
