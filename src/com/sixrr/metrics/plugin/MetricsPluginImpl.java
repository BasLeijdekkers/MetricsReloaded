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

package com.sixrr.metrics.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.metricdisplay.MetricsToolWindow;
import com.sixrr.metrics.ui.metricdisplay.MetricsToolWindowImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class MetricsPluginImpl implements ProjectComponent, MetricsPlugin, JDOMExternalizable {
    private MetricsProfileRepository profileRepository = null;
    private final MetricsReloadedConfig config = new MetricsReloadedConfig();
    private final Project project;
    private MetricsToolWindow metricsToolWindow = null;

    public MetricsPluginImpl(Project project) {
        super();
        this.project = project;
    }

    @NotNull public String getComponentName() {
        return "com.sixrr.metrics.MetricsReloaded";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public void projectOpened() {
        profileRepository = new MetricsProfileRepository(config);
        profileRepository.initialize();
        metricsToolWindow = new MetricsToolWindowImpl(project, profileRepository, config);
        metricsToolWindow.register();
    }

    public void projectClosed() {
        metricsToolWindow.unregister();
    }

    public void readExternal(Element element) throws InvalidDataException {
        config.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        config.writeExternal(element);
    }

    public MetricsReloadedConfig getConfiguration() {
        return config;
    }


    public MetricsToolWindow getMetricsToolWindow() {
        return metricsToolWindow;
    }

    public MetricsProfileRepository getProfileRepository() {
        return profileRepository;
    }
}
