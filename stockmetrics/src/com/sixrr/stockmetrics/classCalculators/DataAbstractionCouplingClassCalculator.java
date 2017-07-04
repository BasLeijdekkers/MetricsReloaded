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

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Query;

import java.util.*;

/**
 * @author Aleksandr Chudov.
 * Number of classes used by this class as attribute.
 */
public class DataAbstractionCouplingClassCalculator extends ClassCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            final Set<PsiClass> classes = new HashSet<PsiClass>();
            final PsiField[] fields = aClass.getFields();
            for (final PsiField field : fields) {
                if (!field.isPhysical()) {
                    continue;
                }
                final PsiType type = field.getType().getDeepComponentType();
                final PsiClass classInType = PsiUtil.resolveClassInType(type);
                if (classInType == null) {
                    continue;
                }
                classes.add(classInType);
            }
            postMetric(aClass, classes.size());
            super.visitClass(aClass);
        }
    }
}
