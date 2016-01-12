/*
 * Copyright 2016 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssFile;
import com.sixrr.stockmetrics.utils.LineUtil;

public class LinesOfCSSModuleCalculator extends ElementCountModuleCalculator {

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiElementVisitor {
        public void visitFile(PsiFile file) {
            super.visitFile(file);
            if (file instanceof CssFile) {
                final int lineCount = LineUtil.countLines(file);
                incrementElementCount(file, lineCount);
            }
        }
    }
}
