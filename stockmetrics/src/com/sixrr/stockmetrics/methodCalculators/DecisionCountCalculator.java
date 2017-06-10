/*
 * Copyright 2005-2017 Sixth and Red River Software, Bas Leijdekkers
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
import com.sixrr.stockmetrics.utils.ExpressionUtils;

public class DecisionCountCalculator extends MethodCalculator {
    private int count = 0;
    private int depth = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            count++;
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (!ExpressionUtils.isEvaluatedAtCompileTime(statement.getCondition())) {
                count++;
            }
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            if (!ExpressionUtils.isEvaluatedAtCompileTime(statement.getCondition())) {
                count++;
            }
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            if (!ExpressionUtils.isEvaluatedAtCompileTime(statement.getCondition())) {
                count++;
            }
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            count++;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if (depth == 0) {
                count = 0;
            }
            depth++;
            super.visitMethod(method);
            depth--;
            if (depth == 0) {
                postMetric(method, count);
            }
        }
    }
}
