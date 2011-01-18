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
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.LineUtil;

import java.util.Set;

public class SourceLinesOfCodeRecursivePackageCalculator extends PackageCalculator {
    private final BuckettedCount<PsiPackage> numLinesPerPackage = new BuckettedCount<PsiPackage>();
    private final BuckettedCount<PsiPackage> numCommentLinesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numLinesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numLines = numLinesPerPackage.getBucketValue(aPackage);
            final int numCommentLines = numCommentLinesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, (double) (numLines - numCommentLines));
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final int lineCount = LineUtil.countLines(file);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(file);
            for (final PsiPackage aPackage : packages) {
                numLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            }
        }

        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            final PsiClass aClass = PsiTreeUtil.getParentOfType(comment, PsiClass.class);
            final int lineCount = LineUtil.countCommentOnlyLines(comment);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(aClass);
            for (final PsiPackage aPackage : packages) {
                numCommentLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            }
        }
    }
}
