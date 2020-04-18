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
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;

public class FanInMethodCalculator extends MethodCalculator {
    private PsiMethod currentMethod;
    private int methodNestingDepth = 0;
    private int result = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                result = 0;
                currentMethod = method;
                final Query<PsiReference> references = ReferencesSearch.search(method);
                for (final PsiReference reference : references) {
                    final PsiElement element = reference.getElement();
                    if (element != null && element.getParent() instanceof PsiCallExpression) {
                        result++;
                    }
                }
            }

            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;

            if (methodNestingDepth == 0) {
                postMetric(method, result);
            }
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            final PsiMethod method = expression.resolveMethod();
            if (currentMethod != null && currentMethod.equals(method)) {
                result--;
            }
            super.visitMethodCallExpression(expression);
        }
    }
}
