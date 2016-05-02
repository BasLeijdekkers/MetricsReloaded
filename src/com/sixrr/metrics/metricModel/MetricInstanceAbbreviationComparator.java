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

package com.sixrr.metrics.metricModel;

import com.sixrr.metrics.profile.MetricInstance;

import java.util.Comparator;

public class MetricInstanceAbbreviationComparator implements Comparator<MetricInstance> {
    @Override
    public int compare(MetricInstance o1, MetricInstance o2) {
        final String abbrev1 = o1.getMetric().getAbbreviation();
        final String upperAbbrev1 = abbrev1.toUpperCase();
        final String abbrev2 = o2.getMetric().getAbbreviation();
        final String upperAbbrev2 = abbrev2.toUpperCase();
        final int caseInsensitiveCompare = upperAbbrev1.compareTo(upperAbbrev2);
        if (caseInsensitiveCompare != 0) {
            return caseInsensitiveCompare;
        }
        return abbrev1.compareTo(abbrev2);
    }
}
