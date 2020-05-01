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
package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class AttributeHidingFactorProjectCalculator extends ProjectCalculator {
    private int numAttributes = 0;
    private int numPublicAttributes = 0;
    private int numClasses = 0;
    private int totalVisibility = 0;
    private final BucketedCount<String> classesPerPackage = new BucketedCount<>();
    private final BucketedCount<String> packageVisibleAttributesPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiClass> subclassesPerClass = new BucketedCount<>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            numClasses++;
            final String packageName = ClassUtils.calculatePackageName(aClass);
            classesPerPackage.incrementBucketValue(packageName);
        }

        @Override
        public void visitField(PsiField field) {
            super.visitField(field);
            numAttributes++;
            final PsiClass containingClass = field.getContainingClass();

            if (field.hasModifierProperty(PsiModifier.PRIVATE) ||
                    containingClass.hasModifierProperty(PsiModifier.PRIVATE)) {
                //don't do anything
            } else if (field.hasModifierProperty(PsiModifier.PROTECTED) ||
                    containingClass.hasModifierProperty(PsiModifier.PROTECTED)) {
                totalVisibility += getSubclassCount(containingClass);
            } else if ((field.hasModifierProperty(PsiModifier.PUBLIC) || containingClass.isInterface()) &&
                    containingClass.hasModifierProperty(PsiModifier.PUBLIC)) {
                numPublicAttributes++;
            } else {
                final String packageName = ClassUtils.calculatePackageName(containingClass);
                packageVisibleAttributesPerPackage.incrementBucketValue(packageName);
            }
        }
    }

    private int getSubclassCount(final PsiClass aClass) {
        if (subclassesPerClass.containsBucket(aClass)) {
            return subclassesPerClass.getBucketValue(aClass);
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
        subclassesPerClass.incrementBucketValue(aClass, numSubclasses[0]);
        return numSubclasses[0];
    }

    @Override
    public void endMetricsRun() {
        totalVisibility += numPublicAttributes * (numClasses - 1);
        final Set<String> packages = classesPerPackage.getBuckets();
        for (String aPackage : packages) {
            final int visibleAttributes = packageVisibleAttributesPerPackage.getBucketValue(aPackage);
            final int classes = classesPerPackage.getBucketValue(aPackage);
            totalVisibility += visibleAttributes * (classes - 1);
        }
        final int denominator = numAttributes * (numClasses - 1);
        final int numerator = denominator - totalVisibility;
        postMetric(numerator, denominator);
    }
}
