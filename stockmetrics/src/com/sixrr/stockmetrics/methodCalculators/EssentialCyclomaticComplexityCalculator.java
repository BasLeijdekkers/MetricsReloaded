/*
 * Copyright 2005-2015 Sixth and Red River Software, Bas Leijdekkers
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

public class EssentialCyclomaticComplexityCalculator extends ComplexityCalculator {

    public boolean isReducible(PsiElement element) {
        if (element == null) {
            return true;
        }
        if (element instanceof PsiReturnStatement ||
                element instanceof PsiThrowStatement ||
                element instanceof PsiContinueStatement ||
                element instanceof PsiBreakStatement) {
            return false;
        } else if (element instanceof PsiIfStatement) {
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
        }
        return true;
    }

    private boolean tryStatementIsReducible(PsiTryStatement statement) {
        final PsiCodeBlock tryBlock = statement.getTryBlock();
        if (!codeBlockIsReducible(tryBlock)) {
            return false;
        }
        final PsiCodeBlock[] catchBlocks = statement.getCatchBlocks();
        for (final PsiCodeBlock catchBlock : catchBlocks) {
            if (!codeBlockIsReducible(catchBlock)) {
                return false;
            }
        }
        final PsiCodeBlock finallyBlock = statement.getFinallyBlock();
        return codeBlockIsReducible(finallyBlock);
    }

    private boolean blockStatementIsReducible(PsiBlockStatement statement) {
        final PsiCodeBlock codeBlock = statement.getCodeBlock();
        return codeBlockIsReducible(codeBlock);
    }

    private boolean codeBlockIsReducible(PsiCodeBlock codeBlock) {
        if (codeBlock == null) {
            return true;
        }
        final PsiStatement[] statements = codeBlock.getStatements();

        for (PsiStatement statement : statements) {
            if (!isReducible(statement)) {
                return false;
            }
        }
        return true;
    }

    private boolean switchStatementIsReducible(PsiSwitchStatement statement) {
        final PsiCodeBlock body = statement.getBody();
        if (body == null) {
            return true;
        }
        final PsiStatement[] statements = body.getStatements();
        boolean pendingLabel = true;
        for (final PsiStatement child : statements) {
            if (child instanceof PsiBreakStatement) {
                final PsiBreakStatement breakStatement = (PsiBreakStatement) child;
                final PsiStatement exitedStatement = breakStatement.findExitedStatement();
                if (statement.equals(exitedStatement)) {
                    pendingLabel = true;
                }
            } else if (child instanceof PsiSwitchLabelStatement) {
                if (!pendingLabel) {
                    return false;
                }
                pendingLabel = true;
            } else {
                if (!isReducible(child)) {
                    return false;
                }
                pendingLabel = false;
            }
        }
        return true;
    }

    private boolean synchronizedStatementIsReducible(PsiSynchronizedStatement statement) {
        final PsiCodeBlock body = statement.getBody();
        return codeBlockIsReducible(body);
    }

    private boolean foreachStatementIsReducible(PsiForeachStatement statement) {
        final PsiStatement body = statement.getBody();
        return isReducible(body);
    }

    private boolean forStatementIsReducible(PsiForStatement statement) {
        final PsiStatement body = statement.getBody();
        return isReducible(body);
    }

    private boolean doWhileStatementIsReducible(PsiDoWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return isReducible(body);
    }

    private boolean whileStatementIsReducible(PsiWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return isReducible(body);
    }

    private boolean ifStatementIsReducible(PsiIfStatement statement) {
        final PsiStatement elseBranch = statement.getElseBranch();
        final PsiStatement thenBranch = statement.getThenBranch();
        return isReducible(thenBranch) &&
                isReducible(elseBranch);
    }
}
