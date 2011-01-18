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

public class NumControlStatementsCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private int elementCount = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                elementCount = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                postMetric(method, elementCount);
            }
        }

        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            elementCount++;
        }

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            elementCount++;
        }

        public void visitContinueStatement(PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
            elementCount++;
        }

        public void visitBreakStatement(PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
            elementCount++;
        }

        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            elementCount++;
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            elementCount++;
        }

        public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
            super.visitSwitchLabelStatement(statement);
            elementCount++;
        }

        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            elementCount++;
        }

        public void visitSynchronizedStatement(PsiSynchronizedStatement statement) {
            super.visitSynchronizedStatement(statement);
            elementCount++;
        }

        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
            elementCount++;
        }

        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            elementCount++;
        }
    }
}
