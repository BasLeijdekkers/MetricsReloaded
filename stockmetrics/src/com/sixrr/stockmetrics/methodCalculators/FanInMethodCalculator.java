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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.BucketedCount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class FanInMethodCalculator extends MethodCalculator {
    private final BucketedCount<PsiMethod> metrics = new BucketedCount<PsiMethod>();
    private final Collection<PsiMethod> visitedMethods = new ArrayList<PsiMethod>();
    private final Stack<PsiMethod> methods = new Stack<PsiMethod>();

    @Override
    public void endMetricsRun() {
        for (PsiMethod method : visitedMethods) {
            postMetric(method, metrics.getBucketValue(method));
        }
        super.endMetricsRun();
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            methods.push(method);
            visitedMethods.add(method);
            super.visitMethod(method);
            methods.pop();
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
        }

        @Override
        public void visitCallExpression(PsiCallExpression callExpression) {
            super.visitCallExpression(callExpression);
            final PsiMethod method = callExpression.resolveMethod();
            if (method == null || !methods.empty() && methods.peek().equals(method)) {
                return;
            }
            metrics.incrementBucketValue(method);
        }
    }
}
