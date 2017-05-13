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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;
import com.sixrr.stockmetrics.utils.LineUtil;

public class BlankLinesCountCalculator extends ClassCalculator {

    private int commentLineCount = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitComment(PsiComment comment) {
            commentLineCount += LineUtil.countLines(comment);
            super.visitComment(comment);
        }

        @Override
        public void visitClass(PsiClass aClass) {
            int previousValue = commentLineCount;
            super.visitClass(aClass);
            if (isConcreteClass(aClass)) {
                int linesOfCode = LineUtil.countLines(aClass);
                int blankLines = aClass.getTextLength();
                final PsiClass[] innerClasses = aClass.getInnerClasses();
                for (PsiClass innerClass : innerClasses) {
                    linesOfCode -= LineUtil.countLines(innerClass);
                    blankLines -= innerClass.getTextLength();
                }
                blankLines -= linesOfCode + commentLineCount;
                postMetric(aClass, (double) blankLines);
            }
            commentLineCount = previousValue;
        }
    }
}
