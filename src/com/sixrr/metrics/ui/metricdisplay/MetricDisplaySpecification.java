/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XMap;
import com.sixrr.metrics.MetricCategory;

import java.util.EnumMap;
import java.util.Map;

@Tag("metrics_layout")
public final class MetricDisplaySpecification {
    @XMap(propertyElementName = "tables", keyAttributeName = "category", entryTagName = "table")
    private final Map<MetricCategory, MetricTableSpecification> specs = new EnumMap<>(MetricCategory.class);

    public MetricTableSpecification getSpecification(MetricCategory category) {
        return specs.computeIfAbsent(category, c -> new MetricTableSpecification());
    }
}
