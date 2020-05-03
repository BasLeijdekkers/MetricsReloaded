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
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;

public class NumOperationsAddedCalculator extends ClassCalculator {

    public NumOperationsAddedCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(final PsiClass aClass) {
            super.visitClass(aClass);
            if (ClassUtils.isAnonymous(aClass) || aClass.isInterface()) {
                return;
            }
            final PsiMethod[] methods = aClass.getMethods();
            int numAddedMethods = 0;
            for (final PsiMethod method : methods) {
                if (method.isConstructor() || method.hasModifierProperty(PsiModifier.ABSTRACT)) {
                    continue;
                }
                if (method.hasModifierProperty(PsiModifier.PRIVATE) || method.hasModifierProperty(PsiModifier.STATIC)) {
                    numAddedMethods++;
                    continue;
                }
                if (!MethodUtils.hasConcreteSuperMethod(method)) {
                    numAddedMethods++;
                }
            }
            postMetric(aClass, numAddedMethods);
        }
    }
}
