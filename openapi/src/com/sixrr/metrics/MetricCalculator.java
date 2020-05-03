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

import com.intellij.psi.PsiFile;

/**
 * The MetricsCalculator interface, which is subclassed for each individual metric in order to implement all of the metrics
 * calculation.  A new MetricsCalculator is instantiated for each metrics calculation run. 
 */
public interface MetricCalculator {

    /**
     * Begin a metrics calculation run.
     * @param resultsHolder the results holder to report metrics results to.
     * @param executionContext the metrics execution context, in which intermediate data can be cached for
     * the duration of the run.
     */
    void beginMetricsRun(MetricsResultsHolder resultsHolder, MetricsExecutionContext executionContext);

    /**
     * Process a file.  This method will be called once for each file in the analysis scope.
     * @param file  the file to calculate metrics for.
     */
    void processFile(PsiFile file);

    /**
     * Complete the metrics run. This is where the calculator should report any final results, and clean up any resources it
     * has acquired.
     */
    void endMetricsRun();
}
