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

public class NumExecutableStatementsCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    private int elementCount = 0;

    public NumExecutableStatementsCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
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

        @Override
        public void visitExpressionListStatement(PsiExpressionListStatement statement) {
            super.visitExpressionListStatement(statement);
            elementCount++;
        }

        @Override
        public void visitExpressionStatement(PsiExpressionStatement statement) {
            super.visitExpressionStatement(statement);
            elementCount++;
        }

        @Override
        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
            super.visitDeclarationStatement(statement);
            elementCount++;
        }

        @Override
        public void visitAssertStatement(PsiAssertStatement statement) {
            super.visitAssertStatement(statement);
            elementCount++;
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            super.visitReturnStatement(statement);
            elementCount++;
        }

        @Override
        public void visitThrowStatement(PsiThrowStatement statement) {
            super.visitThrowStatement(statement);
            elementCount++;
        }
    }
}
