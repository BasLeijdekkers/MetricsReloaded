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

import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiElementFilter;

/**
 * @author Bas Leijdekkers
 */
public final class CyclomaticComplexityUtil {

    public static final PsiElementFilter ACCEPT_ALL = psiElement -> true;

    private CyclomaticComplexityUtil() {}

    public static int calculateComplexity(PsiElement element) {
        return calculateComplexity(element, ACCEPT_ALL);
    }

    public static int calculateComplexity(PsiElement element, PsiElementFilter filter) {
        if (element == null) {
            return 1;
        }
        ComplexityVisitor visitor = new ComplexityVisitor(filter);
        element.accept(visitor);
        return visitor.getComplexity();
    }

    private static class ComplexityVisitor extends JavaRecursiveElementWalkingVisitor {

        private final PsiElementFilter filter;
        private int complexity = 1;

        public ComplexityVisitor(PsiElementFilter filter) {
            this.filter = filter;
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            if (filter.isAccepted(statement)) complexity++;
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            if (filter.isAccepted(statement)) complexity++;
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (filter.isAccepted(statement)) complexity++;
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            if (filter.isAccepted(statement)) complexity++;
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            if (filter.isAccepted(expression)) complexity++;
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            final PsiCodeBlock body = statement.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            boolean pendingLabel = false;
            boolean accepted = true;
            for (final PsiStatement child : statements) {
                if (child instanceof PsiSwitchLabelStatement) {
                    if (!pendingLabel && accepted) {
                        complexity++;
                    }
                    accepted = true;
                    pendingLabel = true;
                } else {
                    accepted &= filter.isAccepted(child);
                    pendingLabel = false;
                }
            }
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            if (filter.isAccepted(statement)) complexity++;
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            if (filter.isAccepted(section)) complexity++;
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            if (!filter.isAccepted(expression)) {
                return;
            }
            final IElementType token = expression.getOperationTokenType();
            if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                complexity += expression.getOperands().length - 1;
            }
        }

        public int getComplexity() {
            return complexity;
        }
    }
}
