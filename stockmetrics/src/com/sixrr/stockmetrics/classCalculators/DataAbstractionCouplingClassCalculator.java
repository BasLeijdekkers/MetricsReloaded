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
import com.intellij.psi.util.PsiUtil;

import java.util.*;

/**
 * @author Aleksandr Chudov.
 * Number of classes used by this class as attribute.
 */
public class DataAbstractionCouplingClassCalculator extends ClassCalculator {
    private final Map<PsiClass, Set<PsiClass>> metrics = new HashMap<PsiClass, Set<PsiClass>>();
    private final Collection<String> visitedTypeNames = new HashSet<String>();
    private final Collection<PsiClass> classes = new HashSet<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    @Override
    public void endMetricsRun() {
        for (final Collection<PsiClass> names : metrics.values()) {
            names.retainAll(classes);
        }
        for (final PsiClass aClass : classes) {
            postMetric(aClass, metrics.get(aClass).size());
        }
        super.endMetricsRun();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            classes.add(aClass);
            visitedTypeNames.add(aClass.getQualifiedName());
            metrics.put(aClass, new HashSet<PsiClass>());
            final PsiField[] fields = aClass.getFields();
            for (final PsiField field : fields) {
                if (!field.isPhysical()) {
                    System.out.println(field.getName());
                    continue;
                }
                final PsiType type = field.getType().getDeepComponentType();
                if (type instanceof PsiPrimitiveType) {
                    continue;
                }
                metrics.get(aClass).add(PsiUtil.resolveClassInClassTypeOnly(type));
            }
        }
    }
}
