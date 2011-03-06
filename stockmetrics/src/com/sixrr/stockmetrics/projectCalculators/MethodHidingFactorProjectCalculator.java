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

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.Bag;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodHidingFactorProjectCalculator extends ProjectCalculator {
    
    private int numMethods = 0;
    private int numPublicMethods = 0;
    private int numClasses = 0;
    private int totalVisibility = 0;
    private Bag<String> classesPerPackage = new Bag<String>();
    private Bag<String> packageVisibleMethodsPerPackage = new Bag<String>();
    private Map<PsiClass, Integer> subclassesPerClass = new HashMap<PsiClass, Integer>();

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            numMethods++;
            final PsiClass containingClass = method.getContainingClass();

            if (method.hasModifierProperty(PsiModifier.PRIVATE) ||
                    containingClass.hasModifierProperty(PsiModifier.PRIVATE)) {
                //don't do anything
            } else if (method.hasModifierProperty(PsiModifier.PROTECTED) ||
                    containingClass.hasModifierProperty(PsiModifier.PROTECTED)) {
                totalVisibility += getSubclassCount(containingClass);
            } else if ((method.hasModifierProperty(PsiModifier.PUBLIC) || containingClass.isInterface()) &&
                    containingClass.hasModifierProperty(PsiModifier.PUBLIC)) {
                numPublicMethods++;
            } else {
                final String packageName = ClassUtils.calculatePackageName(containingClass);
                packageVisibleMethodsPerPackage.add(packageName);
            }
        }

        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            numClasses++;
            final String packageName = ClassUtils.calculatePackageName(aClass);
            classesPerPackage.add(packageName);
        }
    }

    private int getSubclassCount(final PsiClass aClass) {
        if (subclassesPerClass.containsKey(aClass)) {
            return subclassesPerClass.get(aClass);
        }
        final int[] numSubclasses = new int[1];
        final Runnable runnable = new Runnable() {
            public void run() {
                final Project project = executionContext.getProject();
                final GlobalSearchScope globalScope = GlobalSearchScope.allScope(project);
                final Query<PsiClass> query = ClassInheritorsSearch.search(
                        aClass, globalScope, true, true, true);
                for (final PsiClass inheritor : query) {
                    if (!inheritor.isInterface()) {
                        numSubclasses[0]++;
                    }
                }
            }
        };
        final ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.runProcess(runnable, null);
        subclassesPerClass.put(aClass, numSubclasses[0]);
        return numSubclasses[0];
    }

    public void endMetricsRun() {
        totalVisibility += numPublicMethods * (numClasses - 1);
        final Set<String> packages = classesPerPackage.getContents();
        for (String aPackage : packages) {
            final int visibleMethods = packageVisibleMethodsPerPackage.getCountForObject(aPackage);
            final int classes = classesPerPackage.getCountForObject(aPackage);
            totalVisibility += visibleMethods * (classes - 1);
        }
        final int denominator = numMethods * (numClasses - 1);
        final int numerator = denominator - totalVisibility;
        postMetric(numerator, denominator);
    }
}
