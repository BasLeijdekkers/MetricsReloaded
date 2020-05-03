/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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
import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.utils.ControlFlowUtils;
import com.sixrr.metrics.utils.MethodUtils;

public class NumReturnPointsCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    private int numReturnPoints = 0;

    public NumReturnPointsCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                numReturnPoints = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {

                if (mayFallThroughBottom(method)) {
                    final PsiCodeBlock body = method.getBody();
                    if (body != null) {
                        final PsiStatement[] statements = body.getStatements();
                        if (statements.length == 0) {
                            numReturnPoints++;
                        } else {
                            final PsiStatement lastStatement = statements[statements.length - 1];
                            if (ControlFlowUtils.statementMayCompleteNormally(lastStatement)) {
                                numReturnPoints++;
                            }
                        }
                    }
                }
                postMetric(method, numReturnPoints);
            }
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            numReturnPoints++;
        }

        private boolean mayFallThroughBottom(PsiMethod method) {
            if (method.isConstructor()) {
                return true;
            }
            final PsiType returnType = method.getReturnType();
            return PsiType.VOID.equals(returnType);
        }
    }
}
