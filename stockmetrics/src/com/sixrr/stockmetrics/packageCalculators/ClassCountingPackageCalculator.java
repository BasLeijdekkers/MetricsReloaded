/*
 * Copyright 2005-2011, Bas Leijdekkers, Sixth and Red River Software
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
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

abstract class ClassCountingPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numClassesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numClassesPerPackage.getBuckets();
        for (final PsiPackage packageName : packages) {
            final int numClasses = numClassesPerPackage.getBucketValue(packageName);
            postMetric(packageName, numClasses);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (aClass instanceof PsiTypeParameter ||
                    aClass instanceof PsiEnumConstantInitializer) {
                return;
            }
            final PsiPackage psiPackage = ClassUtils.findPackage(aClass);
            numClassesPerPackage.createBucket(psiPackage);

            if (satisfies(aClass)) {
                numClassesPerPackage.incrementBucketValue(psiPackage);
            }
        }
    }

    protected abstract boolean satisfies(PsiClass aClass);
}
