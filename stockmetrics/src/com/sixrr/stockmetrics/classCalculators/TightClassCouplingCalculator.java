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
import com.intellij.psi.*;
import com.intellij.util.containers.Predicate;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.stockmetrics.utils.FieldUsageUtil;

import java.util.*;

/**
 * @author Aleksandr Chudov.
 * Number of method pairs that access common attributes over the total number of method pairs
 */
public class TightClassCouplingCalculator extends MethodPairsCountClassCalculator {
    @Override
    public void endMetricsRun() {
        final BucketedCount<PsiClass> metrics = calculatePairs();
        for (final PsiClass aClass : metrics.getBuckets()) {
            final int n = getVisibleMethodsCount(aClass);
            if (n < 2) {
                postMetric(aClass, 0);
            }
            else {
                postMetric(aClass, metrics.getBucketValue(aClass), n * (n - 1) / 2);
            }
        }
        super.endMetricsRun();
    }

    @Override
    protected int calculatePairs(PsiClass aClass, Predicate<MethodPair> isSuitable) {
        return super.calculatePairs(getVisibleMethods(aClass), isSuitable);
    }
}
