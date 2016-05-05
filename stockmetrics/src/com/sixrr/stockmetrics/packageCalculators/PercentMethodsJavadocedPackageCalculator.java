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

public class PercentMethodsJavadocedPackageCalculator extends PackageCalculator {
    
    private final BucketedCount<PsiPackage> numJavadocedMethodsPerPackage = new BucketedCount<PsiPackage>();
    private final BucketedCount<PsiPackage> numMethodsPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numMethodsPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numMethods = numMethodsPerPackage.getBucketValue(aPackage);
            final int numJavadocedMethods = numJavadocedMethodsPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, numJavadocedMethods, numMethods);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        
        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null || ClassUtils.isAnonymous(containingClass)) {
                return;
            }
            final PsiPackage aPackage = ClassUtils.findPackage(containingClass);
            if (aPackage == null) {
                return;
            }
            numMethodsPerPackage.createBucket(aPackage);
            if (method.getDocComment() != null) {
                numJavadocedMethodsPerPackage.incrementBucketValue(aPackage);
            }
            numMethodsPerPackage.incrementBucketValue(aPackage);
        }
    }
}
