/*
 * Copyright 2005-2015 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class AverageCyclomaticComplexityModuleCalculator extends ModuleCalculator {

    private int methodNestingDepth = 0;
    private int complexity = 0;

    private final BucketedCount<Module> totalComplexityPerModule = new BucketedCount<Module>();
    private final BucketedCount<Module> numMethodsPerModule = new BucketedCount<Module>();

    @Override
    public void endMetricsRun() {
        final Set<Module> modules = numMethodsPerModule.getBuckets();
        for (final Module module : modules) {
            final int numMethods = numMethodsPerModule.getBucketValue(module);
            final int totalComplexity = totalComplexityPerModule.getBucketValue(module);

            postMetric(module, totalComplexity, numMethods);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (method.getBody() == null) {
                return;
            }
            if (methodNestingDepth == 0) {
                if (method.getBody() != null) {
                    complexity = 1;
                }
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0) {
                final PsiClass containingClass = method.getContainingClass();
                if (containingClass != null) {
                    final Module module = ClassUtils.calculateModule(containingClass);
                    if (module == null) {
                        return;
                    }
                    totalComplexityPerModule.incrementBucketValue(module, complexity);
                    numMethodsPerModule.incrementBucketValue(module);
                }
            }
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            complexity++;
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            complexity++;
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            complexity++;
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            complexity++;
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            complexity++;
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            final PsiCodeBlock body = statement.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            boolean pendingLabel = false;
            for (final PsiStatement child : statements) {
                if (child instanceof PsiSwitchLabelStatement) {
                    if (!pendingLabel) {
                        complexity++;
                    }
                    pendingLabel = true;
                } else {
                    pendingLabel = false;
                }
            }
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            complexity++;
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            complexity++;
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            final IElementType token = expression.getOperationTokenType();
            if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                complexity += expression.getOperands().length - 1;
            }
        }
    }
}
