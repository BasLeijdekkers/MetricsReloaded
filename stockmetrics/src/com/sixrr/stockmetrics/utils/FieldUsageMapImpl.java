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

package com.sixrr.stockmetrics.utils;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.TestUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public class FieldUsageMapImpl implements FieldUsageMap {
    private final Map<SmartPsiElementPointer<PsiField>, Set<PsiReference>> fieldUsagePointMap =
            new HashMap<SmartPsiElementPointer<PsiField>, Set<PsiReference>>(1024);
    private final Map<SmartPsiElementPointer<PsiField>, Set<PsiReference>> fieldTestUsagePointMap =
            new HashMap<SmartPsiElementPointer<PsiField>, Set<PsiReference>>(1024);
    private final Map<SmartPsiElementPointer<PsiField>, Set<PsiReference>> fieldProductUsagePointMap =
            new HashMap<SmartPsiElementPointer<PsiField>, Set<PsiReference>>(1024);

    @NotNull
    @Override
    public Set<PsiReference> calculateFieldUsagePoints(PsiField field) {
        return getUsages(fieldUsagePointMap, field);
    }

    @NotNull
    @Override
    public Set<PsiReference> calculateTestFieldUsagePoints(PsiField field) {
        return getUsages(fieldTestUsagePointMap, field);
    }

    @NotNull
    @Override
    public Set<PsiReference> calculateProductFieldUsagePoints(PsiField field) {
        return getUsages(fieldProductUsagePointMap, field);
    }

    private Set<PsiReference> getUsages(Map<SmartPsiElementPointer<PsiField>, Set<PsiReference>> map, PsiField field) {
        final SmartPsiElementPointer<PsiField> pointer = getPointer(field);
        if (!map.containsKey(pointer)) {
            calculateUsages(field);
        }
        return map.get(pointer);
    }

    @NotNull
    private SmartPsiElementPointer<PsiField> getPointer(PsiField field) {
        final SmartPointerManager manager = SmartPointerManager.getInstance(field.getProject());
        return manager.createSmartPsiElementPointer(field);
    }

    private void calculateUsages(PsiField field) {
        final Set<PsiReference> allUsages = new HashSet<PsiReference>();
        final Set<PsiReference> testUsages = new HashSet<PsiReference>();
        final Set<PsiReference> productUsages = new HashSet<PsiReference>();

        final Query<PsiReference> query = ReferencesSearch.search(field);
        for (final PsiReference reference : query) {
            final PsiElement element = reference.getElement();

            final PsiClass referenceClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (referenceClass == null) {
                continue;
            }
            allUsages.add(reference);
            if (TestUtils.isTest(referenceClass)) {
                testUsages.add(reference);
            }
            if (TestUtils.isProduction(referenceClass)) {
                productUsages.add(reference);
            }
        }
        final SmartPsiElementPointer<PsiField> pointer = getPointer(field);
        fieldUsagePointMap.put(pointer, allUsages);
        fieldTestUsagePointMap.put(pointer, testUsages);
        fieldProductUsagePointMap.put(pointer, productUsages);
    }
}
