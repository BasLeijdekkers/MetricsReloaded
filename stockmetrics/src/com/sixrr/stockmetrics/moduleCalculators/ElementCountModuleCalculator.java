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
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public abstract class ElementCountModuleCalculator extends ModuleCalculator {

    private final BucketedCount<Module> elementCountPerModule = new BucketedCount<>();

    public ElementCountModuleCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public final void endMetricsRun() {
        final Set<Module> modules = elementCountPerModule.getBuckets();
        for (final Module module : modules) {
            final int count = elementCountPerModule.getBucketValue(module);
            postMetric(module, count);
        }
    }

    protected void createCount(PsiElement element) {
        final Module module = ClassUtils.calculateModule(element);
        if (module == null) {
            return;
        }
        elementCountPerModule.createBucket(module);
    }

    protected void incrementCount(PsiElement element, int count) {
        final Module module = ClassUtils.calculateModule(element);
        if (module == null) {
            return;
        }
        elementCountPerModule.incrementBucketValue(module, count);
    }
}
