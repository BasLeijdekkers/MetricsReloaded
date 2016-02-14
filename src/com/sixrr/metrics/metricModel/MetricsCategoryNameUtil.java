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

package com.sixrr.metrics.metricModel;

import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NotNull;

public final class MetricsCategoryNameUtil {

    private MetricsCategoryNameUtil() {}

    public static String getLongNameForCategory(@NotNull MetricCategory category) {
        switch (category) {
            case Class:
                return MetricsReloadedBundle.message("class.metrics.long.name");
            case Interface:
                return MetricsReloadedBundle.message("interface.metrics.long.name");
            case Method:
                return MetricsReloadedBundle.message("method.metrics.long.name");
            case Module:
                return MetricsReloadedBundle.message("module.metrics.long.name");
            case Package:
                return MetricsReloadedBundle.message("package.metrics.long.name");
            case FileType:
                return MetricsReloadedBundle.message("file.type.metrics.long.name");
            case Project:
                return MetricsReloadedBundle.message("project.metrics.long.name");
            default:
                throw new AssertionError("unknown category: " + category);
        }
    }

    public static String getShortNameForCategory(@NotNull MetricCategory category) {
        switch (category) {
            case Class:
                return MetricsReloadedBundle.message("class.metrics.short.name");
            case Interface:
                return MetricsReloadedBundle.message("interface.metrics.short.name");
            case Method:
                return MetricsReloadedBundle.message("method.metrics.short.name");
            case Module:
                return MetricsReloadedBundle.message("module.metrics.short.name");
            case Package:
                return MetricsReloadedBundle.message("package.metrics.short.name");
            case FileType:
                return MetricsReloadedBundle.message("file.type.metrics.short.name");
            case Project:
                return MetricsReloadedBundle.message("project.metrics.short.name");
            default:
                throw new AssertionError("unknown category: " + category);
        }
    }
}
