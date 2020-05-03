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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.utils.LineUtil;

public class TrueCommentRatioModuleCalculator extends ElementRatioModuleCalculator {

    public TrueCommentRatioModuleCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {

        @Override
        public void visitFile(PsiFile file) {
            super.visitFile(file);
            createRatio(file);
            final int lineCount = LineUtil.countLines(file);
            incrementDenominator(file, lineCount);
        }

        @Override
        public void visitElement(PsiElement element) {
            super.visitElement(element);
            if (element instanceof PsiComment) {
                final PsiComment comment = (PsiComment) element;
                final int commentOnlyLineCount = LineUtil.countCommentOnlyLines(comment);
                incrementDenominator(comment, -commentOnlyLineCount);
                final int lineCount = LineUtil.countLines(comment);
                incrementNumerator(comment, lineCount);
            }
        }
    }
}
