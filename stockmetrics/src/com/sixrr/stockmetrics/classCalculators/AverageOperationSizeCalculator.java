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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.Metric;

public class AverageOperationSizeCalculator extends ClassCalculator {

    private int numStatements = 0;
    private int numMethods = 0;

    public AverageOperationSizeCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            final int prevNumMethods = numMethods;
            final int prevNumStatements = numStatements;
            if (isConcreteClass(aClass)) {
                numStatements = 0;
                numMethods = 0;
            }
            super.visitClass(aClass);
            if (isConcreteClass(aClass)) {
                if (numMethods != 0) {
                    postMetric(aClass, numStatements, numMethods);
                }
                numStatements = prevNumStatements;
                numMethods = prevNumMethods;
            }
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            if (method.getBody() != null) {
                numMethods++;
            }
        }

        @Override
        public void visitStatement(PsiStatement statement) {
            super.visitStatement(statement);
            if (!(statement instanceof PsiEmptyStatement) &&
                    !(statement instanceof PsiBlockStatement)) {
                numStatements++;
            }
        }
    }
}
