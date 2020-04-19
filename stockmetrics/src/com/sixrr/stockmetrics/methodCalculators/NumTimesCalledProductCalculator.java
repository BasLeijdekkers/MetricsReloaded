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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.sixrr.stockmetrics.utils.MethodCallMap;
import com.sixrr.stockmetrics.utils.MethodCallMapImpl;

import java.util.Set;

public class NumTimesCalledProductCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                final Key<MethodCallMap> key = new Key<>("MethodCallMap");

                MethodCallMap methodCallMap = executionContext.getUserData(key);
                if (methodCallMap == null) {
                    methodCallMap = new MethodCallMapImpl();
                    executionContext.putUserData(key, methodCallMap);
                }
                final Set<PsiReference> methodCalls = methodCallMap.calculateProductMethodCallPoints(method);
                final int calls = methodCalls.size();
                postMetric(method, calls);
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
        }
    }
}
