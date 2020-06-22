/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * To add a new metric to IntelliJ IDEA, create a Metric class for it, which specifies both all the "metadata" for a metric,
 * and tells how a calculator for the metric may be created.
 */
public interface Metric {

    Metric[] EMPTY_ARRAY = {};

    /**
     * The id of the metric.  This is a non-user visible string which will be used a key for managing metrics internal
     * to MetricsReloaded.  It should be unique over the space of all defined metrics.
     * @return the metric id
     */
    @NonNls
    @NotNull
    String getID();

    /**
     * The user-visible name of the metric.  This need not be unique globally, but should be unique within a metric category
     * @return the display name for the metric.
     */
    @NotNull
    String getDisplayName();

    /**
     * The user-visible abbreviation of the metric.  This need not be unique globally, but should be unique within a metric category
     * @return the abbreviation for the metric.
     */
    @NotNull
    String getAbbreviation();

    /**
     * The category for this metric, indicating what objects are measured (classes, methods, modules)
     * @return  the metric category
     */
    @NotNull
    MetricCategory getCategory();

    /**
     * The type of the metric, indicating whether the number returned is a score, a count, or an average.
     * @return the metric type
     */
    @NotNull
    MetricType getType();

    /**
     * A URL directing the user to further information on the metric.  The user will be directed to the URL if they click on the
     * "For more information" label.
     *
     * @return an online help URL, or null if no help is available.
     * @deprecated no longer used. Provide any additional inforation as regular links inside the description
     */
    @Deprecated
    @NonNls
    @Nullable
    String getHelpURL();

    /**
     * A user-visible text fragment directing the user to further information on the metric.  This will be the text displayed in the
     * "For more information" label
     * @return a string describing any online help available, or null if no help is available
     * @deprecated no longer used. Provide any additional inforation as regular links inside the description
     */
    @Deprecated
    @Nullable
    String getHelpDisplayString();


    /**
     * Create a calculator for this method.  The calculator returned is used for the duration of one entire metrics run.
     * @return a calculator for this metric.
     */
    @NotNull
    MetricCalculator createCalculator();

    /**
     * @return true, if this metric requires the dependency map. false otherwise.
     */
    boolean requiresDependents();
}
