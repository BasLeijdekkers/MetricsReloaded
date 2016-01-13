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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;

public abstract class ComplexityCalculator extends MethodCalculator {

    private int complexity = 1;
    private int methodNestingDepth = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            final PsiCodeBlock body = method.getBody();
            if (body == null) {
                return;
            }
            if (methodNestingDepth == 0) {
                complexity = 1;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth <= 0) {
                postMetric(method, complexity);
            }
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            if (!isReducible(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            if (!isReducible(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (!isReducible(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            if (!isReducible(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            if (!isReducible(statement)) {
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

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            if (!isReducible(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            if (!isReducible(section)) {
                complexity ++;
            }
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            final IElementType token = expression.getOperationTokenType();
            if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                complexity += expression.getOperands().length - 1;
            }
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            if (!isReducible(expression)) {
                complexity++;
            }
        }
    }

    protected abstract boolean isReducible(PsiElement element);
}
