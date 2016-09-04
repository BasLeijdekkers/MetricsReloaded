/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.psi.tree.IElementType;
import com.sixrr.metrics.utils.MethodUtils;

/**
 * @author Bas Leijdekkers
 */
public class NumNullChecksCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private int elementCount = 0;

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
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            final IElementType tokenType = expression.getOperationTokenType();
            if (!JavaTokenType.EQEQ.equals(tokenType) && !JavaTokenType.NE.equals(tokenType)) {
                return;
            }
            for (PsiExpression operand : expression.getOperands()) {
                if (operand instanceof PsiLiteralExpression && PsiType.NULL.equals(operand.getType())) {
                    elementCount++;
                }
            }
        }
    }
}
