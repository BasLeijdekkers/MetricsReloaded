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
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class PercentFieldsJavadocedPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numJavadocedFieldsPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numFieldsPerPackage = new BucketedCount<>();

    public PercentFieldsJavadocedPackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numFieldsPerPackage.getBuckets();
        for (final PsiPackage packageName : packages) {
            final int numFields = numFieldsPerPackage.getBucketValue(packageName);
            final int numJavadocedFields = numJavadocedFieldsPerPackage.getBucketValue(packageName);

            postMetric(packageName, numJavadocedFields, numFields);
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
            final PsiPackage aPackage = ClassUtils.findPackage(containingClass);
            if (aPackage == null) {
                return;
            }
            numFieldsPerPackage.createBucket(aPackage);
            if (field.getDocComment() != null) {
                numJavadocedFieldsPerPackage.incrementBucketValue(aPackage);
            }
            numFieldsPerPackage.incrementBucketValue(aPackage);
        }
    }
}
