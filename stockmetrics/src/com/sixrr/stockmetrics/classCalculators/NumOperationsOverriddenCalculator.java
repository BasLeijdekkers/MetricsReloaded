/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.psi.search.searches.SuperMethodsSearch;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;

public class NumOperationsOverriddenCalculator extends ClassCalculator {

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
            int numOverriddenMethods = 0;
            for (final PsiMethod method : methods) {
                if (method.isConstructor() || method.hasModifierProperty(PsiModifier.STATIC) ||
                        method.hasModifierProperty(PsiModifier.PRIVATE)) {
                    continue;
                }
                final Query<MethodSignatureBackedByPsiMethod> query =
                        SuperMethodsSearch.search(method, null, true, false);
                final boolean superMethodFound = !query.forEach(new Processor<MethodSignatureBackedByPsiMethod>() {

                    @Override
                    public boolean process(MethodSignatureBackedByPsiMethod superMethod) {
                        return MethodUtils.isAbstract(superMethod.getMethod());
                    }
                });
                if (superMethodFound) {
                    numOverriddenMethods++;
                }
            }
            postMetric(aClass, numOverriddenMethods);
        }
    }
}
