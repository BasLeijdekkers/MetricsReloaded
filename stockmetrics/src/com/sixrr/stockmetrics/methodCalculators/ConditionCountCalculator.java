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

public class ConditionCountCalculator extends MethodCalculator {
    private int count = 0;
    private int depth = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitExpression(PsiExpression expression) {
            final int oldCount = count;
            super.visitExpression(expression);
            if (expression.getType() != null &&
                    PsiType.BOOLEAN.isAssignableFrom(expression.getType()) && oldCount == count) {
                count++;
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if (depth == 0) {
                count = 0;
            }
            depth++;
            super.visitMethod(method);
            depth--;
            if (depth == 0) {
                postMetric(method, count);
            }
        }
    }
}
