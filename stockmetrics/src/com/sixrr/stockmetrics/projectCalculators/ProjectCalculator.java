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

package com.sixrr.stockmetrics.projectCalculators;

import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;

public abstract class ProjectCalculator extends BaseMetricsCalculator {

    public ProjectCalculator(Metric metric) {
        super(metric);
    }

    void postMetric(int numerator, int denominator) {
        resultsHolder.postProjectMetric(metric, (double) numerator, (double) denominator);
    }

    void postMetric(int value) {
        resultsHolder.postProjectMetric(metric, (double) value);
    }
}
