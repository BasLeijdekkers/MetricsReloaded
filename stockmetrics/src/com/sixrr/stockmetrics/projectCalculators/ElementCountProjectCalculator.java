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

public abstract class ElementCountProjectCalculator extends ProjectCalculator {

    private int numElements = 0;

    public ElementCountProjectCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public final void endMetricsRun() {
        postMetric(numElements);
    }

    protected void incrementCount(int count) {
        numElements += count;
    }
}
