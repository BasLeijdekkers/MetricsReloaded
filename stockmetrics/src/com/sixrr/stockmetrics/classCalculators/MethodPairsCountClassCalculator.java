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
import com.intellij.util.containers.Predicate;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.stockmetrics.utils.FieldUsageUtil;
import com.sixrr.stockmetrics.utils.SetUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public abstract class MethodPairsCountClassCalculator extends ClassCalculator {
    protected final Map<PsiMethod, Set<PsiField>> methodsToFields = new HashMap<PsiMethod, Set<PsiField>>();
    private final Set<PsiClass> visitedClasses = new HashSet<PsiClass>();

    protected static class MethodPair {
        @NotNull
        public final PsiMethod method1;
        @NotNull
        public final PsiMethod method2;

        public MethodPair(@NotNull final PsiMethod method1, @NotNull final PsiMethod method2) {
            this.method1 = method1;
            this.method2 = method2;
        }
    }

    protected int getVisibleMethodsCount(final PsiClass aClass) {
        int count = 0;
        for (final PsiMethod method : aClass.getAllMethods()) {
            if (visitedClasses.contains(method.getContainingClass())) {
                count++;
            }
        }
        return count;
    }

    protected PsiMethod[] getVisibleMethods(final PsiClass aClass) {
        final PsiMethod[] result = new PsiMethod[getVisibleMethodsCount(aClass)];
        int i = 0;
        for (final PsiMethod method : aClass.getAllMethods()) {
            if (visitedClasses.contains(method.getContainingClass())) {
                result[i] = method;
                i++;
            }
        }
        return result;
    }

    private boolean hasCommonFields(final MethodPair methods) {
        final Set<PsiField> a = methodsToFields.get(methods.method1);
        final Set<PsiField> b = methodsToFields.get(methods.method2);
        return SetUtil.hasIntersec(a, b);
    }

    protected BucketedCount<PsiClass> calculatePairs() {
        return calculatePairs(new Predicate<MethodPair>() {
            @Override
            public boolean apply(@Nullable MethodPair methodPair) {
                return hasCommonFields(methodPair);
            }
        });
    }

    protected BucketedCount<PsiClass> calculatePairs(Predicate<MethodPair> isSuitable) {
        final BucketedCount<PsiClass> results = new BucketedCount<PsiClass>();
        for (final PsiClass aClass : visitedClasses) {
            results.incrementBucketValue(aClass, calculatePairs(aClass, isSuitable));
        }
        return results;
    }

    protected int calculatePairs(final PsiClass aClass, final Predicate<MethodPair> isSuitable) {
        return calculatePairs(aClass.getMethods(), isSuitable);

    }
    protected int calculatePairs(final PsiMethod[] methods, final Predicate<MethodPair> isSuitable) {
        int result = 0;
        for (int i = 0; i < methods.length; i++) {
            for (int j = i + 1; j < methods.length; j++) {
                if (isSuitable.apply(new MethodPair(methods[i], methods[j]))) {
                    result++;
                }
            }
        }
        return result;
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new MethodPairsCountClassVisitor();
    }

    protected class MethodPairsCountClassVisitor extends JavaRecursiveElementVisitor {
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
    }
}
