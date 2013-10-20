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
import com.intellij.psi.javadoc.PsiDocComment;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class PercentFieldsJavadocedRecursivePackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numJavadocedFieldsPerPackage = new BucketedCount<PsiPackage>();
    private final BucketedCount<PsiPackage> numFieldsPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numFieldsPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numFields = numFieldsPerPackage.getBucketValue(aPackage);
            final int numJavadocedFields = numJavadocedFieldsPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, numJavadocedFields, numFields);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitField(PsiField field) {
            super.visitField(field);
            final PsiClass containingClass = field.getContainingClass();
            if (containingClass == null || ClassUtils.isAnonymous(containingClass)) {
                return;
            }

            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(containingClass);
            for (final PsiPackage aPackage : packages) {
                numFieldsPerPackage.createBucket(aPackage);
                if (field.getFirstChild()instanceof PsiDocComment) {
                    numJavadocedFieldsPerPackage.incrementBucketValue(aPackage);
                }
                numFieldsPerPackage.incrementBucketValue(aPackage);
            }
        }
    }
}
