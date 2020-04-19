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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public abstract class ElementRatioModuleCalculator extends ModuleCalculator {

    private final BucketedCount<Module> numeratorPerModule = new BucketedCount<>();
    private final BucketedCount<Module> denominatorPerModule = new BucketedCount<>();

    @Override
    public void endMetricsRun() {
        final Set<Module> modules = numeratorPerModule.getBuckets();
        for (final Module module : modules) {
            final int numerator = numeratorPerModule.getBucketValue(module);
            final int denominator = denominatorPerModule.getBucketValue(module);

            if (denominator == 0) {
                postMetric(module, 0);
            } else {
                postMetric(module, numerator, denominator);
            }
        }
    }

    protected void createRatio(PsiElement element) {
        final Module module = ClassUtils.calculateModule(element);
        if (module == null) {
            return;
        }
        numeratorPerModule.createBucket(module);
        denominatorPerModule.createBucket(module);
    }

    protected void incrementNumerator(PsiElement element, int count) {
        increment(element, count, numeratorPerModule);
    }

    protected void incrementDenominator(PsiElement element, int count) {
        increment(element, count, denominatorPerModule);
    }

    private static void increment(PsiElement element, int count, BucketedCount<Module> result) {
        final Module module = ClassUtils.calculateModule(element);
        if (module == null) {
            return;
        }
        result.incrementBucketValue(module, count);
    }
}
