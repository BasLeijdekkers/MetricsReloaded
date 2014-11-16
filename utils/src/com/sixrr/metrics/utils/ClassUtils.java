/*
 * Copyright 2005-2014 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassUtils {

    private ClassUtils() {}

    @NotNull
    public static String calculatePackageName(PsiClass aClass) {
        final PsiJavaFile file = PsiTreeUtil.getParentOfType(aClass, PsiJavaFile.class);
        if (file == null) {
            return "";
        }
        return file.getPackageName();
    }

    public static PsiPackage[] calculatePackagesRecursive(PsiClass aClass) {
        PsiPackage aPackage = findPackage(aClass);
        final List<PsiPackage> out = new ArrayList<PsiPackage>();
        while (aPackage != null) {
            out.add(aPackage);
            aPackage = aPackage.getParentPackage();
        }
        return out.toArray(new PsiPackage[out.size()]);
    }

    public static String calculatePackageName(PsiJavaFile file) {
        return file.getPackageName();
    }

    public static PsiPackage[] calculatePackagesRecursive(PsiJavaFile file) {
        PsiPackage aPackage = findPackage(file);
        final List<PsiPackage> out = new ArrayList<PsiPackage>();
        while (aPackage != null) {
            out.add(aPackage);
            aPackage = aPackage.getParentPackage();
        }
        return out.toArray(new PsiPackage[out.size()]);
    }

    public static boolean isAnonymous(PsiClass aClass) {
        return aClass instanceof PsiAnonymousClass ||
                aClass instanceof PsiTypeParameter;
    }

    @Nullable
    public static Module calculateModule(PsiClass aClass) {
        final PsiJavaFile file = PsiTreeUtil.getParentOfType(aClass, PsiJavaFile.class);
        if (file == null) {
            return null;
        }
        return calculateModule(file);
    }

    @Nullable
    public static Module calculateModule(@Nullable PsiFile file) {
        if (file == null) {
            return null;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }
        final PsiManager manager = file.getManager();
        final Project project = manager.getProject();
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.getModuleForFile(virtualFile);
    }

    public static String calculateModuleName(PsiClass aClass) {
        final PsiJavaFile file = PsiTreeUtil.getParentOfType(aClass, PsiJavaFile.class);
        return calculateModuleName(file);
    }

    public static String calculateModuleName(PsiFile file) {
        if (file == null) {
            return "";
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return "";
        }
        final PsiManager manager = file.getManager();
        final Project project = manager.getProject();
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        final Module module = fileIndex.getModuleForFile(virtualFile);
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
        final Query<PsiClass> query = DirectClassInheritorsSearch.search(aClass);
        final Collection<PsiClass> subclasses = query.findAll();
        return subclasses.isEmpty();
    }

    @Nullable
    public static PsiPackage findPackage(PsiJavaFile file) {
        if (file == null) {
            return null;
        }
        final String referencedPackageName = calculatePackageName(file);
        final Project project = file.getProject();
        final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        return psiFacade.findPackage(referencedPackageName);
    }

    @Nullable
    public static PsiPackage findPackage(PsiClass referencedClass) {
        if (referencedClass == null) {
            return null;
        }
        final String referencedPackageName = calculatePackageName(referencedClass);
        final Project project = referencedClass.getProject();
        final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        return psiFacade.findPackage(referencedPackageName);
    }
}
