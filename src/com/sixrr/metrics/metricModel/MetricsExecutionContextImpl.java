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

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricsExecutionContext;
import com.sixrr.metrics.MetricsResultsHolder;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsExecutionContextImpl implements MetricsExecutionContext {

    private final Project project;
    private final AnalysisScope scope;

    public MetricsExecutionContextImpl(Project project, AnalysisScope scope) {
        this.project = project;
        this.scope = scope;
    }

    public final void execute(final MetricsProfile profile, final MetricsResultsHolder resultsHolder) {
        final Task.Backgroundable task = new Task.Backgroundable(project,
                MetricsReloadedBundle.message("calculating.metrics"), true) {

            public void run(@NotNull final ProgressIndicator indicator) {
                calculateMetrics(profile, resultsHolder);
            }

            @Override
            public void onSuccess() {
                onFinish();
            }

            @Override
            public void onCancel() {
                MetricsExecutionContextImpl.this.onCancel();
            }
        };
        task.queue();
    }

    public void calculateMetrics(MetricsProfile profile, final MetricsResultsHolder resultsHolder) {
        final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        final List<MetricInstance> metrics = profile.getMetricInstances();
        indicator.setText(MetricsReloadedBundle.message("initializing.progress.string"));
        final int numFiles = scope.getFileCount();
        final int numMetrics = metrics.size();
        final List<MetricCalculator> calculators = new ArrayList<MetricCalculator>(numMetrics);
        for (final MetricInstance metricInstance : metrics) {
            indicator.checkCanceled();
            if (!metricInstance.isEnabled()) {
                continue;
            }
            final Metric metric = metricInstance.getMetric();
            final MetricCalculator calculator = metric.createCalculator();

            if (calculator != null) {
                calculators.add(calculator);
                calculator.beginMetricsRun(metric, resultsHolder, this);
            }
        }

        scope.accept(new PsiElementVisitor() {
            private int mainTraversalProgress = 0;

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                if (file instanceof PsiCompiledElement) {
                    return;
                }
                final FileType fileType = file.getFileType();
                if (fileType.isBinary()) {
                    return;
                }
                final VirtualFile virtualFile = file.getVirtualFile();
                final ProjectRootManager rootManager = ProjectRootManager.getInstance(file.getProject());
                final ProjectFileIndex fileIndex = rootManager.getFileIndex();
                if (fileIndex.isExcluded(virtualFile) || !fileIndex.isInContent(virtualFile)) {
                    return;
                }
                final String fileName = file.getName();
                indicator.setText(MetricsReloadedBundle.message("analyzing.progress.string", fileName));
                mainTraversalProgress++;

                for (MetricCalculator calculator : calculators) {
                    calculator.processFile(file);
                }
                indicator.setFraction((double) mainTraversalProgress / (double) numFiles);
            }
        });

        indicator.setText(MetricsReloadedBundle.message("tabulating.results.progress.string"));
        for (MetricCalculator calculator : calculators) {
            indicator.checkCanceled();
            calculator.endMetricsRun();
        }
    }

    public void onFinish() {}

    public void onCancel() {}

    public final Project getProject() {
        return project;
    }

    public final AnalysisScope getScope() {
        return scope;
    }

    private Map userData = new HashMap();

    public final <T> T getUserData(Key<T> key) {
        return (T) userData.get(key);
    }

    public final <T> void putUserData(Key<T> key, T t) {
        userData.put(key, t);
    }
}
