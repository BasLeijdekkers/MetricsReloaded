/*
 * Copyright 2005-2016 Sixth and Red River Software
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

package com.sixrr.stockmetrics;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.TestUtils;

import java.util.*;

public class MethodCallMapImpl implements MethodCallMap {
    private final Map<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>> methodToCallPointMap =
            new HashMap<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>>(1024);
    private final Map<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>> methodToTestCallPointMap =
            new HashMap<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>>(1024);
    private final Map<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>> methodToProductCallPointMap =
            new HashMap<SmartPsiElementPointer<PsiMethod>, Set<PsiReference>>(1024);

    public Set<PsiReference> calculateMethodCallPoints(PsiMethod method) {
        final SmartPointerManager manager = SmartPointerManager.getInstance(method.getProject());
        final SmartPsiElementPointer<PsiMethod> pointer = manager.createSmartPsiElementPointer(method);
        if (!methodToCallPointMap.containsKey(pointer)) {
            calculateCalls(method);
        }
        return methodToCallPointMap.get(pointer);
    }

    public Set<PsiReference> calculateTestMethodCallPoints(PsiMethod method) {
        final SmartPointerManager manager = SmartPointerManager.getInstance(method.getProject());
        final SmartPsiElementPointer<PsiMethod> pointer = manager.createSmartPsiElementPointer(method);
        if (!methodToTestCallPointMap.containsKey(pointer)) {
            calculateCalls(method);
        }
        return methodToTestCallPointMap.get(pointer);
    }

    public Set<PsiReference> calculateProductMethodCallPoints(
            PsiMethod method) {
        final SmartPointerManager manager = SmartPointerManager.getInstance(method.getProject());
        final SmartPsiElementPointer<PsiMethod> pointer = manager.createSmartPsiElementPointer(method);
        if (!methodToProductCallPointMap.containsKey(pointer)) {
            calculateCalls(method);
        }
        return methodToProductCallPointMap.get(pointer);
    }

    private void calculateCalls(final PsiMethod method) {
        final Set<PsiReference> allCalls = new HashSet<PsiReference>(4);
        final Set<PsiReference> testCalls = new HashSet<PsiReference>(4);
        final Set<PsiReference> productCalls = new HashSet<PsiReference>(4);
        final PsiManager psiManager = method.getManager();
        final Project project = psiManager.getProject();
        final SearchScope scope = GlobalSearchScope.projectScope(project);
        final Runnable runnable = new Runnable() {
            public void run() {
                final Query<PsiReference> query =
                        ReferencesSearch.search(method, scope, false);
                final Collection<PsiReference> calls = query.findAll();
                for (final PsiReference reference : calls) {
                    final PsiElement element = reference.getElement();

                    final PsiClass referencingClass =
                            PsiTreeUtil.getParentOfType(element,
                                    PsiClass.class);

                    if (referencingClass != null) {
                        allCalls.add(reference);
                        if (TestUtils.isTest(referencingClass)) {
                            testCalls.add(reference);
                        } else if (TestUtils.isProduction(referencingClass)) {
                            productCalls.add(reference);
                        }
                    }
                }
            }
        };
        final ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.runProcess(runnable, null);
        final PsiMethod[] ancestorMethods = method.findSuperMethods();
        for (PsiMethod ancestorMethod : ancestorMethods) {
            final Set<PsiReference> ancestorCalls = calculateMethodCallPoints(
                    ancestorMethod);
            allCalls.addAll(ancestorCalls);
            final Set<PsiReference> ancestorTestCalls =
                    calculateTestMethodCallPoints(ancestorMethod);
            testCalls.addAll(ancestorTestCalls);
            final Set<PsiReference> ancestorProductCalls =
                    calculateProductMethodCallPoints(ancestorMethod);
            productCalls.addAll(ancestorProductCalls);
        }
        final SmartPointerManager manager = SmartPointerManager.getInstance(method.getProject());
        final SmartPsiElementPointer<PsiMethod> pointer = manager.createSmartPsiElementPointer(method);
        methodToCallPointMap.put(pointer, allCalls);
        methodToProductCallPointMap.put(pointer, productCalls);
        methodToTestCallPointMap.put(pointer, testCalls);
    }
}
