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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;

public class MaximumOperationSizeCalculator extends ClassCalculator {
    private int numStatements = 0;
    private int maxNumStatements = 0;
    private int numMethods = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            int prevMaxNumStatements = 0;
            int prevNumStatements = 0;
            int prevNumMethods = 0;
            if (isConcreteClass(aClass)) {
                prevNumStatements = numStatements;
                prevMaxNumStatements = numStatements;
                prevNumMethods = numMethods;
                maxNumStatements = 0;
                numStatements = 0;
                numMethods = 0;
            }
            super.visitClass(aClass);
            if (isConcreteClass(aClass)) {
                if (numMethods != 0) {
                    postMetric(aClass, (double) maxNumStatements);
                }
                maxNumStatements = prevMaxNumStatements;
                numStatements = prevNumStatements;
                numMethods = prevNumMethods;
            }
        }

        public void visitMethod(PsiMethod method) {
            if (method.getBody() != null) {
                numStatements = 0;
                numMethods++;
            }
            super.visitMethod(method);
            if (numStatements > maxNumStatements) {
                maxNumStatements = numStatements;
            }
        }

        public void visitStatement(PsiStatement statement) {
            super.visitStatement(statement);
            if (!(statement instanceof PsiEmptyStatement) && !(statement instanceof PsiBlockStatement)) {
                numStatements++;
            }
        }
    }
}
