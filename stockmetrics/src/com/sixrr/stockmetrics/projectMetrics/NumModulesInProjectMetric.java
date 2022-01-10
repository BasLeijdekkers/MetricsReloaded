/*
 * Copyright 2005-2022 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.projectMetrics;

import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricType;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.projectCalculators.NumModulesInProjectCalculator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bas Leijdekkers
 */
public class NumModulesInProjectMetric extends ProjectMetric {
    @Override
    public @NotNull String getDisplayName() {
        return StockMetricsBundle.message("number.of.modules.display.name");
    }

    @Override
    public @NotNull String getAbbreviation() {
        return StockMetricsBundle.message("number.of.modules.abbreviation");
    }

    @Override
    public @NotNull MetricType getType() {
        return MetricType.Count;
    }

    @Override
    public @NotNull MetricCalculator createCalculator() {
        return new NumModulesInProjectCalculator(this);
    }
}
