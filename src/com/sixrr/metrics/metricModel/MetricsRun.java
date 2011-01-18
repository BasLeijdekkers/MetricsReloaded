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

import com.intellij.analysis.AnalysisScope;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.MetricsResultsHolder;
import com.sixrr.metrics.profile.MetricsProfile;
import org.jetbrains.annotations.NonNls;

import java.util.List;

public interface MetricsRun extends MetricsResultsHolder {

    List<Metric> getMetrics();

    MetricsResult getResultsForCategory(MetricCategory category);

    void writeToFile(@NonNls String fileName);

    String getProfileName();

    TimeStamp getTimestamp();

    AnalysisScope getContext();

    MetricsRun filterRowsWithoutWarnings(MetricsProfile profile);
}
