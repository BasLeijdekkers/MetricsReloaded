/*
 * Copyright 2005, Sixth and Red River Software
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

public class FormatUtils {
    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

    static {
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setMinimumFractionDigits(2);
    }

    public static String formatValue(Metric metric, Double value) {
        if (value == null) {
            return "";
        }
        final MetricType metricType = metric.getType();
        if (metricType.equals(MetricType.Count) || metricType.equals(MetricType.Score) ||
                metricType.equals(MetricType.RecursiveCount)) {
            final int intValue = value.intValue();
            return Integer.toString(intValue);
        }  else if(metricType.equals(MetricType.Average))
        {
            return numberFormatter.format(value) ;
        } else //it's a ratio or recursive ratio
        {
            return numberFormatter.format(value * 100.0) + '%';
        }
    }

    public static String formatAverageValue(Metric metric, Double value) {
        if (value == null) {
            return "";
        }
        final MetricType metricType = metric.getType();
        if (metricType.equals(MetricType.Count) || metricType.equals(MetricType.Score) ) {
            return numberFormatter.format((double) value);
        } else if (metricType.equals(MetricType.Average)) {
            return numberFormatter.format(value);
        }else //it's a ratio
        {
            return numberFormatter.format(value * 100.0) + '%';
        }
    }
}
