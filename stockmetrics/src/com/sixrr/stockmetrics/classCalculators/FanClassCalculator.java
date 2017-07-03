/*
 * Copyright 2005-2017 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricsExecutionContext;
import com.sixrr.metrics.MetricsResultsHolder;
import com.sixrr.stockmetrics.utils.FieldUsageMap;
import com.sixrr.stockmetrics.utils.FieldUsageMapImpl;

import java.util.*;

public abstract class FanClassCalculator extends ClassCalculator {
    protected final Map<PsiClass, Set<PsiClass>> metrics = new HashMap<PsiClass, Set<PsiClass>>();
    protected final Collection<PsiClass> visitedClasses = new ArrayList<PsiClass>();
    protected final Key<FieldUsageMap> fieldUsageKey = new Key<FieldUsageMap>("FieldUsageMap");

    @Override
    public void beginMetricsRun(Metric metric, MetricsResultsHolder resultsHolder, MetricsExecutionContext executionContext) {
        final FieldUsageMap map = executionContext.getUserData(fieldUsageKey);
        if(map == null) {
            executionContext.putUserData(fieldUsageKey, new FieldUsageMapImpl());
        }
        super.beginMetricsRun(metric, resultsHolder, executionContext);
    }

    @Override
    public void endMetricsRun() {
        for (final PsiClass aClass : visitedClasses) {
            postMetric(aClass, metrics.get(aClass).size());
        }
        super.endMetricsRun();
    }
}
