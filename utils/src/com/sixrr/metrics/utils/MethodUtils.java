/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.search.searches.SuperMethodsSearch;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.util.PsiFormatUtilBase.*;

public final class MethodUtils {

    private MethodUtils() {}

    public static boolean isConcreteMethod(PsiMethod method) {
        return method != null && !method.isConstructor() && !method.hasModifierProperty(PsiModifier.ABSTRACT) &&
                !method.hasModifierProperty(PsiModifier.STATIC) && !method.hasModifierProperty(PsiModifier.PRIVATE);
    }

    public static boolean hasConcreteSuperMethod(PsiMethod method) {
        final Query<MethodSignatureBackedByPsiMethod> search = SuperMethodsSearch.search(method, null, true, false);
        return !search.forEach(superMethod -> {
            return isAbstract(superMethod.getMethod());
        });
    }

    public static boolean isAbstract(@NotNull PsiMethod method) {
        if (method.hasModifierProperty(PsiModifier.STATIC) || method.hasModifierProperty(PsiModifier.DEFAULT)) {
            return false;
        }
        if (method.hasModifierProperty(PsiModifier.ABSTRACT)) {
            return true;
        }
        final PsiClass containingClass = method.getContainingClass();
        return containingClass != null && containingClass.isInterface();
    }

    public static String calculateSignature(PsiMethod method) {
        return PsiFormatUtil.formatMethod(method, PsiSubstitutor.EMPTY,
                                          SHOW_NAME | SHOW_FQ_NAME | SHOW_CONTAINING_CLASS | SHOW_FQ_CLASS_NAMES | SHOW_PARAMETERS,
                                          SHOW_TYPE);
    }
}
