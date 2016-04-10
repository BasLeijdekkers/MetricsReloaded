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

package com.sixrr.metrics.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.metricdisplay.MetricsToolWindow;
import com.sixrr.metrics.ui.metricdisplay.MetricsToolWindowImpl;
import org.jetbrains.annotations.NotNull;

public class MetricsPluginImpl implements ProjectComponent, MetricsPlugin {
    
    private MetricsProfileRepository profileRepository = null;
    private final Project project;
    private MetricsToolWindow metricsToolWindow = null;

    public MetricsPluginImpl(Project project) {
        this.project = project;
    }

    @Override
    @NotNull public String getComponentName() {
        return "com.sixrr.metrics.MetricsReloaded";
    }

    @Override
    public void initComponent() {}

    @Override
    public void disposeComponent() {}

    @Override
    public void projectOpened() {
        metricsToolWindow = new MetricsToolWindowImpl(project, this);
        metricsToolWindow.register();
    }

    @Override
    public void projectClosed() {
        metricsToolWindow.unregister();
    }

    @Override
    public MetricsToolWindow getMetricsToolWindow() {
        return metricsToolWindow;
    }

    @Override
    public MetricsProfileRepository getProfileRepository() {
        if (profileRepository == null) {
            profileRepository = new MetricsProfileRepository();
            profileRepository.initialize();
        }
        return profileRepository;
    }
}
