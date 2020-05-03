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

public class NumOperationsInheritedCalculator extends ClassCalculator {

    public NumOperationsInheritedCalculator(Metric metric) {
        super(metric);
    }

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
            final PsiMethod[] allMethods = aClass.getAllMethods();
            int numInheritedMethods = 0;
            for (final PsiMethod method : allMethods) {
                if (method.isConstructor() || method.hasModifierProperty(PsiModifier.PRIVATE) ||
                        method.hasModifierProperty(PsiModifier.STATIC) ||
                        method.hasModifierProperty(PsiModifier.ABSTRACT)) {
                    continue;
                }
                final PsiClass containingClass = method.getContainingClass();
                if (containingClass == null || containingClass.equals(aClass)) {
                    continue;
                }
                final PsiMethod localMethod = aClass.findMethodBySignature(method, false);
                if (localMethod == null) {
                    numInheritedMethods++;
                }
            }

            postMetric(aClass, numInheritedMethods);
        }
    }
}
