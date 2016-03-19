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
import com.sixrr.stockmetrics.halstead.HalsteadVisitor;
import com.sixrr.metrics.utils.MethodUtils;

public class QCPReliabilityCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private int complexity = 0;
    private int numControlStatements = 0;
    private int numExecutableStatements = 0;
    private int numBranchStatements = 0;
    private int maxNestingDepth = 0;
    private int currentDepth = 0;

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
                numBranchStatements = 0;
                maxNestingDepth = 0;
                currentDepth = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                final HalsteadVisitor visitor = new HalsteadVisitor(executionContext);
                method.accept(visitor);
                final int N = visitor.getLength();
                final double value = (double) (N + (2 * maxNestingDepth) + (3 * complexity) + numBranchStatements +
                        numControlStatements + numExecutableStatements);
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
            boolean isAlreadyCounted = false;
            if (statement.getParent()instanceof PsiIfStatement) {
                final PsiIfStatement parent = (PsiIfStatement) statement.getParent();
                final PsiStatement elseBranch = parent.getElseBranch();
                if (statement.equals(elseBranch)) {
                    isAlreadyCounted = true;
                }
            }
            if (!isAlreadyCounted) {
                enterScope();
            }
            numControlStatements++;
            complexity++;
            super.visitIfStatement(statement);
            if (!isAlreadyCounted) {
                exitScope();
            }
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            enterScope();
            super.visitDoWhileStatement(statement);
            numControlStatements++;
            complexity++;
            exitScope();
        }

        @Override
        public void visitContinueStatement(PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
            numBranchStatements++;
            numControlStatements++;
        }

        @Override
        public void visitBreakStatement(PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
            numControlStatements++;
            if (statement.getLabelIdentifier() != null) {
                numBranchStatements++;
            } else if (!(statement.findExitedStatement()instanceof PsiSwitchStatement)) {
                numBranchStatements++;
            }
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            enterScope();
            super.visitForStatement(statement);
            numControlStatements++;
            complexity++;
            exitScope();
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            enterScope();
            super.visitForeachStatement(statement);
            numControlStatements++;
            complexity++;
            exitScope();
        }

        @Override
        public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
            super.visitSwitchLabelStatement(statement);
            numControlStatements++;
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            enterScope();
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
            super.visitSwitchStatement(statement);
            exitScope();
        }

        @Override
        public void visitSynchronizedStatement(PsiSynchronizedStatement statement) {
            enterScope();
            super.visitSynchronizedStatement(statement);
            numControlStatements++;
            exitScope();
        }

        @Override
        public void visitTryStatement(PsiTryStatement statement) {
            enterScope();
            super.visitTryStatement(statement);
            numControlStatements++;
            exitScope();
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            enterScope();
            super.visitWhileStatement(statement);
            numControlStatements++;
            complexity++;
            exitScope();
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            complexity++;
        }

        @Override
        public void visitBlockStatement(PsiBlockStatement statement) {
            final PsiElement parent = statement.getParent();
            final boolean isAlreadyCounted = parent instanceof PsiDoWhileStatement ||
                    parent instanceof PsiWhileStatement || parent instanceof PsiForStatement ||
                    parent instanceof PsiForeachStatement || parent instanceof PsiIfStatement ||
                    parent instanceof PsiSynchronizedStatement || parent instanceof PsiTryStatement;
            if (!isAlreadyCounted) {
                enterScope();
            }
            super.visitBlockStatement(statement);
            if (!isAlreadyCounted) {
                exitScope();
            }
        }

        private void enterScope() {
            currentDepth++;
            maxNestingDepth = Math.max(maxNestingDepth, currentDepth);
        }

        private void exitScope() {
            currentDepth--;
        }
    }
}
