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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.sixrr.metrics.utils.ClassUtils;

public class PercentFieldsJavadocedClassCalculator extends ClassCalculator {

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass) && !aClass.isInterface()) {
                int numFields = 0;
                int numJavadocedFields = 0;
                final PsiField[] fields = aClass.getFields();
                for (final PsiField field : fields) {
                    numFields++;
                    if (field.getFirstChild()instanceof PsiDocComment) {
                        numJavadocedFields++;
                    }
                }
                postMetric(aClass, numJavadocedFields, numFields);
            }
        }
    }
}
