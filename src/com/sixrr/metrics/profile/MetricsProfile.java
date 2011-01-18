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

package com.sixrr.metrics.profile;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricInstance;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public interface MetricsProfile extends Cloneable {
    void setName(String newProfileName);

    String getName();

    List<MetricInstance> getMetrics();

    void replaceMetrics(List<MetricInstance> newMetrics);

    @Nullable MetricInstance getMetricForClass(Class<? extends Metric> aClass);

    @Nullable MetricInstance getMetricForName(String metricName);

    void writeToFile(File profileFile);

    MetricDisplaySpecification getDisplaySpecification();
}
