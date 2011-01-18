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

public class ConditionalNestingDepthCalculator extends MethodCalculator {
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

        private void enterScope() {
            currentDepth++;
            maximumDepth = Math.max(maximumDepth, currentDepth);
        }

        private void exitScope() {
            currentDepth--;
        }
    }
}
