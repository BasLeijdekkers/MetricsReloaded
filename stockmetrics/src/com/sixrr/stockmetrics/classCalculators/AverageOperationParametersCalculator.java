/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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

public class AverageOperationParametersCalculator extends ClassCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (!isConcreteClass(aClass)) {
                return;
            }
            int parameterCount = 0;
            final PsiMethod[] methods = aClass.getMethods();
            for (final PsiMethod method : methods) {
                final PsiParameterList parameterList = method.getParameterList();
                final PsiParameter[] parameters = parameterList.getParameters();
                parameterCount += parameters.length;
            }
            if (methods.length == 0) {
                postMetric(aClass, 0);
            } else {
                postMetric(aClass, parameterCount, methods.length);
            }
        }
    }
}
