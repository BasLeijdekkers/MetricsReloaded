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

package com.sixrr.stockmetrics.interfaceCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.sixrr.stockmetrics.dependency.DependencyMap;

import java.util.Set;

public class NumTransitiveDependenciesInterfaceCalculator extends InterfaceCalculator {
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (isInterface(aClass)) {
                final DependencyMap dependencyMap = getDependencyMap();
                final Set<PsiClass> dependencies = dependencyMap.calculateTransitiveDependencies(aClass);
                final int numDependencies = dependencies.size() - 1;
                postMetric(aClass, numDependencies);
            }
        }
    }
}
