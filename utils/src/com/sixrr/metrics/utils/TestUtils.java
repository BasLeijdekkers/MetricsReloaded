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

package com.sixrr.metrics.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

public final class TestUtils {

    private TestUtils() {}

    public static boolean isTest(PsiDirectory directory) {
        final VirtualFile virtualFile = directory.getVirtualFile();
        final Project project = directory.getProject();
        return isTest(virtualFile, project);
    }

    public static boolean isTest(VirtualFile virtualFile, Project project) {
        if (virtualFile == null) {
            return false;
        }
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.isInTestSourceContent(virtualFile);
    }

    public static boolean isProduction(VirtualFile virtualFile, Project project) {
        if (virtualFile == null) {
            return false;
        }
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.isInSourceContent(virtualFile) && !fileIndex.isInTestSourceContent(virtualFile);
    }

    public static boolean isTest(PsiFile file) {
        final VirtualFile virtualFile = file.getVirtualFile();
        final Project project = file.getProject();
        return isTest(virtualFile, project);
    }

    public static boolean isProduction(PsiElement element) {
        final PsiFile file = PsiTreeUtil.getParentOfType(element, PsiFile.class);
        if (file == null) {
            return false;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        return isProduction(virtualFile, element.getProject());
    }

    public static boolean isProduction(PsiFile file) {
        final VirtualFile virtualFile = file.getVirtualFile();
        return isProduction(virtualFile, file.getProject());
    }

    public static boolean isTest(PsiElement element) {
        final PsiFile file = PsiTreeUtil.getParentOfType(element, PsiFile.class);
        if (file == null) {
            return false;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        final Project project = element.getProject();
        return isTest(virtualFile, project);
    }
}
