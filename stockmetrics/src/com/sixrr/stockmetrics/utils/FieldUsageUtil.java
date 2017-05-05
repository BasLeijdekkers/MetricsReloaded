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

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public final class FieldUsageUtil {
    private static final Key<FieldUsageMap> fieldUsageMapKey = new Key<FieldUsageMap>("FieldUsageMap");

    private FieldUsageUtil() {
    }

    public static FieldUsageMap getUsageMap(UserDataHolder dataHolder) {
        FieldUsageMap map = dataHolder.getUserData(fieldUsageMapKey);
        if (map == null) {
            map = new FieldUsageMapImpl();
            dataHolder.putUserData(fieldUsageMapKey, map);
        }
        return map;
    }

    /**
     * For each field it finds all usages in methods
     * @param dataHolder Data holder which will be used for get <code>{@link FieldUsageMap}</code> instance
     * @param aClass Class for fields of which result will be found
     * @return Map that contains set of methods for every field from class
     */
    public static Map<PsiField, Set<PsiMethod>> getFieldUsagesInMethods(UserDataHolder dataHolder, PsiClass aClass) {
        final FieldUsageMap map = getUsageMap(dataHolder);
        final PsiField[] fields = aClass.getFields();
        final Map<PsiField, Set<PsiMethod>> fieldsToMethods = new HashMap<PsiField, Set<PsiMethod>>();
        for (final PsiField field : fields) {
            final Set<PsiMethod> methods = new HashSet<PsiMethod>();
            final Set<PsiReference> references = map.calculateFieldUsagePoints(field);
            for (final PsiReference reference : references) {
                final PsiElement element = reference.getElement();
                final PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (method == null) {
                    continue;
                }
                methods.add(method);
            }
            fieldsToMethods.put(field, methods);
        }
        return fieldsToMethods;
    }
}
