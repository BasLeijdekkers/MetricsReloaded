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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author Aleksandr Chudov.
 */
public class FanOutClassCalculator extends ClassCalculator {
    private final Map<PsiClass, Set<PsiClass>> metrics = new HashMap<PsiClass, Set<PsiClass>>();
    private final Collection<PsiClass> visitedClasses = new ArrayList<PsiClass>();
    private final Stack<PsiClass> classes = new Stack<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    @Override
    public void endMetricsRun() {
        for (final PsiClass aClass : visitedClasses) {
            postMetric(aClass, metrics.get(aClass).size());
        }
        super.endMetricsRun();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            metrics.put(aClass, new HashSet<PsiClass>());
            classes.push(aClass);
            visitedClasses.add(aClass);
            super.visitClass(aClass);
            classes.pop();
        }

        @Override
        public void visitCallExpression(PsiCallExpression callExpression) {
            super.visitCallExpression(callExpression);
            if (classes.empty()) {
                return;
            }
            final PsiMethod method = callExpression.resolveMethod();
            if (method == null || classes.peek().equals(method.getContainingClass())) {
                return;
            }
            final PsiClass aClass = method.getContainingClass();
            if (aClass == null) {
                return;
            }
            metrics.get(classes.peek()).add(aClass);
        }
    }
}
