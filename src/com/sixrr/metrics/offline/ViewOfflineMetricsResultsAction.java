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

package com.sixrr.metrics.offline;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.profile.*;
import com.sixrr.metrics.ui.metricdisplay.MetricsView;
import com.sixrr.metrics.ui.metricdisplay.SnapshotFileFilter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewOfflineMetricsResultsAction extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            return;
        }

        final JFileChooser chooser = new JFileChooser();
        final FileFilter filter = new SnapshotFileFilter();
        chooser.setFileFilter(filter);
        final WindowManager myWindowManager = WindowManager.getInstance();
        final Window parent = myWindowManager.suggestParentWindow(project);
        final int returnVal = chooser.showOpenDialog(parent);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final File selectedFile = chooser.getSelectedFile();
        final MetricsRun results = MetricsRunImpl.readFromFile(selectedFile);
        if (results == null) {
            return;
        }
        final MetricsView toolWindow = new MetricsView(project);
        final MetricsProfileRepository repository = MetricsProfileRepository.getInstance();
        final String profileName = results.getProfileName();
        MetricsProfile profile = repository.getProfileByName(profileName);
        if (profile == null) {
            final List<Metric> metrics = results.getMetrics();
            final List<MetricInstance> instances = new ArrayList<>();
            for (Metric metric : metrics) {
                final MetricInstance metricInstance = new MetricInstanceImpl(metric);
                metricInstance.setEnabled(true);
                instances.add(metricInstance);
            }
            profile = new MetricsProfileImpl(profileName, instances);
            repository.addProfile(profile);
        }
        toolWindow.show(results, profile, null, false); //TODO
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        final Presentation presentation = event.getPresentation();
        final DataContext dataContext = event.getDataContext();
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        presentation.setEnabled(project != null);
    }
}
