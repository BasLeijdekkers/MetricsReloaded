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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BucketedCount;

/**
 * @author Aleksandr Chudov.
 */
public class LocalityOfDataClassCalculator extends ClassCalculator {
    private int numberOfParameters = 0;
    private int numberOfLocalVars = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            if (isConcreteClass(aClass)) {
                numberOfParameters = 0;
                numberOfLocalVars = 0;
                super.visitClass(aClass);
                double metric = (double) numberOfLocalVars / (double) (numberOfLocalVars + numberOfParameters);
                if (numberOfLocalVars + numberOfParameters == 0) {
                    postMetric(aClass, 1);
                    return;
                }
                postMetric(aClass, metric);
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            numberOfParameters += method.getParameterList().getParametersCount();
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
            // ignore
        }

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            final PsiMethod method = PsiTreeUtil.getParentOfType(variable, PsiMethod.class);
            if (method == null) {
                return;
            }
            numberOfLocalVars++;
        }
    }
}
