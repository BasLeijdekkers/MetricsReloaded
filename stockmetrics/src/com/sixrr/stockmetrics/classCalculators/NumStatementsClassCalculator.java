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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.ClassUtils;

public class NumStatementsClassCalculator extends ClassCalculator {
    private int elementCount = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            final int prevElementCount = elementCount;
            if (!ClassUtils.isAnonymous(aClass)) {
                elementCount = 0;
            }
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass)) {
                if (!aClass.isInterface()) {
                    postMetric(aClass, elementCount);
                }
                elementCount = prevElementCount;
            }
        }

        public void visitStatement(PsiStatement statement) {
            super.visitStatement(statement);
            if (!(statement instanceof PsiEmptyStatement) &&
                    !(statement instanceof PsiBlockStatement)) {
                elementCount++;
            }
        }
    }
}
