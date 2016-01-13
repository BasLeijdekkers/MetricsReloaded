/*
 * Copyright 2016 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.moduleMetrics;

import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.sixrr.metrics.MetricCalculator;
import com.sixrr.metrics.MetricType;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.moduleCalculators.ElementCountModuleCalculator;
import com.sixrr.stockmetrics.utils.LineUtil;
import org.jetbrains.annotations.Nullable;

public class LinesOfJavaScriptModuleMetric extends ModuleMetric {

    public String getDisplayName() {
        return StockMetricsBundle.message("lines.of.javascript.display.name");
    }

    public String getAbbreviation() {
        return StockMetricsBundle.message("lines.of.javascript.abbreviation");
    }

    public MetricType getType() {
        return MetricType.Count;
    }

    @Nullable
    @Override
    public MetricCalculator createCalculator() {
        return new LinesOfJavaScriptModuleCalculator();
    }

    private static class LinesOfJavaScriptModuleCalculator extends ElementCountModuleCalculator {

        protected PsiElementVisitor createVisitor() {
            return new PsiElementVisitor() {

                public void visitFile(PsiFile file) {
                    super.visitFile(file);
                    if (file.getFileType() == JavaScriptFileType.INSTANCE) {
                        final int lineCount = LineUtil.countLines(file);
                        incrementElementCount(file, lineCount);
                    }
                }
            };
        }

    }
}
