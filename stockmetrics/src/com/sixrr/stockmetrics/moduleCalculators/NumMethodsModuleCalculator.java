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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.sixrr.metrics.Metric;

public class NumMethodsModuleCalculator extends ElementCountModuleCalculator {

    public NumMethodsModuleCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            incrementCount(method, 1);
        }

        @Override
        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            createCount(file);
        }
    }
}
