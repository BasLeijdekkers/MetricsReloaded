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

package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.*;
import com.sixrr.metrics.Metric;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Bas Leijdekkers
 */
public class NumModulesInProjectCalculator extends ElementCountProjectCalculator {
    public NumModulesInProjectCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        private final Set<Module> modules = new HashSet<>();
        private ProjectFileIndex index = null;

        @Override
        public void visitFile(@NotNull PsiFile file) {
            super.visitFile(file);
            if (index == null) {
                index = ProjectFileIndex.getInstance(file.getProject());
            }
            if (modules.add(index.getModuleForFile(file.getVirtualFile()))) {
                incrementCount(1);
            }
        }
    }
}
