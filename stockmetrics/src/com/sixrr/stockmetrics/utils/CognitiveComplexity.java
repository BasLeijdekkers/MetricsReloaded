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

package com.sixrr.stockmetrics.utils;

import com.intellij.codeInsight.daemon.impl.RecursiveCallLineMarkerProvider;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.sixrr.metrics.utils.Stack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Bas Leijdekkers
 */
public final class CognitiveComplexity {

    private CognitiveComplexity() {}


    public static int calculate(@Nullable PsiElement element) {
        if (element == null) {
            return 0;
        }
        final CognitiveComplexityVisitor visitor = new CognitiveComplexityVisitor();
        element.accept(visitor);
        return visitor.getCognitiveComplexity();
    }

    private static class CognitiveComplexityVisitor extends JavaRecursiveElementWalkingVisitor {

        private int nesting = 0;
        private int structural = 0;
        private int fundamental = 0;
        private int hybrid = 0;

        private final Stack<PsiElement> stack = new Stack<>();

        private int nest(PsiElement element) {
            stack.push(element);
            return ++nesting;
        }

        private void unnest(PsiElement element) {
            if (stack.size() == 0) {
                return;
            }
            final PsiElement peek = stack.peek();
            if (element == peek) {
                stack.pop();
                nesting--;
            }
        }

        @Override
        protected void elementFinished(@NotNull PsiElement element) {
            unnest(element);
            super.elementFinished(element);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            nest(method);
            super.visitMethod(method);
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
            nest(expression);
            super.visitLambdaExpression(expression);
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            structural += nest(statement);
            super.visitForStatement(statement);
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            structural += nest(statement);
            super.visitForeachStatement(statement);
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            structural += nest(statement);
            super.visitWhileStatement(statement);
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            structural += nest(statement);
            super.visitDoWhileStatement(statement);
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (statement.getElseBranch() != null) {
                hybrid++;
            }
            final PsiElement parent = statement.getParent();
            if (parent instanceof PsiIfStatement) {
                final PsiIfStatement parentIfStatement = (PsiIfStatement) parent;
                if (parentIfStatement.getElseBranch() == statement) {
                    return;
                }
            }
            structural += nest(statement);
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            structural += nest(expression);
            super.visitConditionalExpression(expression);
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            structural += nest(section);
            super.visitCatchSection(section);
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            structural += nest(statement);
            super.visitSwitchStatement(statement);
        }

        @Override
        public void visitSwitchExpression(PsiSwitchExpression expression) {
            structural += nest(expression);
            super.visitSwitchExpression(expression);
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            final IElementType tokenType = expression.getOperationTokenType();
            if (JavaTokenType.ANDAND == tokenType || JavaTokenType.OROR == tokenType) {
                fundamental++;
            }
            super.visitPolyadicExpression(expression);
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            if (RecursiveCallLineMarkerProvider.isRecursiveMethodCall(expression)) {
                fundamental++;
            }
            super.visitMethodCallExpression(expression);
        }

        @Override
        public void visitBreakStatement(PsiBreakStatement statement) {
            if (statement.getLabelIdentifier() != null) {
                fundamental++;
            }
            super.visitBreakStatement(statement);
        }

        @Override
        public void visitContinueStatement(PsiContinueStatement statement) {
            if (statement.getLabelIdentifier() != null) {
                fundamental++;
            }
            super.visitContinueStatement(statement);
        }

        public int getCognitiveComplexity() {
            return structural + fundamental + hybrid;
        }
    }
}
