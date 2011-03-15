/*
 * Copyright 2005-2011, Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.dialogs.ThresholdDialog;
import com.sixrr.metrics.utils.IconHelper;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class EditThresholdsAction extends AnAction {

    private static final Icon EDIT_ICON = IconHelper.getIcon("/actions/properties.png");
    private final MetricsToolWindow toolWindow;

    EditThresholdsAction(MetricsToolWindow toolWindow) {
        super(MetricsReloadedBundle.message("edit.thresholds.action"),
                MetricsReloadedBundle.message("edit.threshold.values.for.this.metric.profile"),
                EDIT_ICON);
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        assert project != null;
        final MetricCategory category = toolWindow.getSelectedCategory();
        final MetricsProfile profile = toolWindow.getCurrentProfile();
        final List<MetricInstance> metrics = new ArrayList<MetricInstance>();
        for (MetricInstance instance : profile.getMetrics()) {
            if (!instance.isEnabled()) {
                continue;
            }
            final Metric metric = instance.getMetric();
            if (metric.getCategory() != category) {
                continue;
            }
            try {
                metrics.add(instance.clone());
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
        final MetricsResult results = toolWindow.getCurrentRun().getResultsForCategory(category);
        final ThresholdDialog dialog =
                new ThresholdDialog(project, profile.getName(), metrics, results);
        dialog.show();
        if (dialog.isOK()) {
            profile.copyFrom(metrics);
            MetricsProfileRepository.persistProfile(profile);
        }
    }
}
