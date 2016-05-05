/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.psi.*;

public class PercentFieldsJavadocedInterfaceCalculator extends InterfaceCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (!isInterface(aClass)) {
                return;
            }
            int numFields = 0;
            int numJavadocedFields = 0;
            for (final PsiField field : aClass.getFields()) {
                if (field.hasModifierProperty(PsiModifier.PRIVATE)) {
                    continue;
                }
                numFields++;
                if (field.getDocComment() != null) {
                    numJavadocedFields++;
                }
            }
            postMetric(aClass, numJavadocedFields, numFields);
        }
    }
}
