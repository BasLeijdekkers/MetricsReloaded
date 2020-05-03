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
import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;

public abstract class ModuleCalculator extends BaseMetricsCalculator {

    public ModuleCalculator(Metric metric) {
        super(metric);
    }

    void postMetric(Module module, int numerator, int denominator) {
        resultsHolder.postModuleMetric(metric, module, (double) numerator, (double) denominator);
    }

    void postMetric(Module module, int value) {
        resultsHolder.postModuleMetric(metric, module, (double) value);
    }
}
