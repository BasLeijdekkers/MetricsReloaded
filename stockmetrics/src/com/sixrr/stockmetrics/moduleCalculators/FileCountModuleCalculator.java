/*
 * Copyright 2005-2022 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.sixrr.metrics.Metric;
import org.jetbrains.annotations.NotNull;

public class FileCountModuleCalculator extends ElementCountModuleCalculator {

    private final FileType fileType;

    public FileCountModuleCalculator(@NotNull Metric metric) {
        super(metric);
        fileType = null;
    }

    public FileCountModuleCalculator(@NotNull Metric metric, @NotNull FileType fileType) {
        super(metric);
        this.fileType = fileType;
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiElementVisitor {

        @Override
        public void visitFile(@NotNull PsiFile file) {
            super.visitFile(file);
            incrementCount(file, fileType == null || file.getFileType() == fileType ? 1 : 0);
        }
    }
}
