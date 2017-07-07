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
import com.sixrr.stockmetrics.utils.SetUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.calculateFieldToMethodUsage;
import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.getApplicableMethods;

public class LackOfCohesionInMethods5ClassCalculator extends ClassCalculator {
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
            final Map<PsiField, Set<PsiMethod>> fieldToMethods =
                    calculateFieldToMethodUsage(new HashSet<PsiField>(Arrays.asList(aClass.getFields())), getApplicableMethods(aClass));
            final PsiField[] fields = aClass.getFields();
            if (fields.length < 2) {
                postMetric(aClass, 0);
                return;
            }
            double sumOfJaccardDistances = 0.0;
            for (int i = 0; i < fields.length; i++) {
                for (int j = i + 1; j < fields.length; j++) {
                    final Set<PsiMethod> a = fieldToMethods.get(fields[i]);
                    final Set<PsiMethod> b = fieldToMethods.get(fields[j]);
                    sumOfJaccardDistances += SetUtil.jaccardDistance(a, b);
                }
            }
            postMetric(aClass, sumOfJaccardDistances / (double) (fields.length * (fields.length - 1) / 2));
        }
    }
}
