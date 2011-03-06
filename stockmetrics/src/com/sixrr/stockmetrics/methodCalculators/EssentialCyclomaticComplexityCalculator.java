/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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

    public boolean statementIsReducible(PsiStatement statement) {
        if (statement == null) {
            return true;
        }
        if (statement instanceof PsiReturnStatement ||
                statement instanceof PsiThrowStatement ||
                statement instanceof PsiContinueStatement ||
                statement instanceof PsiBreakStatement) {
            return false;
        } else if (statement instanceof PsiIfStatement) {
            return ifStatementIsReducible((PsiIfStatement) statement);
        } else if (statement instanceof PsiWhileStatement) {
            return whileStatementIsReducible((PsiWhileStatement) statement);
        } else if (statement instanceof PsiDoWhileStatement) {
            return doWhileStatementIsReducible((PsiDoWhileStatement) statement);
        } else if (statement instanceof PsiForStatement) {
            return forStatementIsReducible((PsiForStatement) statement);
        } else if (statement instanceof PsiForeachStatement) {
            return foreachStatementIsReducible((PsiForeachStatement) statement);
        } else if (statement instanceof PsiSynchronizedStatement) {
            return synchronizedStatementIsReducible((PsiSynchronizedStatement) statement);
        } else if (statement instanceof PsiTryStatement) {
            return tryStatementIsReducible((PsiTryStatement) statement);
        } else if (statement instanceof PsiSwitchStatement) {
            return switchStatementIsReducible((PsiSwitchStatement) statement);
        } else if (statement instanceof PsiBlockStatement) {
            return blockStatementIsReducible((PsiBlockStatement) statement);
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
        final PsiCodeBlock finalyBlock = statement.getFinallyBlock();
        return codeBlockIsReducible(finalyBlock);
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
            if (!statementIsReducible(statement)) {
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
                if (!statementIsReducible(child)) {
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
        return statementIsReducible(body);
    }

    private boolean forStatementIsReducible(PsiForStatement statement) {
        final PsiStatement body = statement.getBody();
        return statementIsReducible(body);
    }

    private boolean doWhileStatementIsReducible(PsiDoWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return statementIsReducible(body);
    }

    private boolean whileStatementIsReducible(PsiWhileStatement statement) {
        final PsiStatement body = statement.getBody();
        return statementIsReducible(body);
    }

    private boolean ifStatementIsReducible(PsiIfStatement statement) {
        final PsiStatement elseBranch = statement.getElseBranch();
        final PsiStatement thenBranch = statement.getThenBranch();
        return statementIsReducible(thenBranch) &&
                statementIsReducible(elseBranch);
    }
}
