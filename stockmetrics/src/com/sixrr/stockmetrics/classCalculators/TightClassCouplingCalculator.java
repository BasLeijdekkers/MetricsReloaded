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

import java.util.Map;
import java.util.Set;

import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.calculateFieldUsage;
import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.getApplicableMethods;

public class TightClassCouplingCalculator extends ClassCalculator {
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
            // TODO extract to utils
            final Map<PsiMethod, Set<PsiField>> fieldUsage = calculateFieldUsage(getApplicableMethods(aClass));
            final PsiMethod[] applicableMethods = getApplicableMethods(aClass).toArray(new PsiMethod[0]);
            final int allPairs = applicableMethods.length * (applicableMethods.length - 1) / 2;
            int connectedPairs = 0;
            for (int i = 0; i < applicableMethods.length; i++) {
                for (int j = i + 1; j < applicableMethods.length; j++) {
                    if (SetUtil.hasIntersec(fieldUsage.get(applicableMethods[i]), fieldUsage.get(applicableMethods[j]))) {
                        connectedPairs++;
                    }
                }
            }
            postMetric(aClass, connectedPairs, allPairs);
        }
    }
}
