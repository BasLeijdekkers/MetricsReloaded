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

package com.sixrr.metrics.metricModel;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceExpression;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricsExecutionContext;
import com.sixrr.metrics.MetricsResultsHolder;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsExecutionContextImpl implements MetricsExecutionContext {
    private final Project project;
    private final AnalysisScope scope;

    public MetricsExecutionContextImpl(Project project, AnalysisScope scope) {
        super();
        this.project = project;
        this.scope = scope;
    }

    public boolean execute(final MetricsProfile profile,
                           final MetricsResultsHolder metricsRun) {
        final int numFiles = scope.getFileCount();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final Runnable runnable = new Runnable() {
            public void run() {
                final ProgressIndicator progressIndicator = progressManager.getProgressIndicator();
                final List<MetricInstance> metrics = profile.getMetrics();
                progressIndicator.setText(MetricsReloadedBundle.message("initializing.progress.string"));
                final int numMetrics = metrics.size();
                final List<MetricCalculator> calculators = new ArrayList<MetricCalculator>(numMetrics);
                for (final MetricInstance metricInstance : metrics) {
                    final Metric metric = metricInstance.getMetric();
                    if (metricInstance.isEnabled()) {
                        final MetricCalculator calculator = metric.createCalculator();

                        if (calculator != null) {
                            calculators.add(calculator);
                            calculator.beginMetricsRun(metricInstance.getMetric(), metricsRun,
                                    MetricsExecutionContextImpl.this);
                        }
                    }
                }

                scope.accept(new PsiElementVisitor() {
                    private int mainTraversalProgress = 0;

                    public void visitReferenceExpression(PsiReferenceExpression psiReferenceExpression) {
                    }

                    public void visitFile(PsiFile psiFile) {
                        super.visitFile(psiFile);
                        final String fileName = psiFile.getName();
                        progressIndicator.setText(MetricsReloadedBundle.message("analyzing.progress.string", fileName));
                        mainTraversalProgress++;

                        for (MetricCalculator calculator : calculators) {
                            calculator.processFile(psiFile);
                        }
                        progressIndicator.setFraction((double) mainTraversalProgress / (double) numFiles);
                    }
                });

                progressIndicator.setText(MetricsReloadedBundle.message("tabulating.results.progress.string"));
                for (MetricCalculator calculator : calculators) {
                    calculator.endMetricsRun();
                }
            }

        };

        //noinspection HardCodedStringLiteral
        return !progressManager.runProcessWithProgressSynchronously(runnable, "MetricsReloaded", true, project);
    }

    public Project getProject() {
        return project;
    }

    public AnalysisScope getScope() {
        return scope;
    }

    private Map userData = new HashMap();

    public <T> T getUserData(Key<T> key) {
        return (T) userData.get(key);
    }

    public <T> void putUserData(Key<T> key, T t) {
        userData.put(key, t);
    }

}
