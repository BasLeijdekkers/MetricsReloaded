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

package com.sixrr.stockmetrics.utils;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;

import java.util.Set;

public interface MethodCallMap {
    Set<PsiReference> calculateMethodCallPoints(PsiMethod method);

    Set<PsiReference> calculateTestMethodCallPoints(PsiMethod method);

    Set<PsiReference> calculateProductMethodCallPoints(PsiMethod method);
}
