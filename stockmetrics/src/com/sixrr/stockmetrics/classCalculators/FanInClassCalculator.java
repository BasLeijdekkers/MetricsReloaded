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
import com.sixrr.stockmetrics.utils.FieldUsageMap;

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
public class FanInClassCalculator extends FanClassCalculator {
    private final Stack<PsiClass> classes = new Stack<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            if (!isConcreteClass(aClass)) {
                return;
            }
            if (!metrics.containsKey(aClass)) {
                metrics.put(aClass, new HashSet<PsiClass>());
            }
            classes.push(aClass);
            visitedClasses.add(aClass);
            super.visitClass(aClass);
            classes.pop();
            final FieldUsageMap map = executionContext.getUserData(fieldUsageKey);
            final PsiField[] fields = aClass.getFields();
            for (final PsiField field : fields) {
                final Set<PsiReference> references = map.calculateFieldUsagePoints(field);
                for (final PsiReference reference : references) {
                    final PsiElement element = reference.getElement();
                    final PsiClass fieldClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
                    if (fieldClass == null || fieldClass.equals(aClass)) {
                        continue;
                    }
                    final Set<PsiClass> classes = metrics.get(aClass);
                    classes.add(fieldClass);
                }
            }
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
        }

        @Override
        public void visitCallExpression(PsiCallExpression callExpression) {
            super.visitCallExpression(callExpression);
            final PsiMethod method = callExpression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiClass aClass = method.getContainingClass();
            if (aClass == null || classes.empty() || classes.peek().equals(aClass)) {
                return;
            }
            final Set<PsiClass> s = metrics.containsKey(aClass) ? metrics.get(aClass) : new HashSet<PsiClass>();
            s.add(classes.peek());
            metrics.put(aClass, s);
        }
    }
}
