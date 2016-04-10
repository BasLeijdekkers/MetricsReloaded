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

package com.sixrr.metrics.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(name = "MetricsReloaded", storages = @Storage(file = "metrics.reloaded.xml"))
public final class MetricsReloadedConfig implements PersistentStateComponent<MetricsReloadedConfig> {

    public String selectedProfile = "";
    public boolean autoscroll = false;
    public boolean showOnlyWarnings = false;

    private MetricsReloadedConfig() {}

    public static MetricsReloadedConfig getInstance() {
        return ServiceManager.getService(MetricsReloadedConfig.class);
    }

    public String getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(String selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public boolean isAutoscroll() {
        return autoscroll;
    }

    public void setAutoscroll(boolean autoscroll) {
        this.autoscroll = autoscroll;
    }

    public boolean isShowOnlyWarnings() {
        return showOnlyWarnings;
    }

    public void setShowOnlyWarnings(boolean showOnlyWarnings) {
        this.showOnlyWarnings = showOnlyWarnings;
    }

    @Nullable
    @Override
    public MetricsReloadedConfig getState() {
        return this;
    }

    @Override
    public void loadState(MetricsReloadedConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
