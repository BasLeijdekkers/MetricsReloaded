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

public class LocalityOfDataClassCalculator extends ClassCalculator {
    private final Map<PsiMethod, Set<PsiField>> usedFields = new HashMap<PsiMethod, Set<PsiField>>();
    private final Map<PsiMethod, Set<PsiField>> usedLocalFields = new HashMap<PsiMethod, Set<PsiField>>();
    private final BucketedCount<PsiClass> localVars = new BucketedCount<PsiClass>();
    private final BucketedCount<PsiClass> allVars = new BucketedCount<PsiClass>();

    @Override
    public void endMetricsRun() {
        usedFields.clear();
        usedLocalFields.clear();
        localVars.clear();
        allVars.clear();
        super.endMetricsRun();
    }

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

            super.visitClass(aClass);
            double metric = 1.0;
            if (allVars.getBucketValue(aClass) > 0) {
                metric = (double) localVars.getBucketValue(aClass) / (double) allVars.getBucketValue(aClass);
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

            allVars.incrementBucketValue(aClass, method.getParameterList().getParametersCount());
            usedFields.put(method, new HashSet<PsiField>());
            usedLocalFields.put(method, new HashSet<PsiField>());
            super.visitMethod(method);
            allVars.incrementBucketValue(aClass, usedFields.get(method).size());
            localVars.incrementBucketValue(aClass, usedLocalFields.get(method).size());

            usedFields.remove(method);
            usedLocalFields.remove(method);
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

            final Set<PsiField> fields = MethodUtils.getUsedFields(calledMethod);
            usedFields.get(method).addAll(fields);
            for (final PsiField field : fields) {
                if (isLocal(aClass, field)) {
                    usedLocalFields.get(method).add(field);
                }
            }
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
            final PsiMethod method = PsiTreeUtil.getParentOfType(expression, PsiMethod.class);
            if (aClass == null || method == null) {
                return;
            }

            if (element instanceof PsiField) {
                final PsiField field = (PsiField) element;
                usedFields.get(method).add(field);
                if (isLocal(aClass, field)) {
                    usedLocalFields.get(method).add(field);
                }
            }
        }

        private boolean isLocal(final PsiClass aClass, final PsiField field) {
            final PsiClass fieldClass = field.getContainingClass();
            if (fieldClass == null) {
                return false;
            }

            if (aClass.equals(fieldClass)) {
                return !field.hasModifierProperty(PsiModifier.PUBLIC);
            }

            return aClass.isInheritor(fieldClass, true) && field.hasModifierProperty(PsiModifier.PROTECTED);
        }
    }
}
