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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.PsiClass;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;

public abstract class ClassCalculator extends BaseMetricsCalculator {

    void postMetric(PsiClass aClass, int numerator, int denominator) {
        resultsHolder.postClassMetric(metric, aClass, (double) numerator, (double) denominator);
    }

    void postMetric(PsiClass aClass, int value) {
        resultsHolder.postClassMetric(metric, aClass, (double) value);
    }

    void postMetric(PsiClass aClass, double value) {
        resultsHolder.postClassMetric(metric, aClass, value);
    }

    protected static boolean isConcreteClass(PsiClass aClass) {
        return !(aClass.isInterface() || ClassUtils.isAnonymous(aClass));
    }
}
