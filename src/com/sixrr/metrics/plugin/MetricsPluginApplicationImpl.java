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

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ExportableApplicationComponent;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * Allows for exporting metrics profiles.
 * <p/>
 * <pre>
 * File / Export settings...
 * </pre>
 * <p/>
 * The metrics configuration files are located within <code>.IntellijIDEA50/config/metrics/...</code>
 */
public class MetricsPluginApplicationImpl
        implements ExportableApplicationComponent, MetricsPluginApplication {

    public File[] getExportFiles() {
        @NonNls final String dirName =
                PathManager.getConfigPath() + File.separator + "metrics";
        final File metricsDirectory = new File(dirName);
        final File[] files = metricsDirectory.listFiles();
        final File[] out = new File[files.length + 1];
        out[0] = metricsDirectory;
        System.arraycopy(files, 0, out, 1, files.length);
        return out;
    }

    public String getPresentableName() {
        return MetricsReloadedBundle.message("metrics.profiles.configuration.presentable.name");
    }

    public String getComponentName() {
        return "MetricsReloaded";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
