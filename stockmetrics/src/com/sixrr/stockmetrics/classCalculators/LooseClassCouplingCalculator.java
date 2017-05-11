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
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.stockmetrics.utils.FieldUsageUtil;

import java.util.*;

/**
 * @author Aleksandr Chudov.
 */
public class LooseClassCouplingCalculator extends MethodPairsCountClassCalculator {
    private final Map<PsiMethod, Set<PsiMethod>> methodsCalls = new HashMap<PsiMethod, Set<PsiMethod>>();

    @Override
    public void endMetricsRun() {
        for (final Map.Entry<PsiMethod, Set<PsiMethod>> e : methodsCalls.entrySet()) {
            if (!methodsToFields.containsKey(e.getKey())) {
                methodsToFields.put(e.getKey(), new HashSet<PsiField>());
            }
            collectFields(e.getKey(), e.getKey(), new HashSet<PsiMethod>());
        }
        final BucketedCount<PsiClass> metrics = calculatePairs();
        for (final PsiClass aClass : metrics.getBuckets()) {
            final int n = aClass.getMethods().length;
            if (n < 2) {
                postMetric(aClass, 0);
            }
            else {
                postMetric(aClass, metrics.getBucketValue(aClass), n * (n - 1) / 2);
            }
        }
        super.endMetricsRun();
    }

    private void collectFields(final PsiMethod current, final PsiMethod primary, final Set<PsiMethod> visited) {
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        if (methodsToFields.containsKey(current)) {
            methodsToFields.get(primary).addAll(methodsToFields.get(current));
        }
        if (methodsCalls.get(current) == null) {
            return;
        }
        for (final PsiMethod neighbor : methodsCalls.get(current)) {
            collectFields(neighbor, current, visited);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends MethodPairsCountClassVisitor {
        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod currentMethod = PsiTreeUtil.getParentOfType(expression, PsiMethod.class);
            final PsiMethod calledMethod = expression.resolveMethod();
            if (currentMethod == null || currentMethod.getContainingClass() == null || calledMethod == null) {
                return;
            }
            if (!methodsCalls.containsKey(currentMethod)) {
                methodsCalls.put(currentMethod, new HashSet<PsiMethod>());
            }
            methodsCalls.get(currentMethod).add(calledMethod);
        }
    }
}
