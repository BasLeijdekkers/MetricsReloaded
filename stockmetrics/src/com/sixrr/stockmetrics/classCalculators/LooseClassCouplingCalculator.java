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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.*;

public class LooseClassCouplingCalculator extends ClassCalculator {
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
            final Set<PsiMethod> applicableMethods = getApplicableMethods(aClass);
            final int allPairs = applicableMethods.size() * (applicableMethods.size() - 1) / 2;
            final Map<PsiMethod, Set<PsiField>> fieldsPerMethod = calculateFieldUsage(applicableMethods);
            final Set<Set<PsiMethod>> components = calculateComponents(applicableMethods, fieldsPerMethod,
                    new HashMap<PsiMethod, Set<PsiMethod>>());
            int metric = 0;
            for (final Set<PsiMethod> methods : components) {
                final int n = methods.size();
                metric += n * (n - 1) / 2;
            }
            postMetric(aClass, metric, allPairs);
        }
    }
}
