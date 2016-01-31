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

/**
 * The MetricType class enumerates the various kinds of metrics available (counts, ratios, averages, etc.), and is used
 * to determine how metric values are aggregated and displayed.
 */
public enum MetricType {
    /**
     * Use the Count metric type if your metric is a count of objects in some context (i.e. methods per class) which can be
     * reasonably aggregated to higher levels.  Counts should be positive integer values.
     */
    Count,

    /**
     * Use the Score metric type if your metric is some number with no intrinsic meaning other than comparison (i.e. Halstead metrics), or which
     * cannot be reasonably aggregated (i.e. loop nesting depths).  Scores can be any integer value.
     */
    Score,

    /**
     * Use the Average metric type if your metric is some computed value representing the average value of some other metric over
     * the reported value.  Averages will be averaged, but not totalled.  Averages can be any value.
     */
    Average,

    /**
     * Use the Ratio type if your metric is the ratio of two set sizes, with one set a subset of the other (i.e. comment ratios).
     * Ratio values will be reported as percentages, and the numerators and denominators aggregated separately.
     */
    Ratio,

    /**
     * Use the RecursiveRatio type if your metric is the ratio of two set sizes, with one set a subset of the other (i.e. comment ratios),
     * and which is reported on packages recursively.   This is basically the same as the
     * Ratio type, with slightly different aggregation semantics to prevent double-counting of recursively included packages.
     * Ratio values will be reported as percentages, and the numerators and denominators aggregated separately.
     */
    RecursiveRatio,

    /**
     * Use the RecursiveCount metric type if your metric is a count of objects in some context (i.e. methods per class) which can be
     * reasonably aggregated to higher levels, and is reported on packages recursively.  This is basically the same as the
     * Count type, with slightly different aggregation semantics to prevent double-counting of recursively included packages.
     * RecursiveCounts should be positive integer values.
     */
    RecursiveCount
}
