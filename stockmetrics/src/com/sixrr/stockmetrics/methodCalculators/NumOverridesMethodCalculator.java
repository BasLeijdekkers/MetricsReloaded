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

package com.sixrr.stockmetrics.methodCalculators;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.MethodUtils;

import java.util.Collection;

public class NumOverridesMethodCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitMethod(final PsiMethod method) {
            final Runnable runnable = new Runnable() {
                public void run() {
                    if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                        final Project project = executionContext.getProject();
                        final GlobalSearchScope globalScope = GlobalSearchScope.projectScope(project);
                        final Query<PsiMethod> query =
                                OverridingMethodsSearch.search(method,
                                        globalScope, true);
                        final Collection<PsiMethod> overridingMethods =
                                query.findAll();
                        postMetric(method, overridingMethods.size());
                    }
                    methodNestingDepth++;
                }
            };
            final ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.runProcess(runnable, null);
            super.visitMethod(method);
            methodNestingDepth--;
        }
    }
}
