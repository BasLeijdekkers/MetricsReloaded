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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.util.Key;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;
import com.sixrr.stockmetrics.ClassReferenceCache;

import java.util.Collection;
import java.util.Set;

public class EncapsulationRatioPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numClassesPerPackage = new BuckettedCount<PsiPackage>();
    private final BuckettedCount<PsiPackage> numInternalClassesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numClassesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numClasses = numClassesPerPackage.getBucketValue(aPackage);
            final int numInternalClasses = numInternalClassesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numInternalClasses, numClasses);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiPackage psiPackage = ClassUtils.findPackage(aClass);
            numClassesPerPackage.createBucket(psiPackage);
            if (!TestUtils.isTest(aClass) && !ClassUtils.isAnonymous(aClass)) {
                numClassesPerPackage.incrementBucketValue(psiPackage);
                if (isInternal(aClass)) {
                    numInternalClassesPerPackage.incrementBucketValue(psiPackage);
                }
            }
        }
    }

    private boolean isInternal(PsiClass aClass) {
        final String packageName = ClassUtils.calculatePackageName(aClass);
        final Key<ClassReferenceCache> key = new Key<ClassReferenceCache>("ClassReferenceCache");

        ClassReferenceCache classReferenceCache = executionContext.getUserData(key);
        if (classReferenceCache == null) {
            classReferenceCache = new ClassReferenceCache();
            executionContext.putUserData(key, classReferenceCache);
        }
        final Collection<PsiReference> references =
                classReferenceCache.findClassReferences(aClass);
        for (final PsiReference reference : references) {
            final PsiElement element = reference.getElement();
            final PsiClass referencingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);

            if (referencingClass != null && !TestUtils.isTest(referencingClass)) {
                final String referencingPackageName = ClassUtils.calculatePackageName(referencingClass);
                if (!packageName.equals(referencingPackageName)) {
                    return false;
                }
            }
        }
        return true;
    }
}
