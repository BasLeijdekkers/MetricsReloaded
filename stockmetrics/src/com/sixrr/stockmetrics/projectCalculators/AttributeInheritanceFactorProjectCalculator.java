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

package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.psi.*;

public class AttributeInheritanceFactorProjectCalculator extends ProjectCalculator {
    private int availableFields = 0;
    private int inheritedFields = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiField[] allFields = aClass.getAllFields();
            for (PsiField field : allFields) {
                final PsiClass containingClass = field.getContainingClass();
                if (containingClass == null) {
                    continue;
                }
                final String className = containingClass.getName();
                if (containingClass.equals(aClass)) {
                    availableFields++;
                } else if ("java.lang.Object".equals(className)) {

                } else if (!field.hasModifierProperty(PsiModifier.PRIVATE)) {
                    availableFields++;
                    inheritedFields++;
                }
            }
        }
    }

    public void endMetricsRun() {
        postMetric(inheritedFields, availableFields);
    }
}
