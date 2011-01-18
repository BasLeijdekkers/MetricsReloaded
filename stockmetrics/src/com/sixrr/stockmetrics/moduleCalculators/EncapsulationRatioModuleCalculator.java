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

package com.sixrr.stockmetrics.moduleCalculators;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.stockmetrics.ClassReferenceCache;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.TestUtils;

import java.util.Collection;

public class EncapsulationRatioModuleCalculator extends ElementRatioModuleCalculator {
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (!TestUtils.isTest(aClass) && !ClassUtils.isAnonymous(aClass)) {
                incrementDenominator(aClass, 1);
                if (isInternal(aClass)) {
                    incrementNumerator(aClass, 1);
                }
            }
        }
    }

    private boolean isInternal(PsiClass aClass) {
        final String moduleName = ClassUtils.calculateModuleName(aClass);
        final Key<ClassReferenceCache> key = new Key<ClassReferenceCache>("ClassReferenceCache");

        ClassReferenceCache classReferenceCache = executionContext.getUserData(key);
        if (classReferenceCache == null) {
            classReferenceCache = new ClassReferenceCache();
            executionContext.putUserData(key, classReferenceCache);
        }
        final Collection<PsiReference> references =
                classReferenceCache.findClassReferences(aClass);
        for (final PsiReference reference : references) {
            final PsiElement element = reference.getElement();
            final PsiClass referencingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);

            if (referencingClass != null && !TestUtils.isTest(referencingClass)) {
                final String referencingModuleName = ClassUtils.calculateModuleName(referencingClass);
                if (!moduleName.equals(referencingModuleName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void visitFile(PsiFile file) {
        final Module module = ClassUtils.calculateModule(file);
        numeratorPerModule.createBucket(module);
        denominatorPerModule.createBucket(module);
    }
}
