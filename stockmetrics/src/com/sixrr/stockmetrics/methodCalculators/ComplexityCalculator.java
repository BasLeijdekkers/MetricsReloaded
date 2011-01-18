/*
 * Copyright 2005, Sixth and Red River Software
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
import com.intellij.psi.tree.IElementType;
import com.sixrr.metrics.utils.MethodUtils;

abstract class ComplexityCalculator extends MethodCalculator {
    private int complexity = 1;
    private int methodNestingDepth = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                complexity = 0;
            }
            methodNestingDepth++;

            if (!MethodUtils.isAbstract(method)) {
                complexity++;
            }
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth <= 0) {
                if (!MethodUtils.isAbstract(method)) {
                    postMetric(method, complexity);
                }
            }
        }

        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            if (!statementIsReducible(statement)) {
                complexity++;
            }
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            if (!statementIsReducible(statement)) {
                complexity++;
            }
        }

        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (!statementIsReducible(statement)) {
                complexity++;
            }
        }

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            if (!statementIsReducible(statement)) {
                complexity++;
            }
        }

        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            if (!statementIsReducible(statement)) {
                final PsiCodeBlock body = statement.getBody();
                if (body == null) {
                    return;
                }
                final PsiStatement[] statements = body.getStatements();
                boolean pendingLabel = false;
                for (final PsiStatement child : statements) {
                    if (child instanceof PsiSwitchLabelStatement) {
                        if (!pendingLabel) {
                            complexity++;
                        }
                        pendingLabel = true;
                    } else {
                        pendingLabel = false;
                    }
                }
            }
        }

        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            if (!statementIsReducible(statement)) {
                complexity++;
            }
        }

        public void visitBinaryExpression(PsiBinaryExpression expression) {
            super.visitBinaryExpression(expression);
            if (countShortCircuitExpressions()) {
                final PsiJavaToken sign = expression.getOperationSign();
                final IElementType token = sign.getTokenType();
                if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                    complexity++;
                }
            }
        }
    }

    protected boolean countShortCircuitExpressions() {
        return false;
    }

    protected abstract boolean statementIsReducible(PsiStatement statement);
}
