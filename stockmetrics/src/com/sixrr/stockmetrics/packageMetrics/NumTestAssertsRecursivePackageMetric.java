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

package com.sixrr.stockmetrics.packageMetrics;

import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricType;
import com.sixrr.stockmetrics.i18n.HelpURLs;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.packageCalculators.NumTestAssertsRecursivePackageCalculator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NumTestAssertsRecursivePackageMetric extends PackageMetric {

    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.junit.test.assertions.recursive.display.name");
    }

    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.junit.test.assertions.recursive.abbreviation");
    }

    public MetricType getType() {
        return MetricType.RecursiveCount;
    }

    public String getHelpURL() {
        return HelpURLs.JUNIT_URL;
    }

    @Nullable
    public String getHelpDisplayString() {
        return HelpURLs.JUNIT_DISPLAY_STRING;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new NumTestAssertsRecursivePackageCalculator();
    }
}
