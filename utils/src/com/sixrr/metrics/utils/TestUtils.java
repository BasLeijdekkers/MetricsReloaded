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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.codeInsight.AnnotationUtil;
import org.jetbrains.annotations.NonNls;

public class TestUtils {
    private TestUtils() {
        super();
    }

    public static boolean isTest(PsiClass aClass) {
        final PsiManager manager = aClass.getManager();
        final PsiFile file = PsiTreeUtil.getParentOfType(aClass, PsiFile.class);
        if (file == null) {
            return false;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        final Project project = manager.getProject();
        return TestUtils.isTest(project, virtualFile);
    }

    public static boolean isTest(PsiDirectory directory) {
        final PsiManager manager = directory.getManager();
        final VirtualFile virtualFile = directory.getVirtualFile();
        final Project project = manager.getProject();
        return TestUtils.isTest(project, virtualFile);
    }

    public static boolean isTest(Project project, VirtualFile virtualFile) {
        if (virtualFile == null) {
            return false;
        }
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(
                project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.isInTestSourceContent(virtualFile);
    }

    public static boolean isTest(PsiFile file) {
        final PsiManager manager = file.getManager();
        final VirtualFile virtualFile = file.getVirtualFile();
        final Project project = manager.getProject();
        return TestUtils.isTest(project, virtualFile);
    }

    public static boolean isJUnitTestCase(PsiClass aClass) {
        final Project project = aClass.getProject();
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass testCase =
                psiFacade.findClass("junit.framework.TestCase", scope);
        if (aClass.isInheritor(testCase, true)) {
            return true;
        }
        final PsiMethod[] methods = aClass.getMethods();
        for (PsiMethod method : methods) {
            //noinspection HardCodedStringLiteral
            if (AnnotationUtil.isAnnotated(method, "org.junit.Test", true)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJUnitTestMethod(PsiMethod method) {
        //noinspection HardCodedStringLiteral
        if (AnnotationUtil.isAnnotated(method, "org.junit.Test", true)) {
            return true;
        }
        final PsiType returnType = method.getReturnType();
        if (!PsiType.VOID.equals(returnType)) {
            return false;
        }
        if (!method.hasModifierProperty(PsiModifier.PUBLIC)) {
            return false;
        }
        @NonNls final String methodName = method.getName();
        if (!methodName.startsWith("test")) {
            return false;
        }
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return false;
        }
        return isJUnitTestCase(containingClass);
    }

    public static boolean isJUnitAssertCall(PsiMethodCallExpression call) {
        final PsiMethod containingMethod = PsiTreeUtil.getParentOfType(call,
                                                                     PsiMethod.class);
        if (containingMethod == null) {
            return false;
        }
        final PsiReferenceExpression methodExpression =
                call.getMethodExpression();
        @NonNls final String methodName = methodExpression.getReferenceName();
        if (methodName != null &&
                (methodName.startsWith("assert") || "fail".equals(methodName))) {
            return isJUnitTestMethod(containingMethod);
        }
        return false;
    }
}
