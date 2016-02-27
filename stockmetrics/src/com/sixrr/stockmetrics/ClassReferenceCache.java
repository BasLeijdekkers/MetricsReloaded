/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiReference;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public class ClassReferenceCache {

    private final Map<SmartPsiElementPointer<PsiClass>, Collection<PsiReference>> cachedReferences =
            new WeakHashMap<SmartPsiElementPointer<PsiClass>, Collection<PsiReference>>(256);

    public Collection<PsiReference> findClassReferences(final PsiClass aClass) {
        final SmartPointerManager manager = SmartPointerManager.getInstance(aClass.getProject());
        final SmartPsiElementPointer<PsiClass> pointer = manager.createSmartPsiElementPointer(aClass);

        final Collection<PsiReference> references = cachedReferences.get(pointer);
        if (references == null) {
            final SearchScope scope = aClass.getUseScope();
            final Collection<PsiReference> newReferences = ReferencesSearch.search(aClass, scope, false).findAll();
            cachedReferences.put(pointer, newReferences);
        }
        return cachedReferences.get(pointer);
    }
}
