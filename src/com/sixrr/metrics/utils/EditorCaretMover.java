/*
 * Copyright 2005, Sixth and Red River Software
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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class EditorCaretMover {
    private final Project project;
    private boolean shouldMoveCaret = true;

    public EditorCaretMover(Project project) {
        super();
        this.project = project;
    }

    private void disableMovementOneTime() {
        shouldMoveCaret = false;
    }

    public void moveEditorCaret(PsiElement element) {
        if (element == null) {
            return;
        }
        if (shouldMoveCaret(element)) {
            final Editor editor = getEditor(element);
            if (editor == null) {
                return;
            }

            final int textOffset = element.getTextOffset();
            if (textOffset < editor.getDocument().getTextLength()) {
                editor.getCaretModel().moveToOffset(textOffset);
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            }
        }
        shouldMoveCaret = true;
    }

    private boolean shouldMoveCaret(PsiElement element) {
        return shouldMoveCaret &&
                PluginPsiUtil.isElementInSelectedFile(project, element);
    }

    @Nullable
    private Editor getEditor(PsiElement element) {
        return PluginPsiUtil.getEditorIfSelected(project, element);
    }

    @Nullable
    public Editor openInEditor(PsiElement element) {

        final PsiFile psiFile;
        final int i;
        if (element instanceof PsiFile) {
            psiFile = (PsiFile) element;
            i = -1;
        } else {
            psiFile = element.getContainingFile();
            i = element.getTextOffset();
        }
        if (psiFile == null) {
            return null;
        }
        final OpenFileDescriptor fileDesc =
                new OpenFileDescriptor(project, psiFile.getVirtualFile(), i);
        disableMovementOneTime();
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        return fileEditorManager.openTextEditor(fileDesc, false);
    }
}
