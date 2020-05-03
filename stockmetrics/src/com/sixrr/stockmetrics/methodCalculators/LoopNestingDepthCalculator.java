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
import com.sixrr.metrics.utils.MethodUtils;

public class LoopNestingDepthCalculator extends MethodCalculator {

    private int methodNestingCount = 0;
    private int maximumDepth = 0;
    private int currentDepth = 0;

    public LoopNestingDepthCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
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

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            enterScope();
            super.visitDoWhileStatement(statement);
            exitScope();
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            enterScope();
            super.visitWhileStatement(statement);
            exitScope();
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            enterScope();
            super.visitForStatement(statement);
            exitScope();
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            enterScope();
            super.visitForeachStatement(statement);
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
