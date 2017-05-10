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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;
import com.sixrr.stockmetrics.utils.FieldUsageUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public class LackOfCohesionInMethods2ClassCalculator extends ClassCalculator {
    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    protected class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (!isConcreteClass(aClass)) {
                return;
            }
            final int fieldsNumber = aClass.getFields().length;
            final int n = aClass.getMethods().length;
            if (n == 1) {
                postMetric(aClass, 0);
                return;
            }
            if (fieldsNumber == 0) {
                postMetric(aClass, -n, n - 1);
                return;
            }
            final Map<PsiField, Set<PsiMethod>> fieldToMethods = FieldUsageUtil.getFieldUsagesInMethods(executionContext, aClass);
            final Map<PsiMethod, Set<PsiField>> methodsToFields = new HashMap<PsiMethod, Set<PsiField>>();
            for (final Map.Entry<PsiField, Set<PsiMethod>> e : fieldToMethods.entrySet()) {
                if (!aClass.equals(e.getKey().getContainingClass())) {
                    continue;
                }
                for (final PsiMethod method : e.getValue()) {
                    if (!methodsToFields.containsKey(method)) {
                        methodsToFields.put(method, new HashSet<PsiField>());
                    }
                    methodsToFields.get(method).add(e.getKey());
                }
            }
            int eachFieldMethodNumber = 0;
            for (final Map.Entry<PsiMethod, Set<PsiField>> e : methodsToFields.entrySet()) {
                if (e.getValue().size() == fieldsNumber) {
                    eachFieldMethodNumber++;
                }
            }
            postMetric(aClass, eachFieldMethodNumber * (n - 1) - n, n - 1);
        }
    }
}
