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

import java.util.Stack;

/**
 * Created by Aleksandr Chudov on 02.04.2017.
 */
public class FanInMethodCalculator extends MethodCalculator {
    private final Stack<Integer> fanInMetrics = new Stack<Integer>();
    private int currentMetric = -1;
    private final Stack<PsiMethod> methods = new Stack<PsiMethod>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            if (currentMetric != -1) {
                fanInMetrics.push(Integer.valueOf(currentMetric));
            }
            methods.push(method);
            currentMetric = 0;
            super.visitMethod(method);
            postMetric(method, currentMetric);
            methods.pop();
            currentMetric = fanInMetrics.empty() ? -1 : fanInMetrics.pop().intValue();
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (methods.empty()) {
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if (method == null || methods.peek().equals(method)) {
                return;
            }
            currentMetric++;
        }
    }
}
