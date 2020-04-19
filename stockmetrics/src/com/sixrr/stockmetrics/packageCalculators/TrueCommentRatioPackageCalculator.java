/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.LineUtil;

import java.util.Set;

public class TrueCommentRatioPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numLinesPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numCommentLinesPerPackage = new BucketedCount<>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numLinesPerPackage.getBuckets();
        for (PsiPackage aPackage : packages) {
            final int numLines = numLinesPerPackage.getBucketValue(aPackage);
            final int numCommentLines = numCommentLinesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numCommentLines, numLines - numCommentLines);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {

        @Override
        public void visitFile(PsiFile file) {
            super.visitFile(file);
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            if (aPackage == null) {
                return;
            }
            final int lineCount = LineUtil.countLines(file);
            numLinesPerPackage.incrementBucketValue(aPackage, lineCount);
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            final PsiPackage aPackage = ClassUtils.findPackage(comment);
            if (aPackage == null) {
                return;
            }
            final int lineCount = LineUtil.countCommentOnlyLines(comment);
            numCommentLinesPerPackage.incrementBucketValue(aPackage, lineCount);
        }
    }
}
