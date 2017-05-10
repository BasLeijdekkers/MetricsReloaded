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

import com.intellij.codeInsight.dataflow.SetUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.util.containers.Predicate;
import com.sixrr.metrics.utils.BucketedCount;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public class LackOfCohesionInMethods1ClassCalculator extends MethodPairsCountClassCalculator {
    @Override
    public void endMetricsRun() {
        final BucketedCount<PsiClass> withCommonFields = calculatePairs();
        final Set<PsiClass> buckets = withCommonFields.getBuckets();
        for (final PsiClass aClass : buckets) {
            final PsiMethod[] methods = aClass.getMethods();
            final int n = methods.length;
            final int methodsPairsCount = n * (n - 1) / 2;
            postMetric(aClass, Math.max(0, methodsPairsCount - 2 * withCommonFields.getBucketValue(aClass)));
        }
        super.endMetricsRun();
    }
}
