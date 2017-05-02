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

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Aleksandr Chudov.
 */
public interface FieldUsageMap {
    @NotNull
    Set<PsiReference> calculateFieldUsagePoints(PsiField field);

    @NotNull
    Set<PsiReference> calculateTestFieldUsagePoints(PsiField field);

    @NotNull
    Set<PsiReference> calculateProductFieldUsagePoints(PsiField field);
}
