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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

/**
 * @author Bas Leijdekkers
 */
public abstract class ElementCountPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> elementCountPerPackage = new BucketedCount<>();

    public ElementCountPackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public final void endMetricsRun() {
        final Set<PsiPackage> packages = elementCountPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int count = elementCountPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, count);
        }
    }

    protected void createCount(PsiElement element) {
        final PsiPackage aPackage = ClassUtils.findPackage(element);
        if (aPackage == null) {
            return;
        }
        elementCountPerPackage.createBucket(aPackage);
    }

    protected void createCountRecursive(PsiElement element) {
        final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(element);
        for (PsiPackage aPackage : packages) {
            elementCountPerPackage.createBucket(aPackage);
        }
    }

    protected void incrementCount(PsiElement element, int count) {
        final PsiPackage aPackage = ClassUtils.findPackage(element);
        if (aPackage == null) {
            return;
        }
        elementCountPerPackage.incrementBucketValue(aPackage, count);
    }

    protected void incrementCountRecursive(PsiElement element, int count) {
        final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(element);
        for (PsiPackage aPackage : packages) {
            elementCountPerPackage.incrementBucketValue(aPackage, count);
        }
    }
}
