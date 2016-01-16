/*
 * Copyright 2005-2016 Bas Leijdekkers, Sixth and Red River Software
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

package com.sixrr.stockmetrics.execution;

import com.intellij.analysis.AnalysisScope;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.Processor;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricsExecutionContext;
import com.sixrr.metrics.MetricsResultsHolder;
import com.sixrr.stockmetrics.dependency.DependencyMap;
import com.sixrr.stockmetrics.dependency.DependencyMapImpl;
import com.sixrr.stockmetrics.dependency.DependentsMap;
import com.sixrr.stockmetrics.dependency.DependentsMapImpl;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.metricModel.BaseMetric;

public abstract class BaseMetricsCalculator implements MetricCalculator {

    private static final Key<DependencyMap> dependencyMapKey = new Key<DependencyMap>("dependencyMap");
    private static final Key<DependentsMap> dependentsMapKey = new Key<DependentsMap>("dependentsMap");


    protected Metric metric = null;
    protected MetricsResultsHolder resultsHolder = null;
    protected MetricsExecutionContext executionContext = null;

    public void beginMetricsRun(Metric metric, MetricsResultsHolder resultsHolder,
                                MetricsExecutionContext executionContext) {
        this.metric = metric;
        this.resultsHolder = resultsHolder;
        this.executionContext = executionContext;
        if (((BaseMetric)metric).requiresDependents() && getDependencyMap() == null) {
            calculateDependencies();
        }
    }

    public void processFile(PsiFile file) {
        final PsiElementVisitor visitor = createVisitor();
        file.accept(visitor);
    }

    protected abstract PsiElementVisitor createVisitor();

    public void endMetricsRun() {
    }

    public DependencyMap getDependencyMap() {
        return executionContext.getUserData(dependencyMapKey);
    }

    public DependentsMap getDependentsMap() {
        return executionContext.getUserData(dependentsMapKey);
    }

    private void calculateDependencies() {
        final DependentsMapImpl dependentsMap = new DependentsMapImpl();
        final DependencyMapImpl dependencyMap = new DependencyMapImpl();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProgressIndicator progressIndicator = progressManager.getProgressIndicator();

        final Project project = executionContext.getProject();
        final AnalysisScope analysisScope = new AnalysisScope(project);
        final int allFilesCount = analysisScope.getFileCount();
        final PsiManager psiManager = PsiManager.getInstance(project);
        final Application application = ApplicationManager.getApplication();

        analysisScope.accept(new Processor<VirtualFile>() {

            private int dependencyProgress = 0;

            @Override
            public boolean process(VirtualFile virtualFile) {
                final String fileName = virtualFile.getName();
                progressIndicator.setText(
                        StockMetricsBundle.message("building.dependency.structure.progress.string", fileName));
                progressIndicator.setFraction((double) dependencyProgress / (double) allFilesCount);
                dependencyProgress++;
                if (virtualFile.getFileType() != JavaFileType.INSTANCE) return true;
                final AccessToken token = application.acquireReadActionLock();
                try {
                    final PsiFile file = psiManager.findFile(virtualFile);
                    if (!(file instanceof PsiJavaFile)) return true;
                    dependencyMap.build(file);
                    dependentsMap.build(file);
                } finally {
                    token.finish();
                }
                return true;
            }
        });
        executionContext.putUserData(dependencyMapKey, dependencyMap);
        executionContext.putUserData(dependentsMapKey, dependentsMap);
    }
}
