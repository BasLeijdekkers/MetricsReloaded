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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.calculateFieldToMethodUsage;
import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.getApplicableMethods;

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

            final Set<PsiMethod> applicableMethods = getApplicableMethods(aClass);
            final int n = applicableMethods.size();
            if (n <= 1) {
                postMetric(aClass, 0);
                return;
            }

            final Set<PsiField> fields = new HashSet<PsiField>(Arrays.asList(aClass.getFields()));
            final int fieldsNumber = fields.size();
            final Map<PsiField, Set<PsiMethod>> fieldToMethods =
                    calculateFieldToMethodUsage(fields, applicableMethods);
            int fieldsUsagesSum = 0;
            for (final Map.Entry<PsiField, Set<PsiMethod>> e : fieldToMethods.entrySet()) {
                fieldsUsagesSum += e.getValue().size();
            }

            final double averageFieldUsage = fieldsNumber == 0 ? 0.0 : (double) fieldsUsagesSum / fieldsNumber;
            postMetric(aClass, Math.min(1.0, ((double) n - averageFieldUsage) / (double) (n - 1)));
        }
    }
}
