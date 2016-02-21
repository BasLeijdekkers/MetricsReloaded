/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sixrr.metrics.utils;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;

/**
 * @author Bas Leijdekkers
 */
public final class JavaTestUtils {

    private JavaTestUtils() {}

    public static boolean isJUnitAssertCall(PsiMethodCallExpression call) {
        final PsiMethod containingMethod = PsiTreeUtil.getParentOfType(call, PsiMethod.class);
        if (containingMethod == null) {
            return false;
        }
        final PsiReferenceExpression methodExpression = call.getMethodExpression();
        @NonNls final String methodName = methodExpression.getReferenceName();
        return methodName != null && (methodName.startsWith("assert") ||
                "fail".equals(methodName)) && isJUnitTestMethod(containingMethod);
    }

    public static boolean isJUnitTestCase(PsiClass aClass) {
        final Project project = aClass.getProject();
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass testCase =
                psiFacade.findClass("junit.framework.TestCase", scope);
        if (testCase == null) {
            return false;
        }
        if (aClass.isInheritor(testCase, true)) {
            return true;
        }
        final PsiMethod[] methods = aClass.getMethods();
        for (PsiMethod method : methods) {
            if (AnnotationUtil.isAnnotated(method, "org.junit.Test", true)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJUnitTestMethod(PsiMethod method) {
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
        return containingClass != null && isJUnitTestCase(containingClass);
    }

}
