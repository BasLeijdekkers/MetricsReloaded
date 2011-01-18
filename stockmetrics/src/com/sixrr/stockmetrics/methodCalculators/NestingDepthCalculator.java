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
import com.sixrr.metrics.utils.MethodUtils;

public class NestingDepthCalculator extends MethodCalculator {
    private int methodNestingCount = 0;
    private int maximumDepth = 0;
    private int currentDepth = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(PsiMethod method) {
            if (methodNestingCount == 0) {
                maximumDepth = 0;
                currentDepth = 0;
            }
            methodNestingCount++;
            super.visitMethod(method);
            methodNestingCount--;
            if (methodNestingCount == 0) {
                if (!MethodUtils.isAbstract(method)) {
                    postMetric(method, maximumDepth);
                }
            }
        }

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

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            enterScope();
            super.visitDoWhileStatement(statement);
            enterScope();
        }

        public void visitForStatement(PsiForStatement statement) {
            enterScope();
            super.visitForStatement(statement);
            enterScope();
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            enterScope();
            super.visitForeachStatement(statement);
            enterScope();
        }

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
            super.visitIfStatement(statement);

            if (!isAlreadyCounted) {
                exitScope();
            }
        }

        public void visitSynchronizedStatement(PsiSynchronizedStatement statement) {
            enterScope();
            super.visitSynchronizedStatement(statement);
            exitScope();
        }

        public void visitTryStatement(PsiTryStatement statement) {
            enterScope();
            super.visitTryStatement(statement);
            exitScope();
        }

        public void visitSwitchStatement(PsiSwitchStatement statement) {
            enterScope();
            super.visitSwitchStatement(statement);
            exitScope();
        }

        public void visitWhileStatement(PsiWhileStatement statement) {
            enterScope();
            super.visitWhileStatement(statement);
            exitScope();
        }

        private void enterScope() {
            currentDepth++;
            maximumDepth = Math.max(maximumDepth, currentDepth);
        }

        private void exitScope() {
            currentDepth--;
        }
    }
}
