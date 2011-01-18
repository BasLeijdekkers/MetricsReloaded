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

package com.sixrr.metrics;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * To add metrics to IntellIJ IDEA, you first create the Metric classes for them, and then bind them into IDEA using a
 * MetricsProvider component.  Create an implementation of MetricsProvider which lists the metrics classes you wish to add.
 * Additionally, you can specify a list of pre-built metrics profiles, which may make it easier for new users to use your
 * metrics.
 */
public interface MetricProvider extends ApplicationComponent {
    /**
     * Returns the list of metrics classes provided by this provider.
     * @return the metrics classes for this provider
     */
    @NotNull
    List<Class<? extends Metric>> getMetricClasses();

    /**
     * Returns the list of prebuilt metrics profiles provided by this provider.
     * @return the prebuild metrics profiles for this provider.
     */
    @NotNull
    List<PrebuiltMetricProfile> getPrebuiltProfiles();
}
