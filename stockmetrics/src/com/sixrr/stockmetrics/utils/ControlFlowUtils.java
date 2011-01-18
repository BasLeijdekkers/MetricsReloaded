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

package com.sixrr.stockmetrics.utils;

import com.intellij.psi.*;

public class ControlFlowUtils {
    private ControlFlowUtils() {
        super();
    }

    public static boolean statementMayCompleteNormally(PsiStatement statement) {
        if (statement == null) {
            return true;
        }
        if (statement instanceof PsiBreakStatement ||
                statement instanceof PsiContinueStatement ||
                statement instanceof PsiReturnStatement ||
                statement instanceof PsiThrowStatement) {
            return false;
        } else if (statement instanceof PsiExpressionListStatement ||
                statement instanceof PsiExpressionStatement ||
                statement instanceof PsiEmptyStatement ||
                statement instanceof PsiAssertStatement ||
                statement instanceof PsiDeclarationStatement) {
            return true;
        } else if (statement instanceof PsiForStatement) {
            return forStatementMayReturnNormally((PsiForStatement) statement);
        } else if (statement instanceof PsiForeachStatement) {
            return true;
        } else if (statement instanceof PsiWhileStatement) {
            return whileStatementMayReturnNormally((PsiWhileStatement) statement);
        } else if (statement instanceof PsiDoWhileStatement) {
            return doWhileStatementMayReturnNormally((PsiDoWhileStatement) statement);
        } else if (statement instanceof PsiSynchronizedStatement) {
            final PsiCodeBlock body = ((PsiSynchronizedStatement) statement).getBody();
            return codeBlockMayCompleteNormally(body);
        } else if (statement instanceof PsiBlockStatement) {
            final PsiCodeBlock codeBlock = ((PsiBlockStatement) statement).getCodeBlock();
            return codeBlockMayCompleteNormally(codeBlock);
        } else if (statement instanceof PsiLabeledStatement) {
            return labeledStatementMayCompleteNormally((PsiLabeledStatement) statement);
        } else if (statement instanceof PsiIfStatement) {
            return ifStatementMayReturnNormally((PsiIfStatement) statement);
        } else if (statement instanceof PsiTryStatement) {
            return tryStatementMayReturnNormally((PsiTryStatement) statement);
        } else if (statement instanceof PsiSwitchStatement) {
            return switchStatementMayReturnNormally((PsiSwitchStatement) statement);
        } else   // unknown statement type
        {
            return true;
        }
    }

    private static boolean doWhileStatementMayReturnNormally(PsiDoWhileStatement loopStatement) {
        final PsiExpression test = loopStatement.getCondition();
        final PsiStatement body = loopStatement.getBody();
        return statementMayCompleteNormally(body) && !BoolUtils.isTrue(test)
                || statementIsBreakTarget(loopStatement);
    }

    private static boolean whileStatementMayReturnNormally(PsiWhileStatement loopStatement) {
        final PsiExpression test = loopStatement.getCondition();
        return !BoolUtils.isTrue(test)
                || statementIsBreakTarget(loopStatement);
    }

    private static boolean forStatementMayReturnNormally(PsiForStatement loopStatement) {
        final PsiExpression test = loopStatement.getCondition();

        if (statementIsBreakTarget(loopStatement)) {
            return true;
        }
        if (test == null) {
            return false;
        }
        return !BoolUtils.isTrue(test);
    }

    private static boolean switchStatementMayReturnNormally(PsiSwitchStatement switchStatement) {
        if (statementIsBreakTarget(switchStatement)) {
            return true;
        }
        final PsiCodeBlock body = switchStatement.getBody();
        if (body == null) {
            return true;
        }
        final PsiStatement[] statements = body.getStatements();
        return statementMayCompleteNormally(statements[statements.length - 1]);
    }

    private static boolean tryStatementMayReturnNormally(PsiTryStatement tryStatement) {
        final PsiCodeBlock finallyBlock = tryStatement.getFinallyBlock();
        if (finallyBlock != null) {
            if (!codeBlockMayCompleteNormally(finallyBlock)) {
                return false;
            }
        }
        final PsiCodeBlock tryBlock = tryStatement.getTryBlock();
        if (codeBlockMayCompleteNormally(tryBlock)) {
            return true;
        }
        final PsiCodeBlock[] catchBlocks = tryStatement.getCatchBlocks();
        for (final PsiCodeBlock catchBlock : catchBlocks) {
            if (codeBlockMayCompleteNormally(catchBlock)) {
                return true;
            }
        }
        return false;
    }

    private static boolean ifStatementMayReturnNormally(PsiIfStatement ifStatement) {
        final PsiStatement thenBranch = ifStatement.getThenBranch();
        if (statementMayCompleteNormally(thenBranch)) {
            return true;
        }
        final PsiStatement elseBranch = ifStatement.getElseBranch();
        return elseBranch == null ||
                statementMayCompleteNormally(elseBranch);
    }

    private static boolean labeledStatementMayCompleteNormally(PsiLabeledStatement labeledStatement) {
        final PsiStatement statement = labeledStatement.getStatement();
        return statementMayCompleteNormally(statement) ||
                statementIsBreakTarget(statement);
    }

    private static boolean codeBlockMayCompleteNormally(PsiCodeBlock block) {
        if (block == null) {
            return true;
        }
        final PsiStatement[] statements = block.getStatements();
        for (final PsiStatement statement : statements) {
            if (!statementMayCompleteNormally(statement)) {
                return false;
            }
        }
        return true;
    }

    private static boolean statementIsBreakTarget(PsiStatement statement) {
        final BreakFinder breakFinder = new BreakFinder(statement);
        statement.accept(breakFinder);
        return breakFinder.breakFound();
    }

    private static class BreakFinder extends JavaRecursiveElementVisitor {
        private boolean m_found = false;
        private final PsiStatement m_target;

        private BreakFinder(PsiStatement target) {
            super();
            m_target = target;
        }

        private boolean breakFound() {
            return m_found;
        }

        public void visitBreakStatement(PsiBreakStatement breakStatement) {
            super.visitBreakStatement(breakStatement);
            final PsiStatement exitedStatement = breakStatement.findExitedStatement();
            if (m_target.equals(exitedStatement)) {
                m_found = true;
            }
        }
    }
}

