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
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.sixrr.metrics.Metric;
import com.sixrr.stockmetrics.utils.LineUtil;
import com.sixrr.metrics.utils.MethodUtils;

public class JavadocLinesOfCodeMethodCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    protected int elementCount = 0;

    public JavadocLinesOfCodeMethodCalculator(Metric metric) {
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
                elementCount = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                postMetric(method, elementCount);
            }
        }

        @Override
        public void visitDocComment(PsiDocComment comment) {
            super.visitDocComment(comment);
            elementCount += LineUtil.countLines(comment);
        }
    }
}
