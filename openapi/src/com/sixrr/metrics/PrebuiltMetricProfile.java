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

import java.util.*;

/**
 * The PrebuiltMetricProfile class represents a pre-build set of metrics and thresholds which a metrics plugin may install
 * into IntelliJ IDEA.  Prebuilt profiles may make it easier for end-users to learn the metrics available, and how they
 * interact.
 */
public class PrebuiltMetricProfile {
    private final String profileName;
    private final Set<String> metricNames = new HashSet<>();
    private final Map<String, Double> lowerThresholds = new HashMap<>();
    private final Map<String, Double> upperThresholds = new HashMap<>();

    /**
     * Create a prebuilt metric profile.
     *
     * @param profileName the name of the profile.
     */
    public PrebuiltMetricProfile(@NotNull String profileName) {
        this.profileName = profileName;
    }

    public void addMetric(@NotNull Class<? extends Metric> metricClass) {
        addMetric(metricClass, null, null);
    }

    public void addMetric(@NotNull Class<? extends Metric> metricClass, Double lowerThreshold, Double upperThreshold) {
        addMetric(calculateName(metricClass), lowerThreshold, upperThreshold);
    }

    /**
     * Add a metric to the profile, with no upper or lower threshold specified.
     * This is equivalent to add(metricID, null, null)
     *
     * @param metricID the ID of the metric to add.
     */
    public void addMetric(@NotNull @NonNls String metricID) {
        addMetric(metricID, null, null);
    }

    /**
     * Add a metric to the profile, optionally specifying upper and lower thresholds for the metrics acceptable.
     *
     * @param metricID       the ID of the metric to add.
     * @param lowerThreshold The lower threshold of acceptable values for the metric, or null if there isn't any.
     * @param upperThreshold The upper threshold of acceptable values for the metric, or null if there isn't any.
     */
    public void addMetric(@NotNull @NonNls String metricID, Double lowerThreshold, Double upperThreshold) {
        metricNames.add(metricID);
        if (lowerThreshold != null) {
            lowerThresholds.put(metricID, lowerThreshold);
        }
        if (upperThreshold != null) {
            upperThresholds.put(metricID, upperThreshold);
        }
    }

    /**
     * Get the name of the profile.
     *
     * @return the name of the profile
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Get the ids of the metrics which have been added to the profile.
     *
     * @return the metric ids for the profile.
     */
    public Set<String> getMetricIDs() {
        return Collections.unmodifiableSet(metricNames);
    }

    /**
     * Get the lower threshold for a metric in the profile, or null if there is no lower threshold specified.
     *
     * @param id the id of the metric.
     * @return the lower threshold of acceptable values for the metric, or null if there isn't any.
     */
    public Double getLowerThresholdForMetric(String id) {
        return lowerThresholds.get(id);
    }

    /**
     * Get the upper threshold for a metric in the profile, or null if there is no upper threshold specified.
     *
     * @param id the id of the metric.
     * @return the upper threshold of acceptable values for the metric, or null if there isn't any.
     */
    public Double getUpperThresholdForMetric(String id) {
        return upperThresholds.get(id);
    }

    private static String calculateName(@NotNull Class<? extends Metric> metricClass) {
        final String className = metricClass.getSimpleName();
        if (!className.endsWith("Metric")) {
            throw new IllegalArgumentException("class name must end with Metric");
        }
        @NonNls final int endIndex = className.length() - "Metric".length();
        return className.substring(0, endIndex);
    }
}
