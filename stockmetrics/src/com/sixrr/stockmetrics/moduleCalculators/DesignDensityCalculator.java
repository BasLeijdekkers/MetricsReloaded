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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiElementFilter;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;
import com.sixrr.stockmetrics.methodCalculators.ComplexityCalculator;
import com.sixrr.stockmetrics.methodCalculators.DesignComplexityCalculator;
import com.sixrr.stockmetrics.utils.CyclomaticComplexityUtil;

public class DesignDensityCalculator extends ElementRatioModuleCalculator {
    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (MethodUtils.isAbstract(method)) {
                return;
            }
            createRatio(method);
            final int design = CyclomaticComplexityUtil.calculateComplexity(method,  new PsiElementFilter() {
                @Override
                public boolean isAccepted(PsiElement element) {
                    return !notCallAnotherModule(element);
                }
            });
            final int complexity = CyclomaticComplexityUtil.calculateComplexity(method,  new PsiElementFilter() {
                @Override
                public boolean isAccepted(PsiElement element) {
                    return true;
                }
            });
            System.err.println(method.getName());
            System.err.println(design);
            System.err.println(complexity);
            incrementNumerator(method, design);
            incrementDenominator(method, complexity);
        }
    }

    private boolean notCallAnotherModule(PsiElement element) {
        if (element == null) {
            return true;
        }
        if (element instanceof PsiIfStatement) {
            final PsiIfStatement ifStatement = (PsiIfStatement) element;
            return !containsMethodCall(ifStatement.getThenBranch()) && !containsMethodCall(ifStatement.getElseBranch());
        } else if (element instanceof PsiLoopStatement) {
            final PsiLoopStatement loopStatement = (PsiLoopStatement) element;
            return !containsMethodCall(loopStatement.getBody());
        } else if (element instanceof PsiCatchSection) {
            final PsiCatchSection catchSection = (PsiCatchSection) element;
            return !containsMethodCall(catchSection.getCatchBlock());
        } else if (element instanceof PsiBlockStatement) {
            return blockStatementIsReducible((PsiBlockStatement) element);
        } else if (element instanceof PsiConditionalExpression) {
            final PsiConditionalExpression conditionalExpression = (PsiConditionalExpression) element;
            return !containsMethodCall(conditionalExpression.getThenExpression()) &&
                    !containsMethodCall(conditionalExpression.getElseExpression());
        } else {
            return !containsMethodCall(element);
        }
    }

    private static boolean blockStatementIsReducible(PsiBlockStatement statement) {
        return !containsMethodCall(statement.getCodeBlock());
    }

    private static boolean containsMethodCall(PsiElement element) {
        if (element == null) {
            return false;
        }
        final MethodCallVisitor visitor = new MethodCallVisitor();
        element.accept(visitor);
        return visitor.isMethodCalled();
    }

    private static class MethodCallVisitor extends JavaRecursiveElementVisitor {
        private boolean methodCalled = false;

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            methodCalled = true;
        }

        private boolean isMethodCalled() {
            return methodCalled;
        }
    }
}
