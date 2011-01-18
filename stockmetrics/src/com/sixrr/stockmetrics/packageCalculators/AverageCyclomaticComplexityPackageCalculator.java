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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class AverageCyclomaticComplexityPackageCalculator extends PackageCalculator {
    private int methodNestingDepth = 0;
    private int complexity = 0;
    private final BuckettedCount<PsiPackage> totalComplexityPerPackage = new BuckettedCount<PsiPackage>();
    private final BuckettedCount<PsiPackage> numMethodsPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numMethodsPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numClasses = numMethodsPerPackage.getBucketValue(aPackage);
            final int numAbstractClasses = totalComplexityPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, numAbstractClasses, numClasses);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
            }
            if (method.getBody() != null) {
                complexity = 1;
            }

            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0) {
                final PsiClass containingClass = method.getContainingClass();
                if (containingClass != null) {
                    final PsiPackage aPackage = ClassUtils.findPackage(containingClass);
                    totalComplexityPerPackage.incrementBucketValue(aPackage, complexity);
                    numMethodsPerPackage.incrementBucketValue(aPackage);
                }
            }
        }

        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            complexity++;
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            complexity++;
        }

        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            complexity++;
        }

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            complexity++;
        }

        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            complexity++;
        }

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

        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            complexity++;
        }
    }
}
