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

package com.sixrr.stockmetrics.fileTypeMetrics;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

/**
 * @author Bas Leijdekkers
 */
public abstract class ElementRatioFileTypeCalculator extends FileTypeCalculator {

    private final BucketedCount<FileType> numeratorPerModule = new BucketedCount<>();
    private final BucketedCount<FileType> denominatorPerModule = new BucketedCount<>();

    public ElementRatioFileTypeCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public void endMetricsRun() {
        final Set<FileType> fileTypes = numeratorPerModule.getBuckets();
        for (final FileType fileType : fileTypes) {
            final int numerator = numeratorPerModule.getBucketValue(fileType);
            final int denominator = denominatorPerModule.getBucketValue(fileType);

            if (denominator == 0) {
                postMetric(fileType, 0);
            } else {
                postMetric(fileType, numerator, denominator);
            }
        }
    }

    protected void createRatio(PsiElement element) {
        final FileType fileType = ClassUtils.calculateFileType(element);
        if (fileType == null) {
            return;
        }
        numeratorPerModule.createBucket(fileType);
        denominatorPerModule.createBucket(fileType);
    }

    protected void incrementNumerator(PsiElement element, int count) {
        increment(element, count, numeratorPerModule);
    }

    protected void incrementDenominator(PsiElement element, int count) {
        increment(element, count, denominatorPerModule);
    }

    private static void increment(PsiElement element, int count, BucketedCount<FileType> result) {
        final FileType fileType = ClassUtils.calculateFileType(element);
        if (fileType == null) {
            return;
        }
        result.incrementBucketValue(fileType, count);
    }
}
