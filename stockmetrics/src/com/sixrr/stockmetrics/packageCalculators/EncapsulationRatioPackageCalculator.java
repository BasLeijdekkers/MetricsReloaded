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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;
import com.sixrr.stockmetrics.utils.ClassReferenceCache;

import java.util.Set;

public class EncapsulationRatioPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numClassesPerPackage = new BucketedCount<PsiPackage>();
    private final BucketedCount<PsiPackage> numInternalClassesPerPackage = new BucketedCount<PsiPackage>();

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numClassesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numClasses = numClassesPerPackage.getBucketValue(aPackage);
            final int numInternalClasses = numInternalClassesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numInternalClasses, numClasses);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiPackage aPackage = ClassUtils.findPackage(aClass);
            if (aPackage == null) {
                return;
            }
            numClassesPerPackage.createBucket(aPackage);
            if (!TestUtils.isTest(aClass) && !ClassUtils.isAnonymous(aClass)) {
                numClassesPerPackage.incrementBucketValue(aPackage);
                if (isInternal(aClass)) {
                    numInternalClassesPerPackage.incrementBucketValue(aPackage);
                }
            }
        }

        private boolean isInternal(PsiClass aClass) {
            if (aClass.hasModifierProperty(PsiModifier.PRIVATE) ||
                    aClass.hasModifierProperty(PsiModifier.PACKAGE_LOCAL)) {
                return true;
            }
            final String packageName = ClassUtils.calculatePackageName(aClass);
            final Key<ClassReferenceCache> key = new Key<ClassReferenceCache>("ClassReferenceCache");

            ClassReferenceCache classReferenceCache = executionContext.getUserData(key);
            if (classReferenceCache == null) {
                classReferenceCache = new ClassReferenceCache();
                executionContext.putUserData(key, classReferenceCache);
            }
            for (final PsiReference reference : classReferenceCache.findClassReferences(aClass)) {
                final PsiElement element = reference.getElement();
                final PsiClass referencingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);

                if (referencingClass == null || TestUtils.isTest(referencingClass)) {
                    continue;
                }
                final String referencingPackageName = ClassUtils.calculatePackageName(referencingClass);
                if (!packageName.equals(referencingPackageName)) {
                    return false;
                }
            }
            return true;
        }
    }
}
