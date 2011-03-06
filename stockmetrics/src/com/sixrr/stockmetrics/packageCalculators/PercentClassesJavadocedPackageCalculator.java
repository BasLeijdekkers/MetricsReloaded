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

package com.sixrr.stockmetrics.packageCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.javadoc.PsiDocComment;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public class PercentClassesJavadocedPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numJavadocedClassesPerPackage = new BuckettedCount<PsiPackage>();
    private final BuckettedCount<PsiPackage> numClassesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numClassesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numClasses = numClassesPerPackage.getBucketValue(aPackage);
            final int numJavadocedClasses = numJavadocedClassesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numJavadocedClasses, numClasses);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiPackage aPackage = ClassUtils.findPackage(aClass);
            if (aPackage == null) {
                return;
            }
            if (ClassUtils.isAnonymous(aClass)) {
                return;
            }
            if (aClass.getFirstChild()instanceof PsiDocComment) {
                numJavadocedClassesPerPackage.incrementBucketValue(aPackage);
            }
            numClassesPerPackage.incrementBucketValue(aPackage);
        }
    }
}
