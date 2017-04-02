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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.psi.*;

import java.util.*;

/**
 * Created by Aleksandr Chudov on 02.04.2017.
 */
public class FanOutMethodCalculator extends MethodCalculator {
    private final Map<PsiMethod, Integer> metrics = new HashMap<PsiMethod, Integer>();
    private final Collection<PsiMethod> visitMethods = new ArrayList<PsiMethod>();
    private final Stack<PsiMethod> methods = new Stack<PsiMethod>();

    @Override
    public void endMetricsRun() {
        for (PsiMethod method : visitMethods) {
            postMetric(method, metrics.get(method));
        }
        super.endMetricsRun();
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            methods.push(method);
            visitMethods.add(method);
            if (!metrics.containsKey(method)) {
                metrics.put(method, 0);
            }
            super.visitMethod(method);
            methods.pop();
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod method = expression.resolveMethod();
            if (method == null || methods.empty() || methods.peek().equals(method)) {
                return;
            }
            int metric = metrics.containsKey(method) ? metrics.get(method).intValue() : 0;
            metrics.put(method, metric + 1);
        }
    }
}
