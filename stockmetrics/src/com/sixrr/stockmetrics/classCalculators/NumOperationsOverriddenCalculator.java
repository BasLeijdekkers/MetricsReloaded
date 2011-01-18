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

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;

import java.util.Collection;

public class NumOperationsOverriddenCalculator extends ClassCalculator {

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(final PsiClass aClass) {
            super.visitClass(aClass);
            final Runnable runnable = new Runnable() {
                public void run() {
                    if (!ClassUtils.isAnonymous(aClass) && !aClass.isInterface()) {
                        final PsiMethod[] methods = aClass.getMethods();
                        final Project project = executionContext.getProject();
                        final GlobalSearchScope globalScope = GlobalSearchScope.allScope(project);
                        int numOverriddenMethods = 0;
                        for (final PsiMethod method : methods) {
                            final Collection<PsiMethod> overrides = OverridingMethodsSearch
                                    .search(method, globalScope, true).findAll();
                            boolean overrideFound = false;
                            for (final PsiMethod override : overrides) {
                                if (!MethodUtils.isAbstract(override)) {
                                    overrideFound = true;
                                }
                            }
                            if (overrideFound) {
                                numOverriddenMethods++;
                            }
                        }
                        postMetric(aClass, numOverriddenMethods);
                    }
                }
            };
            final ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcess(runnable, null);
        }
    }
}
