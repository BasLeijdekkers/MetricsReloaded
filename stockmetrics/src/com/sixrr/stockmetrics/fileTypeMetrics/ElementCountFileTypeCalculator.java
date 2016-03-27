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

package com.sixrr.stockmetrics.fileTypeMetrics;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class ElementCountFileTypeCalculator extends FileTypeCalculator {

    private final BucketedCount<FileType> elementCountsPerFileType = new BucketedCount<FileType>();

    @Override
    public void endMetricsRun() {
        final Set<FileType> fileTypes = elementCountsPerFileType.getBuckets();
        for (FileType fileType : fileTypes) {
            final int count = elementCountsPerFileType.getBucketValue(fileType);
            postMetric(fileType, count);
        }
    }

    public void createCount(@NotNull PsiElement element) {
        final FileType fileType = ClassUtils.calculateFileType(element);
        if (fileType == null) {
            return;
        }
        elementCountsPerFileType.createBucket(fileType);
    }

    protected void incrementCount(PsiElement element, int count) {
        final FileType fileType = ClassUtils.calculateFileType(element);
        if (fileType == null) {
            return;
        }
        elementCountsPerFileType.incrementBucketValue(fileType, count);
    }
}
