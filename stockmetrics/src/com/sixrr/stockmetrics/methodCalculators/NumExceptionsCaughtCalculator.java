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

import com.intellij.psi.*;
import com.sixrr.metrics.utils.MethodUtils;

import java.util.HashSet;
import java.util.Set;

public class NumExceptionsCaughtCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private final Set<String> caughtExceptions = new HashSet<String>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                caughtExceptions.clear();
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                final int numCaughtExceptions = caughtExceptions.size();
                postMetric(method, numCaughtExceptions);
            }
        }

        @Override
        public void visitTryStatement(PsiTryStatement statement) {
            super.visitTryStatement(statement);
            final PsiParameter[] catchBlockParameters = statement.getCatchBlockParameters();
            for (final PsiParameter parameter : catchBlockParameters) {
                final PsiType parameterType = parameter.getType();
                final String parameterClassName = parameterType.getCanonicalText();
                caughtExceptions.add(parameterClassName);
            }
        }
    }
}
