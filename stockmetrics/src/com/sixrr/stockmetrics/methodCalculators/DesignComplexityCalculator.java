/*
 * Copyright 2005-2015, Sixth and Red River Software, Bas Leijdekkers
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

    public boolean isReducible(PsiElement element) {
        if (element == null) {
            return true;
        }
        if (element instanceof PsiIfStatement) {
            return ifStatementIsReducible((PsiIfStatement) element);
        } else if (element instanceof PsiWhileStatement) {
            return whileStatementIsReducible((PsiWhileStatement) element);
        } else if (element instanceof PsiDoWhileStatement) {
            return doWhileStatementIsReducible((PsiDoWhileStatement) element);
        } else if (element instanceof PsiForStatement) {
            return forStatementIsReducible((PsiForStatement) element);
        } else if (element instanceof PsiForeachStatement) {
            return foreachStatementIsReducible((PsiForeachStatement) element);
        } else if (element instanceof PsiSynchronizedStatement) {
            return synchronizedStatementIsReducible((PsiSynchronizedStatement) element);
        } else if (element instanceof PsiTryStatement) {
            return tryStatementIsReducible((PsiTryStatement) element);
        } else if (element instanceof PsiSwitchStatement) {
            return switchStatementIsReducible((PsiSwitchStatement) element);
        } else if (element instanceof PsiBlockStatement) {
            return blockStatementIsReducible((PsiBlockStatement) element);
        } else if (element instanceof PsiConditionalExpression) {
            return isConditionalExpressionReducible((PsiConditionalExpression) element);
        }
        return true;
    }

    private static boolean tryStatementIsReducible(PsiTryStatement statement) {
        final PsiCodeBlock tryBlock = statement.getTryBlock();
        if (containsMethodCall(tryBlock)) {
            return false;
        }
        final PsiCodeBlock[] catchBlocks = statement.getCatchBlocks();
        for (final PsiCodeBlock catchBlock : catchBlocks) {
            if (containsMethodCall(catchBlock)) {
                return false;
            }
        }
        final PsiCodeBlock finallyBlock = statement.getFinallyBlock();
        return !containsMethodCall(finallyBlock);
    }

    private static boolean blockStatementIsReducible(PsiBlockStatement statement) {
        final PsiCodeBlock codeBlock = statement.getCodeBlock();
        return !containsMethodCall(codeBlock);
    }

    private static boolean switchStatementIsReducible(PsiSwitchStatement statement) {
        final PsiCodeBlock body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean synchronizedStatementIsReducible(PsiSynchronizedStatement statement) {
        final PsiCodeBlock body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean foreachStatementIsReducible(PsiForeachStatement statement) {
        final PsiStatement body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean forStatementIsReducible(PsiForStatement statement) {
        final PsiStatement body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean doWhileStatementIsReducible(PsiDoWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean whileStatementIsReducible(PsiWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return !containsMethodCall(body);
    }

    private static boolean ifStatementIsReducible(PsiIfStatement statement) {
        final PsiStatement elseBranch = statement.getElseBranch();
        final PsiStatement thenBranch = statement.getThenBranch();
        return !containsMethodCall(thenBranch) &&
                !containsMethodCall(elseBranch);
    }

    private static boolean isConditionalExpressionReducible(PsiConditionalExpression expression) {
        final PsiExpression thenExpression = expression.getThenExpression();
        final PsiExpression elseExpression = expression.getElseExpression();
        return !containsMethodCall(thenExpression) && !containsMethodCall(elseExpression);
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

        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            methodCalled = true;
        }

        private boolean isMethodCalled() {
            return methodCalled;
        }
    }
}
