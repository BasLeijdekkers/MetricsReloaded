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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.analysis.AnalysisScope;

/**
 * The MetricsExecutionContext is used .  One MetricsExecutionContext is created for each metrics run, and shared by
 * all of the individual MetricsCalculators.  That makes it handy for sharing data between MetricsCalculators.
 */
public interface MetricsExecutionContext extends UserDataHolder {
    /**
     * A reference to the project that metrics are being calculated for.
     * @return  the project for the run.
     */
    Project getProject();

  /**
     * A reference to the analysis scope (i.e. set of files) that metrics are being calculated for.
     * @return  the analysis scope for the run.
     */
    AnalysisScope getScope();

}
