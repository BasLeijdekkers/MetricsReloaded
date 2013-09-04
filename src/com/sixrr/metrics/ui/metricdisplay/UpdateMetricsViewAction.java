/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.metricModel.TimeStamp;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.*;

class UpdateMetricsViewAction extends AnAction {

    private static final Icon ICON = AllIcons.Actions.Refresh;

    private final MetricsToolWindow toolWindow;
    private final Project project;

    UpdateMetricsViewAction(MetricsToolWindow toolWindow, Project project) {
        super(MetricsReloadedBundle.message("update.metrics.action"),
                MetricsReloadedBundle.message("update.metrics.description"), ICON);
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final AnalysisScope currentScope = toolWindow.getCurrentScope();
        final MetricsProfile currentProfile = toolWindow.getCurrentProfile();
        final MetricsRunImpl metricsRun = new MetricsRunImpl();
        new MetricsExecutionContextImpl(project, currentScope) {
            
            @Override
            public void onFinish() {
                metricsRun.setProfileName(currentProfile.getName());
                metricsRun.setContext(currentScope);
                metricsRun.setTimestamp(new TimeStamp());
                toolWindow.update(metricsRun);
            }
        }.execute(currentProfile, metricsRun);
    }
}
