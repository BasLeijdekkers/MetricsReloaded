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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;
import com.sixrr.stockmetrics.utils.LineUtil;

import java.util.Set;

public class TestRatioPackageCalculator extends PackageCalculator {

    private final BuckettedCount<PsiPackage> numLinesPerPackage = new BuckettedCount<PsiPackage>();
    private final BuckettedCount<PsiPackage> numTestLinesPerPackage = new BuckettedCount<PsiPackage>();

    public void endMetricsRun() {
        final Set<PsiPackage> packages = numLinesPerPackage.getBuckets();
        for (final PsiPackage aPackage : packages) {
            final int numLines = numLinesPerPackage.getBucketValue(aPackage);
            final int numTestLines = numTestLinesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numTestLines, numLines);
        }
    }

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitJavaFile(PsiJavaFile file) {
            super.visitJavaFile(file);
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            final int lineCount = LineUtil.countLines(file);
            numLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            if (TestUtils.isTest(file)) {
                numTestLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            }
        }
    }
}
