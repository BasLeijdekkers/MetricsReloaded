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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.openapi.module.Module;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;
import com.sixrr.stockmetrics.utils.CyclomaticComplexityUtil;

import java.util.Set;

public class AverageCyclomaticComplexityModuleCalculator extends ModuleCalculator {

    private final BucketedCount<Module> totalComplexityPerModule = new BucketedCount<>();
    private final BucketedCount<Module> numMethodsPerModule = new BucketedCount<>();

    @Override
    public void endMetricsRun() {
        final Set<Module> modules = numMethodsPerModule.getBuckets();
        for (final Module module : modules) {
            final int numMethods = numMethodsPerModule.getBucketValue(module);
            final int totalComplexity = totalComplexityPerModule.getBucketValue(module);

            postMetric(module, totalComplexity, numMethods);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (MethodUtils.isAbstract(method)) {
                return;
            }
            final Module module = ClassUtils.calculateModule(method);
            if (module == null) {
                return;
            }
            final int complexity = CyclomaticComplexityUtil.calculateComplexity(method);
            totalComplexityPerModule.incrementBucketValue(module, complexity);
            numMethodsPerModule.incrementBucketValue(module);
        }
    }
}
