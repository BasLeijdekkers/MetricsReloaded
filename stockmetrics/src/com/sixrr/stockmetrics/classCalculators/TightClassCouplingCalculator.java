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

import com.intellij.codeInsight.dataflow.SetUtil;
import com.intellij.psi.*;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.stockmetrics.utils.FieldUsageUtil;

import java.util.*;

/**
 * @author Aleksandr Chudov.
 * Number of method pairs that access common attributes over the total number of method pairs
 */
public class TightClassCouplingCalculator extends MethodPairsCountClassCalculator {
//    private final Map<PsiMethod, Set<PsiField>> methodsToFields = new HashMap<PsiMethod, Set<PsiField>>();
//    private final Collection<PsiClass> visitedClasses = new ArrayList<PsiClass>();

    @Override
    public void endMetricsRun() {
/*        for (final PsiClass aClass : visitedClasses) {
            final List<PsiMethod> methods = Arrays.asList(aClass.getMethods());
            final int n = methods.size();
            if (n < 2) {
                postMetric(aClass, 0);
                continue;
            }
            int result = 0;
            for (int i = 0; i < methods.size(); i++) {
                for (int j = i + 1; j < methods.size(); j++) {
                    final Set<PsiField> a = methodsToFields.get(methods.get(i));
                    final Set<PsiField> b = methodsToFields.get(methods.get(j));
                    if (a == null || b == null) {
                        continue;
                    }
                    if (!SetUtil.intersect(a, b).isEmpty()) {
                        result++;
                    }
                }
            }
            postMetric(aClass, result, n * (n - 1) / 2);
        }
*/
        final BucketedCount<PsiClass> metrics = calculatePairs();
        for (final PsiClass aClass : metrics.getBuckets()) {
            final int n = aClass.getMethods().length;
            if (n < 2) {
                postMetric(aClass, 0);
            }
            else {
                postMetric(aClass, metrics.getBucketValue(aClass), n * (n - 1) / 2);
            }
        }
        super.endMetricsRun();
    }

    /*@Override
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
            visitedClasses.add(aClass);
            final Map<PsiField, Set<PsiMethod>> fieldToMethods = FieldUsageUtil.getFieldUsagesInMethods(executionContext, aClass);
            for (final Map.Entry<PsiField, Set<PsiMethod>> e : fieldToMethods.entrySet()) {
                for (final PsiMethod method : e.getValue()) {
                    if (!methodsToFields.containsKey(method)) {
                        methodsToFields.put(method, new HashSet<PsiField>());
                    }
                    methodsToFields.get(method).add(e.getKey());
                }
            }
        }
    }*/
}
