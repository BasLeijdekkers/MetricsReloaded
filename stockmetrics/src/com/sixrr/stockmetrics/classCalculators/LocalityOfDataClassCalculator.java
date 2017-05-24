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
import com.sixrr.metrics.utils.MethodUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public class LocalityOfDataClassCalculator extends ClassCalculator {
    private final Map<PsiClass, Set<PsiField>> usedFields = new HashMap<PsiClass, Set<PsiField>>();
    private final BucketedCount<PsiClass> params = new BucketedCount<PsiClass>();

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
            usedFields.put(aClass, new HashSet<PsiField>());
            params.createBucket(aClass);
            super.visitClass(aClass);
            int localFields = 0;
            for (final PsiField field : usedFields.get(aClass)) {
                final PsiClass fieldClass = field.getContainingClass();
                if (fieldClass == null) {
                    continue;
                }
                if (aClass.equals(fieldClass)) {
                    if (!field.hasModifierProperty(PsiModifier.PUBLIC)) {
                        localFields++;
                    }
                    continue;
                }
                if (aClass.isInheritor(fieldClass, true)) {
                    if (field.hasModifierProperty(PsiModifier.PROTECTED)) {
                        localFields++;
                    }
                }
            }
            double metric = (double) localFields / (double) (params.getBucketValue(aClass) + usedFields.get(aClass).size());
            if (Double.isInfinite(metric) || Double.isNaN(metric)) {
                metric = 1.0;
            }
            postMetric(aClass, metric);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if (MethodUtils.isTrivialGetterOrSetter(method)) {
                return;
            }
            final PsiClass aClass = method.getContainingClass();
            if (aClass == null) {
                return;
            }
            params.incrementBucketValue(aClass, method.getParameterList().getParametersCount());
            super.visitMethod(method);
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiMethod calledMethod = expression.resolveMethod();
            if (calledMethod == null || !MethodUtils.isTrivialGetterOrSetter(calledMethod)) {
                return;
            }
            final PsiMethod method = PsiTreeUtil.getParentOfType(expression, PsiMethod.class);
            final PsiClass aClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            if (method == null || aClass == null) {
                return;
            }
            final PsiClass methodClass = method.getContainingClass();
            if (!aClass.equals(methodClass)) {
                return;
            }
            usedFields.get(aClass).add(MethodUtils.getUsedFields(calledMethod).iterator().next());
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression expression) {
            // ignore
        }

        @Override
        public void visitReferenceExpression(PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);
            final PsiElement element = expression.resolve();
            final PsiClass aClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            if (aClass == null) {
                return;
            }
            if (element instanceof PsiField) {
                final PsiField field = (PsiField) element;
                usedFields.get(aClass).add(field);
            }
        }
    }
}
