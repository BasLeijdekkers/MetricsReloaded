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

package com.sixrr.metrics;

/**
 * The metric category for a metric determines just what sort of program objects a metric is calculated for.
 */
public enum MetricCategory {

    /**
     * The metric is calculated for each method.
     */
    Method,

    /**
     * The metric is calculated for each concrete or abstract class.
     */
    Class,

    /**
     * The metric is calculated for each interface.
     */
    Interface,

    /**
     * The metric is calculated for each package.
     */
    Package,

    /**
     * The metric is calculated for each module.
     */
    Module,

    /**
     * The metrics is calculated for each file type.
     */
    FileType,

    /**
     * Tbe metric is calculated once for the project as a whole.
     */
    Project
}