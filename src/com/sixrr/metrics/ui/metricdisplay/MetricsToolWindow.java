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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.profile.MetricsProfile;
import org.jetbrains.annotations.NonNls;

public abstract class MetricsToolWindow implements Disposable {

    @NonNls
    public static final String TOOL_WINDOW_ICON_PATH = "/images/metricsToolWindow.png";
    @NonNls
    public static final String METRICS_TOOL_WINDOW_ID = "Metrics";

    public static MetricsToolWindow getInstance(Project project) {
        return ServiceManager.getService(project, MetricsToolWindow.class);
    }

    public abstract void show(MetricsRun results, MetricsProfile profile, AnalysisScope scope, boolean showOnlyWarnings);

    public abstract void update(MetricsRun results);

    public abstract void updateWithDiff(MetricsRun results);

    public abstract void reloadAsDiff(MetricsRun prevResults);

    public abstract void removeDiffOverlay();

    public abstract boolean hasDiffOverlay();

    public abstract void close();

    public abstract MetricsRun getCurrentRun();

    public abstract AnalysisScope getCurrentScope();

    public abstract MetricsProfile getCurrentProfile();

    public abstract MetricCategory getSelectedCategory();
}
