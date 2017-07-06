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
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BucketedCount;

public class InformationFlowBasedCohesionClassCalculator extends ClassCalculator {
    private final BucketedCount<PsiClass> metrics = new BucketedCount<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    @Override
    public void endMetricsRun() {
        metrics.clear();
        super.endMetricsRun();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            if (!isConcreteClass(aClass)) {
                return;
            }
            metrics.createBucket(aClass);
            super.visitClass(aClass);
            postMetric(aClass, metrics.getBucketValue(aClass));
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
            // ignore
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod calledMethod = expression.resolveMethod();
            if (calledMethod == null) {
                return;
            }
            final PsiClass calledClass = calledMethod.getContainingClass();
            if (calledClass == null) {
                return;
            }
            final PsiClass aClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            if (!calledClass.equals(aClass)) {
                return;
            }
            metrics.incrementBucketValue(aClass, calledMethod.getParameterList().getParametersCount());
        }
    }
}
