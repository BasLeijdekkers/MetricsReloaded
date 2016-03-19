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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiTodoSearchHelper;

public final class TodoUtil {

    private TodoUtil() {}

    public static int getTodoItemsCount(PsiElement element) {
        final PsiFile file = element.getContainingFile();
        final PsiTodoSearchHelper todoSearchHelper = PsiTodoSearchHelper.SERVICE.getInstance(file.getProject());
        final int offset = element.getTextOffset();
        return todoSearchHelper.findTodoItems(file, offset, offset + element.getTextLength()).length;
    }
}
