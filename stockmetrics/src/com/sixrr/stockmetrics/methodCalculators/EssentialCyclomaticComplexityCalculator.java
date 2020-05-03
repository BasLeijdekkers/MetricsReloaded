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

public class EssentialCyclomaticComplexityCalculator extends ComplexityCalculator {

    public EssentialCyclomaticComplexityCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public boolean isReducible(PsiElement element) {
        if (element == null) {
            return true;
        }
        if (element instanceof PsiReturnStatement || element instanceof PsiThrowStatement ||
                element instanceof PsiContinueStatement) {
            return false;
        } else if (element instanceof PsiBreakStatement) {
            final PsiBreakStatement breakStatement = (PsiBreakStatement) element;
            return isBreakStatementReducible(breakStatement);
        } else if (element instanceof PsiIfStatement) {
            final PsiIfStatement ifStatement = (PsiIfStatement) element;
            return isReducible(ifStatement.getThenBranch()) && isReducible(ifStatement.getElseBranch());
        } else if (element instanceof PsiLoopStatement) {
            final PsiLoopStatement whileStatement = (PsiLoopStatement) element;
            return isReducible(whileStatement.getBody());
        } else if (element instanceof PsiSynchronizedStatement) {
            final PsiSynchronizedStatement synchronizedStatement = (PsiSynchronizedStatement) element;
            return isReducible(synchronizedStatement.getBody());
        } else if (element instanceof PsiTryStatement) {
            final PsiTryStatement tryStatement = (PsiTryStatement) element;
            return tryStatementIsReducible(tryStatement);
        } else if (element instanceof PsiSwitchStatement) {
            final PsiSwitchStatement switchStatement = (PsiSwitchStatement) element;
            return isReducible(switchStatement.getBody());
        } else if (element instanceof PsiBlockStatement) {
            final PsiBlockStatement blockStatement = (PsiBlockStatement) element;
            return isReducible(blockStatement.getCodeBlock());
        } else if (element instanceof PsiCodeBlock) {
            return codeBlockIsReducible((PsiCodeBlock) element);
        }
        return true;
    }

    private static boolean isBreakStatementReducible(PsiBreakStatement breakStatement) {
        if (breakStatement.getLabelIdentifier() != null) {
            return false;
        }
        return breakStatement.findExitedStatement() instanceof PsiSwitchStatement;
    }

    private boolean tryStatementIsReducible(PsiTryStatement statement) {
        final PsiCodeBlock tryBlock = statement.getTryBlock();
        if (!isReducible(tryBlock)) {
            return false;
        }
        final PsiCodeBlock[] catchBlocks = statement.getCatchBlocks();
        for (final PsiCodeBlock catchBlock : catchBlocks) {
            if (!isReducible(catchBlock)) {
                return false;
            }
        }
        final PsiCodeBlock finallyBlock = statement.getFinallyBlock();
        return isReducible(finallyBlock);
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
}
