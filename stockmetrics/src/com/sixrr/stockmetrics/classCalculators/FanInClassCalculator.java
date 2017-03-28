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
import com.intellij.psi.PsiMethodCallExpression;

import java.util.Stack;

/**
 * Created by Aleksandr Chudov on 27.03.2017.
 */
public class FanInClassCalculator extends ClassCalculator {
    private final Stack<Integer> fanInMetrics = new Stack<Integer>();
    private int currentMetric = -1;
    private final Stack<PsiClass> classes = new Stack<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            if (currentMetric != -1) {
                fanInMetrics.push(Integer.valueOf(currentMetric));
            }
            classes.push(aClass);
            currentMetric = 0;
            super.visitClass(aClass);
            postMetric(aClass, currentMetric);
            classes.pop();
            currentMetric = fanInMetrics.empty() ? -1 : fanInMetrics.pop().intValue();
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (classes.empty()) {
                return;
            }
            if (classes.peek().equals(expression.resolveMethod().getContainingClass())) {
                return;
            }
            currentMetric++;
        }
    }
}
