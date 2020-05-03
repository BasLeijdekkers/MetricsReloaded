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

package com.sixrr.stockmetrics.interfaceCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.LineUtil;

public class SourceLinesOfCodeInterfaceCalculator extends InterfaceCalculator {

    private int elementCount = 0;

    public SourceLinesOfCodeInterfaceCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            int prevElementCount = 0;
            if (!ClassUtils.isAnonymous(aClass)) {
                prevElementCount = elementCount;
                elementCount = 0;
            }
            elementCount += LineUtil.countLines(aClass);
            final PsiClass[] innerClasses = aClass.getInnerClasses();
            for (PsiClass innerClass : innerClasses) {
                elementCount -= LineUtil.countLines(innerClass);
            }
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass)) {
                if (isInterface(aClass)) {
                    postMetric(aClass, (double) elementCount);
                }
                elementCount = prevElementCount;
            }
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            elementCount -= LineUtil.countCommentOnlyLines(comment);
        }
    }
}
