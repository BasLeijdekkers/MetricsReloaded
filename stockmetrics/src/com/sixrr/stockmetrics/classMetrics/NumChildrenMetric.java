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

package com.sixrr.stockmetrics.classMetrics;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricType;
import com.sixrr.stockmetrics.classCalculators.ClassCalculator;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bas Leijdekkers
 */
public class NumChildrenMetric extends ClassMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.children.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.children.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Score;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new NumberOfChildrenCalculator(this);
    }

    private static class NumberOfChildrenCalculator extends ClassCalculator {

        public NumberOfChildrenCalculator(Metric metric) {
            super(metric);
        }

        @Override
        protected PsiElementVisitor createVisitor() {
            return new Visitor();
        }

        private class Visitor extends JavaRecursiveElementVisitor {

            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);
                if (!isConcreteClass(aClass) || aClass.isEnum()) {
                    return;
                }
                final int numberOfChildren = getNumberOfChildren(aClass);
                postMetric(aClass, numberOfChildren);
            }

            int getNumberOfChildren(final PsiClass aClass) {
                if (aClass.hasModifierProperty(PsiModifier.FINAL)) {
                    return 0;
                }
                return ClassInheritorsSearch.search(aClass, false).findAll().size();
            }
        }
    }
}
