/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class FormatSpanUtils {
    private int startLine;
    private int endLine;
    private int startCol;
    private int endCol;

    public int getStartLine() {
        return startLine;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndCol() {
        return endCol;
    }

    public void calculateSpanValues(PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return;
        }
        PsiFile file = element.getContainingFile();
        Project project = file.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(file);
        int startOffset = element.getTextRange().getStartOffset();
        int endOffset = element.getTextRange().getEndOffset();
        this.startLine = document.getLineNumber(startOffset) + 1;
        this.endLine = document.getLineNumber(endOffset) + 1;
        this.startCol = startOffset - document.getLineStartOffset(this.startLine - 1) + 1;
        this.endCol = endOffset - document.getLineStartOffset(this.endLine - 1) + 1;
    }
}
