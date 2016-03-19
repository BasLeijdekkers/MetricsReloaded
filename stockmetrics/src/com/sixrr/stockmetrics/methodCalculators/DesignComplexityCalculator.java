/*
 * Copyright 2005-2016, Sixth and Red River Software, Bas Leijdekkers
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

public class DesignComplexityCalculator extends ComplexityCalculator {

    @Override
    public boolean isReducible(PsiElement element) {
        if (element == null) {
            return true;
        }
        if (element instanceof PsiIfStatement) {
            final PsiIfStatement ifStatement = (PsiIfStatement) element;
            return !containsMethodCall(ifStatement.getThenBranch()) && !containsMethodCall(ifStatement.getElseBranch());
        } else if (element instanceof PsiLoopStatement) {
            final PsiLoopStatement loopStatement = (PsiLoopStatement) element;
            return !containsMethodCall(loopStatement.getBody());
        } else if (element instanceof PsiCatchSection) {
            final PsiCatchSection catchSection = (PsiCatchSection) element;
            return !containsMethodCall(catchSection.getCatchBlock());
        } else if (element instanceof PsiBlockStatement) {
            return blockStatementIsReducible((PsiBlockStatement) element);
        } else if (element instanceof PsiConditionalExpression) {
            final PsiConditionalExpression conditionalExpression = (PsiConditionalExpression) element;
            return !containsMethodCall(conditionalExpression.getThenExpression()) &&
                    !containsMethodCall(conditionalExpression.getElseExpression());
        } else {
            return !containsMethodCall(element);
        }
    }

    private static boolean blockStatementIsReducible(PsiBlockStatement statement) {
        return !containsMethodCall(statement.getCodeBlock());
    }

    private static boolean containsMethodCall(PsiElement element) {
        if (element == null) {
            return false;
        }
        final MethodCallVisitor visitor = new MethodCallVisitor();
        element.accept(visitor);
        return visitor.isMethodCalled();
    }

    private static class MethodCallVisitor extends JavaRecursiveElementVisitor {
        private boolean methodCalled = false;

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            methodCalled = true;
        }

        private boolean isMethodCalled() {
            return methodCalled;
        }
    }
}
