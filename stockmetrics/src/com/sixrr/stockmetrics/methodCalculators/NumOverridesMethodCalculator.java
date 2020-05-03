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
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.util.Query;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.MethodUtils;

public class NumOverridesMethodCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;

    public NumOverridesMethodCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(final PsiMethod method) {
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                final int numberOfOverrides = calculateNumberOfOverrides(method);
                if (numberOfOverrides >= 0) {
                    postMetric(method, numberOfOverrides);
                }
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
        }

        private int calculateNumberOfOverrides(PsiMethod method) {
            if (method.isConstructor() ||
                    method.hasModifierProperty(PsiModifier.STATIC) ||
                    method.hasModifierProperty(PsiModifier.FINAL) ||
                    method.hasModifierProperty(PsiModifier.PRIVATE))
                return -1;
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null ||
                    containingClass instanceof PsiAnonymousClass ||
                    containingClass.hasModifierProperty(PsiModifier.FINAL)) {
                return -1;
            }
            final Query<PsiMethod> query = OverridingMethodsSearch.search(method);
            return query.findAll().size();
        }
    }
}
