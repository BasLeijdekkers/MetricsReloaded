/*
 * Copyright 2005-2011, Bas Leijdekkers, Sixth and Red River Software
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

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Processor;
import com.intellij.util.Query;
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
            System.out.println("about to calculate dependencies");
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

    public void calculateDependencies() {
        final DependentsMapImpl dependentsMap = new DependentsMapImpl();
        final DependencyMapImpl dependencyMap = new DependencyMapImpl();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProgressIndicator progressIndicator = progressManager.getProgressIndicator();

        final Project project = executionContext.getProject();
        final GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        final Query<PsiClass> query = AllClassesSearch.search(scope, project);
        final int[] count = {0};
        query.forEach(new Processor<PsiClass>() {
            public boolean process(PsiClass aClass) {
                count[0]++;
                return true;
            }
        });
        final int allFilesCount = count[0];
        final PsiElementVisitor visitor = new JavaRecursiveElementVisitor() {
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);
                dependencyMap.build(aClass);
                dependentsMap.build(aClass);
            }
        };


        query.forEach(new Processor<PsiClass>() {
            private int dependencyProgress = 0;

            public boolean process(PsiClass aClass) {
                final String fileName = aClass.getName();
                progressIndicator.setText(
                        StockMetricsBundle.message("building.dependency.structure.progress.string",
                                fileName));
                progressIndicator.setFraction((double) dependencyProgress / (double) allFilesCount);
                dependencyProgress++;
                aClass.accept(visitor);
                return true;
            }
        });
        executionContext.putUserData(dependencyMapKey, dependencyMap);
        executionContext.putUserData(dependentsMapKey, dependentsMap);
    }
}
