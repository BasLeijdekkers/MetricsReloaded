package com.sixrr.stockmetrics.projectCalculators;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import com.sixrr.metrics.utils.Bag;
import com.sixrr.metrics.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AttributeHidingFactorProjectCalculator extends ProjectCalculator {
    private int numAttributes = 0;
    private int numPublicAttributes = 0;
    private int numClasses = 0;
    private int totalVisibility = 0;
    private Bag<String> classesPerPackage = new Bag<String>();
    private Bag<String> packageVisibleAttributesPerPackage = new Bag<String>();
    private Map<PsiClass, Integer> subclassesPerClass = new HashMap<PsiClass, Integer>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            numClasses++;
            final String packageName = ClassUtils.calculatePackageName(aClass);
            classesPerPackage.add(packageName);
        }

        @Override
        public void visitField(PsiField field) {
            super.visitField(field);
            numAttributes++;
            final PsiClass containingClass = field.getContainingClass();

            if (field.hasModifierProperty(PsiModifier.PRIVATE) ||
                    containingClass.hasModifierProperty(PsiModifier.PRIVATE)) {
                //dodn't do anythng
            } else if (field.hasModifierProperty(PsiModifier.PROTECTED) ||
                    containingClass.hasModifierProperty(PsiModifier.PROTECTED)) {
                totalVisibility += getSubclassCount(containingClass);
            } else if ((field.hasModifierProperty(PsiModifier.PUBLIC) || containingClass.isInterface()) &&
                    containingClass.hasModifierProperty(PsiModifier.PUBLIC)) {
                numPublicAttributes++;
            } else {
                final String packageName = ClassUtils.calculatePackageName(containingClass);
                packageVisibleAttributesPerPackage.add(packageName);
            }
        }
    }

    private int getSubclassCount(final PsiClass aClass) {
        if (subclassesPerClass.containsKey(aClass)) {
            return subclassesPerClass.get(aClass);
        }
        final int[] numSubclasses = new int[1];
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Project project = executionContext.getProject();
                final GlobalSearchScope globalScope = GlobalSearchScope.allScope(project);
                final Query<PsiClass> query = ClassInheritorsSearch.search(
                        aClass, globalScope, true, true, true);
                for (final PsiClass inheritor : query) {
                    if (!inheritor.isInterface()) {
                        numSubclasses[0]++;
                    }
                }
            }
        };
        final ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.runProcess(runnable, null);
        subclassesPerClass.put(aClass, numSubclasses[0]);
        return numSubclasses[0];
    }

    @Override
    public void endMetricsRun() {
        totalVisibility += numPublicAttributes * (numClasses - 1);
        final Set<String> packages = classesPerPackage.getContents();
        for (String aPackage : packages) {
            final int visibleAttributes = packageVisibleAttributesPerPackage.getCountForObject(aPackage);
            final int classes = classesPerPackage.getCountForObject(aPackage);
            totalVisibility += visibleAttributes * (classes - 1);
        }
        final int denominator = numAttributes * (numClasses - 1);
        final int numerator = denominator - totalVisibility;
        postMetric(numerator, denominator);
    }
}
