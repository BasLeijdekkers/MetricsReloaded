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
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.LineUtil;
import com.sixrr.metrics.utils.TestUtils;

import java.util.Set;

public class LinesOfTestCodeRecursivePackageCalculator extends PackageCalculator {
    private final BuckettedCount<PsiPackage> numLinesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numLinesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numLines = numLinesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, (double) numLines);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final int lineCount = LineUtil.countLines(file);
            final PsiPackage[] packageNames = ClassUtils.calculatePackagesRecursive(file);
            for (final PsiPackage packageName : packageNames) {
                numLinesPerPackage.createBucket(packageName);
                if (TestUtils.isTest(file)) {
                    numLinesPerPackage.incrementBucketValue(packageName, lineCount);
                }
            }
        }
    }
}
