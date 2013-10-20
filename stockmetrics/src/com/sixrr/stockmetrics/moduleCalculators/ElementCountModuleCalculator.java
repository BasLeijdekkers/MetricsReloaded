/*
 * Copyright 2005-2013 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.metrics.utils.BuckettedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

abstract class ElementCountModuleCalculator extends ModuleCalculator {

    protected final BuckettedCount<Module> elementsCountPerModule =
            new BuckettedCount<Module>();

    @Override
    public void endMetricsRun() {
        final Set<Module> modules = elementsCountPerModule.getBuckets();
        for (final Module module : modules) {
            final int numCommentLines = elementsCountPerModule.getBucketValue(module);

            postMetric(module, numCommentLines);
        }
    }

    protected void incrementElementCount(PsiElement element, int count) {
        final PsiFile file = PsiTreeUtil.getParentOfType(element, PsiFile.class, false);
        final Module module = ClassUtils.calculateModule(file);
        if (module == null) {
            return;
        }
        elementsCountPerModule.incrementBucketValue(module, count);
    }
}
