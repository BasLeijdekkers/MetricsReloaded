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
import com.sixrr.stockmetrics.halstead.HalsteadVisitor;
import com.sixrr.metrics.utils.MethodUtils;

public class QCPCorrectnessCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    private int complexity = 0;
    private int numControlStatements = 0;
    private int numExecutableStatements = 0;

    public QCPCorrectnessCalculator(Metric metric) {
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
                complexity = 1;
                numExecutableStatements = 0;
                numControlStatements = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                final HalsteadVisitor visitor = new HalsteadVisitor();
                method.accept(visitor);
                final double D = visitor.getDifficulty();
                final double value = D + (double) numControlStatements + (double) numExecutableStatements +
                        (double) (2 * complexity);
                postMetric(method, value);
            }
        }

        @Override
        public void visitExpressionListStatement(PsiExpressionListStatement statement) {
            super.visitExpressionListStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitExpressionStatement(PsiExpressionStatement statement) {
            super.visitExpressionStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
            super.visitDeclarationStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitAssertStatement(PsiAssertStatement statement) {
            super.visitAssertStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            super.visitReturnStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitThrowStatement(PsiThrowStatement statement) {
            super.visitThrowStatement(statement);
            numExecutableStatements++;
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            numControlStatements++;
            complexity++;
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            numControlStatements++;
            complexity++;
        }

        @Override
        public void visitContinueStatement(PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitBreakStatement(PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            numControlStatements++;
            complexity++;
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            numControlStatements++;
            complexity++;
        }

        @Override
        public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
            super.visitSwitchLabelStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            numControlStatements++;
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

        @Override
        public void visitSynchronizedStatement(PsiSynchronizedStatement statement) {
            super.visitSynchronizedStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            numControlStatements++;
            complexity++;
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            complexity++;
        }
    }
}
