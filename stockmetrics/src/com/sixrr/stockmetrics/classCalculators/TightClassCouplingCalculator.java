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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.Set;

import static com.sixrr.stockmetrics.utils.MethodsCohesionUtils.calculateConnectedMethods;
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
            final Set<PsiMethod> applicableMethods = getApplicableMethods(aClass);
            final int allPairs = applicableMethods.size() * (applicableMethods.size() - 1) / 2;
            final int connectedPairs = calculateConnectedMethods(applicableMethods);
            postMetric(aClass, connectedPairs, allPairs);
        }
    }
}
