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

package com.sixrr.metrics.metricModel;

import com.sixrr.metrics.MetricCategory;

import java.util.Comparator;

public class MetricComparator implements Comparator<MetricInstance> {
    public int compare(MetricInstance metric1, MetricInstance metric2) {
        final MetricCategory category1 = metric1.getMetric().getCategory();
        final MetricCategory category2 = metric2.getMetric().getCategory();
        final int categoryComparison = category1.compareTo(category2);
        if (categoryComparison != 0) {
            return -categoryComparison;
        }
        final String displayName1 = metric1.getMetric().getDisplayName();
        final String displayName2 = metric2.getMetric().getDisplayName();
        return displayName1.compareTo(displayName2);
    }
}
