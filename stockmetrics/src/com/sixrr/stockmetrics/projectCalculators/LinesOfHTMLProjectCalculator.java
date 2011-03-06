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

package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.xml.XmlFile;
import com.sixrr.stockmetrics.utils.LineUtil;

public class LinesOfHTMLProjectCalculator extends ElementCountProjectCalculator {

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {
        
        public void visitFile(PsiFile file) {
            if (!(file instanceof XmlFile)) {
                return;
            }
            final String fileName = file.getName();
            //noinspection HardCodedStringLiteral
            if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                numElements += LineUtil.countLines(file);
            }
        }
    }
}
