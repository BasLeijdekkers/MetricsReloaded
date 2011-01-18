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

public class ControlDensityCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private int numerator = 0;
    private int denominator = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                numerator = 0;
                denominator = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                if (denominator == 0) {
                    postMetric(method, 0);
                } else {
                    postMetric(method, numerator, denominator);
                }
            }
        }

        public void visitStatement(PsiStatement statement) {
            super.visitStatement(statement);
            if (!(statement instanceof PsiEmptyStatement) &&
                    !(statement instanceof PsiBlockStatement)) {
                denominator++;
            }
        }

        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            numerator++;
        }

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            numerator++;
        }

        public void visitContinueStatement(PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
            numerator++;
        }

        public void visitBreakStatement(PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
            numerator++;
        }

        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            numerator++;
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            numerator++;
        }

        public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
            super.visitSwitchLabelStatement(statement);
            numerator++;
        }

        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            numerator++;
        }

        public void visitSynchronizedStatement(PsiSynchronizedStatement statement) {
            super.visitSynchronizedStatement(statement);
            numerator++;
        }

        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
            numerator++;
        }

        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            numerator++;
        }
    }
}
