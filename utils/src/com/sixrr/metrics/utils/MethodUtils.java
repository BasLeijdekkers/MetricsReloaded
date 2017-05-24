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

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFieldImpl;
import com.intellij.psi.search.searches.SuperMethodsSearch;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.codehaus.groovy.transform.FieldASTTransformation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MethodUtils {

    private MethodUtils() {}

    public static boolean isConcreteMethod(PsiMethod method) {
        return method != null && !method.isConstructor() && !method.hasModifierProperty(PsiModifier.ABSTRACT) &&
                !method.hasModifierProperty(PsiModifier.STATIC) && !method.hasModifierProperty(PsiModifier.PRIVATE);
    }

    public static boolean hasConcreteSuperMethod(PsiMethod method) {
        final Query<MethodSignatureBackedByPsiMethod> search = SuperMethodsSearch.search(method, null, true, false);
        return !search.forEach(new Processor<MethodSignatureBackedByPsiMethod>() {

            @Override
            public boolean process(MethodSignatureBackedByPsiMethod superMethod) {
                return isAbstract(superMethod.getMethod());
            }
        });
    }

    public static boolean isAbstract(PsiMethod method) {
        if (method.hasModifierProperty(PsiModifier.STATIC) || method.hasModifierProperty(PsiModifier.DEFAULT)) {
            return false;
        }
        if (method.hasModifierProperty(PsiModifier.ABSTRACT)) {
            return true;
        }
        final PsiClass containingClass = method.getContainingClass();
        return containingClass != null && containingClass.isInterface();
    }

    public static String calculateSignature(PsiMethod method) {
        final PsiClass containingClass = method.getContainingClass();
        final String className;
        if (containingClass != null) {
            className = containingClass.getQualifiedName();
        } else {
            className = "";
        }
        final String methodName = method.getName();
        final StringBuilder out = new StringBuilder(50);
        out.append(className);
        out.append('.');
        out.append(methodName);
        out.append('(');
        final PsiParameterList parameterList = method.getParameterList();
        final PsiParameter[] parameters = parameterList.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (i != 0) {
                out.append(',');
            }
            final PsiType parameterType = parameters[i].getType();
            final String parameterTypeText = parameterType.getPresentableText();
            out.append(parameterTypeText);
        }
        out.append(')');
        return out.toString();
    }

    public static boolean isGetter(final PsiMethod method) {
        final String methodName = method.getName();
        final PsiType returnType = method.getReturnType();
        if (returnType == null || PsiType.VOID.equals(returnType) || method.getParameterList().getParametersCount() > 0 || !methodName.toLowerCase().startsWith("get")) {
            return false;
        }
        final PsiClass aClass = method.getContainingClass();
        return aClass != null;
    }

    public static boolean isTrivialGetter(final PsiMethod method) {
        if (!isGetter(method)) {
            return false;
        }
        final PsiCodeBlock body = method.getBody();
        if (body == null) {
            return false;
        }
        final PsiStatement[] statements = body.getStatements();
        return statements.length == 1 && statements[0] instanceof PsiReturnStatement && getUsedFields(statements[0]).size() == 1;
    }

    public static boolean isSetter(final PsiMethod method) {
        final String methodName = method.getName();
        final PsiType returnType = method.getReturnType();
        final PsiParameterList params = method.getParameterList();
        if (!PsiType.VOID.equals(returnType) || !methodName.toLowerCase().startsWith("set") || params.getParametersCount() != 1) {
            return false;
        }
        final PsiClass aClass = method.getContainingClass();
        return aClass != null;
    }

    public static boolean isTrivialSetter(final PsiMethod method) {
        if (!isSetter(method)) {
            return false;
        }
        final PsiCodeBlock body = method.getBody();
        if (body == null) {
            return false;
        }
        final PsiStatement[] statements = body.getStatements();
        return statements.length == 1 && statements[0] instanceof PsiExpressionStatement && getUsedFields(method).size() == 1;
    }

    @NotNull
    public static Set<PsiField> getUsedFields(@NotNull final PsiElement element) {
        final Set<PsiField> fields = new HashSet<PsiField>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                final PsiElement element = expression.resolve();
                if (element instanceof PsiField) {
                    fields.add((PsiField) element);
                }
            }
        });
        return fields;
    }

    public static boolean isGetterOrSetter(final PsiMethod method) {
        return isGetter(method) || isSetter(method);
    }

    public static boolean isTrivialGetterOrSetter(final PsiMethod method) {
        return isTrivialGetter(method) || isTrivialSetter(method);
    }

    private static boolean hasField(@NotNull final PsiClass aClass, @NotNull final String name, @NotNull final PsiType type) {
        final PsiField[] fields = aClass.getAllFields();
        for (final PsiField field : fields) {
            final String fieldName = field.getName();
            if (fieldName != null && fieldName.toLowerCase().equals(name.toLowerCase()) && type.isAssignableFrom(field.getType())) {
                return true;
            }
        }
        return false;
    }
}
