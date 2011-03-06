/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.TodoUtil;

import java.util.Set;

public class TodoCommentCountPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numTodoCommentsPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numTodoCommentsPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numCommentLines = numTodoCommentsPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, numCommentLines);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            numTodoCommentsPerPackage.createBucket(aPackage);
        }

        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            final PsiClass aClass = PsiTreeUtil.getParentOfType(comment, PsiClass.class);
            final PsiPackage aPackage = ClassUtils.findPackage(aClass);
            if (aPackage == null) {
                return;
            }
            if (TodoUtil.isTodoComment(comment)) {
                numTodoCommentsPerPackage.incrementBucketValue(aPackage, 1);
            }
        }
    }
}
