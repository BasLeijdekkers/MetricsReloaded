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

package com.sixrr.metrics.ui;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;

import java.text.NumberFormat;

public final class FormatUtils {

    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
    private static final NumberFormat intFormatter = NumberFormat.getIntegerInstance();

    static {
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setMinimumFractionDigits(2);
    }

    private FormatUtils() {}

    public static String formatValue(Metric metric, Double value) {
        return formatValue(metric, value, false);
    }

    public static String formatValue(Metric metric, Double value, boolean average) {
        if (value == null) {
            return "";
        }
        final MetricType metricType = metric.getType();
        if (metricType == MetricType.Count || metricType == MetricType.Score ||
                metricType == MetricType.RecursiveCount) {
            return average ? numberFormatter.format(value.doubleValue()) : intFormatter.format(value.longValue());
        }  else if(metricType == MetricType.Average) {
            return numberFormatter.format(value) ;
        } else { //it's a ratio or recursive ratio
            return numberFormatter.format(value.doubleValue() * 100.0) + '%';
        }
    }
}
