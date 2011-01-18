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

package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MethodInheritanceFactorProjectCalculator extends ProjectCalculator {
    private int availableMethods = 0;
    private int inheritedMethods = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            final PsiMethod[] allMethods = aClass.getAllMethods();
            final Set<PsiMethod> nonOverriddenMethods = new HashSet<PsiMethod>();
            for (PsiMethod method : allMethods) {
                boolean overrideFound = false;
                for (PsiMethod testMethod : allMethods) {
                    if (overrides(testMethod, method)) {
                        overrideFound = true;
                        break;
                    }
                }
                if (!overrideFound) {
                    nonOverriddenMethods.add(method);
                }
            }
            for (PsiMethod method : nonOverriddenMethods) {
                final PsiClass containingClass = method.getContainingClass();
                if (containingClass != null) {
                    if (containingClass.equals(aClass)) {
                        availableMethods++;
                    } else if (classIsInLibrary(containingClass)) {

                    } else if (!method.hasModifierProperty(PsiModifier.PRIVATE)) {
                        availableMethods++;
                        inheritedMethods++;
                    }
                }
            }
        }

        private boolean overrides(PsiMethod testMethod, PsiMethod method) {
            if (testMethod.equals(method)) {
                return false;
            }
            final PsiMethod[] superMethods = testMethod.findSuperMethods();
            for (PsiMethod superMethod : superMethods) {
                if (superMethod.equals(method)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void endMetricsRun() {
        postMetric(inheritedMethods, availableMethods);
    }

    public static boolean classIsInLibrary(@NotNull PsiClass aClass) {
        final PsiFile file = aClass.getContainingFile();
        if (file == null) {
            return false;
        }
        final String fileName = file.getName();
        //noinspection HardCodedStringLiteral
        return fileName != null && !fileName.endsWith(".java");
    }
}
