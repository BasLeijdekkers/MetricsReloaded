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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ClassUtils {

    private ClassUtils() {}

    @NotNull
    public static String calculatePackageName(PsiElement element) {
        final PsiFile file = element.getContainingFile();
        if (!(file instanceof PsiJavaFile)) {
            return "";
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        return javaFile.getPackageName();
    }

    public static PsiPackage[] calculatePackagesRecursive(PsiElement element) {
        PsiPackage aPackage = findPackage(element);
        final List<PsiPackage> out = new ArrayList<PsiPackage>();
        while (aPackage != null) {
            out.add(aPackage);
            aPackage = aPackage.getParentPackage();
        }
        return out.toArray(new PsiPackage[out.size()]);
    }

    public static boolean isAnonymous(PsiClass aClass) {
        return aClass instanceof PsiAnonymousClass || aClass instanceof PsiTypeParameter;
    }

    @Nullable
    public static Module calculateModule(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }
        final PsiFile file = element.getContainingFile();
        if (file == null) {
            return null;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(file.getProject());
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.getModuleForFile(virtualFile);
    }

    public static String calculateModuleName(PsiElement element) {
        final Module module = calculateModule(element);
        if (module == null) {
            return "";
        }
        return module.getName();
    }

    public static boolean isTopLevel(PsiClass aClass) {
        return PsiTreeUtil.getParentOfType(aClass, PsiClass.class) == null;
    }

    public static boolean isAbstract(PsiClass aClass) {
        return !aClass.isInterface() && aClass.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    public static boolean isConcrete(PsiClass aClass) {
        return !aClass.isInterface() && !aClass.isEnum() && !aClass.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    public static boolean isRoot(PsiClass aClass) {
        if (aClass.isInterface()) {
            return false;
        }
        final PsiClass superClass = aClass.getSuperClass();
        return superClass == null || "java.lang.Object".equals(superClass.getQualifiedName());
    }

    public static boolean isLeaf(PsiClass aClass) {
        if (aClass.isInterface()) {
            return false;
        }
        return DirectClassInheritorsSearch.search(aClass).findFirst() == null;
    }

    @Nullable
    public static PsiPackage findPackage(PsiElement element) {
        if (element == null) {
            return null;
        }
        final PsiFile file = element.getContainingFile();
        final PsiDirectory directory = file.getContainingDirectory();
        return JavaDirectoryService.getInstance().getPackage(directory);
    }
}
