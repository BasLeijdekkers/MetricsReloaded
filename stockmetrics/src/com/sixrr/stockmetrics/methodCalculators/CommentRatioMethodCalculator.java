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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.utils.LineUtil;

public class CommentRatioMethodCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    private int numerator = 0;
    private int denominator = 0;

    public CommentRatioMethodCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                numerator = 0;
                denominator = 0;
            }
            denominator = LineUtil.countLines(method);

            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0) {
                if (denominator == 0) {
                    postMetric(method, 0);
                } else {
                    postMetric(method, numerator, denominator);
                }
            }
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            numerator += LineUtil.countLines(comment);
        }
    }
}
