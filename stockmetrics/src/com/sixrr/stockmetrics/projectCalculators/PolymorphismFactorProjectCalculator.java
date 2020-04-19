/*
 * Copyright 2005-2020, Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;

import java.util.HashMap;
import java.util.Map;

public class PolymorphismFactorProjectCalculator extends ProjectCalculator {
    private Map<PsiClass, Integer> subclassesPerClass = new HashMap<>();
    private int numOverridingMethods = 0;
    private int numOverridePotentials = 0;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiMethod[] methods = aClass.getMethods();
            for (PsiMethod method : methods) {
                final PsiMethod[] superMethods = method.findSuperMethods();
                if (superMethods.length == 0) {
                    numOverridePotentials += getSubclassCount(aClass);
                } else {
                    numOverridingMethods++;
                }
            }
        }
    }

    @Override
    public void endMetricsRun() {
        postMetric(numOverridingMethods, numOverridePotentials);
    }

    private int getSubclassCount(final PsiClass aClass) {
        if (subclassesPerClass.containsKey(aClass)) {
            return subclassesPerClass.get(aClass);
        }
        final int[] numSubclasses = new int[1];
        final Runnable runnable = () -> {
            final Project project = executionContext.getProject();
            final GlobalSearchScope globalScope = GlobalSearchScope.allScope(project);
            final Query<PsiClass> query = ClassInheritorsSearch.search(
                    aClass, globalScope, true, true, true);
            for (final PsiClass inheritor : query) {
                if (!inheritor.isInterface()) {
                    numSubclasses[0]++;
                }
            }
        };
        final ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.runProcess(runnable, null);
        subclassesPerClass.put(aClass, numSubclasses[0]);
        return numSubclasses[0];
    }
}
