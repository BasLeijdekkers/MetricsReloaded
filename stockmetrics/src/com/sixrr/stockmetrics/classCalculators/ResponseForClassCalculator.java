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
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ResponseForClassCalculator extends ClassCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            if (ClassUtils.isAnonymous(aClass)) {
                return;
            }
            super.visitClass(aClass);
            final Set<PsiMethod> methodsCalled = new HashSet<>();
            // class and field initializers are considered part of the constructor and not counted
            Collections.addAll(methodsCalled, aClass.getMethods());
            aClass.acceptChildren(new JavaRecursiveElementVisitor() {

                @Override
                public void visitClass(PsiClass aClass) {
                    // do not recurse into anonymous, inner and local classes
                }

                @Override
                public void visitCallExpression(PsiCallExpression callExpression) {
                    super.visitCallExpression(callExpression);
                    final PsiMethod target = callExpression.resolveMethod();
                    if (target != null) {
                        methodsCalled.add(target);
                    }
                }
            });
            final int numMethods = methodsCalled.size();
            postMetric(aClass, numMethods);
        }
    }
}
