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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.MethodUtils;
import com.sixrr.stockmetrics.utils.CognitiveComplexity;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bas Leijdekkers
 */
public class CognitiveComplexityCalculator extends MethodCalculator {

    public CognitiveComplexityCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(@NotNull PsiMethod method) {
            super.visitMethod(method);
            if (MethodUtils.isAbstract(method)) {
                return;
            }
            final int complexity = CognitiveComplexity.calculate(method.getBody());
            postMetric(method, complexity);
        }
    }
}
