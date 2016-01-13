/*
 * Copyright 2005-2016 Bas Leijdekkers, Sixth and Red River Software
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
import com.intellij.psi.*;
import com.sixrr.metrics.utils.BucketedCount;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.Set;

public abstract class ClassCountingModuleCalculator extends ModuleCalculator {

    private final BucketedCount<Module> numClassesPerModule = new BucketedCount<Module>();

    protected abstract boolean satisfies(PsiClass aClass);

    @Override
    public void endMetricsRun() {
        final Set<Module> modules = numClassesPerModule.getBuckets();
        for (final Module module : modules) {
            final int numClasses = numClassesPerModule.getBucketValue(module);
            postMetric(module, numClasses);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (aClass instanceof PsiTypeParameter ||
                    aClass instanceof PsiEnumConstantInitializer) {
                return;
            }
            final Module module = ClassUtils.calculateModule(aClass);
            if (module == null) {
                return;
            }
            numClassesPerModule.createBucket(module);
            if (satisfies(aClass)) {
                numClassesPerModule.incrementBucketValue(module);
            }
        }
    }
}
