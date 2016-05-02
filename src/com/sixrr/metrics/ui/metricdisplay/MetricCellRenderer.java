/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.utils.FormatUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

class MetricCellRenderer extends ColoredTableCellRenderer {

    private final MetricInstance metricInstance;

    MetricCellRenderer(MetricInstance metricInstance) {
        this.metricInstance = metricInstance;
    }

    @Override
    protected void customizeCellRenderer(JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
        final MetricTableModel model = (MetricTableModel) table.getModel();
        if (value instanceof String) { // measured object
            if (model.hasSummaryRows() && row == model.getRowCount() - 2) {
                append((String) value, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
            }
            else {
                append((String) value);
            }
        }
        if (metricInstance == null) {
            return;
        }
        setTextAlign(SwingConstants.RIGHT);
        final Metric metric = metricInstance.getMetric();
        if (value instanceof Double) { // regular value
            double doubleValue = ((Double) value).doubleValue();
            final MetricType metricType = metric.getType();
            if (metricType == MetricType.Ratio || metricType == MetricType.RecursiveRatio) {
                doubleValue *= 100.0;
            }
            if (model.hasSummaryRows()) {
                if (row == model.getRowCount() - 1) {
                    append(FormatUtils.formatValue(metric, (Double) value, true));
                    return;
                }
                else if (row == model.getRowCount() - 2) {
                    append(FormatUtils.formatValue(metric, (Double) value), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                    return;
                }
            }
            final String stringValue = FormatUtils.formatValue(metric, (Double) value);
            if (metricInstance.isUpperThresholdEnabled() && doubleValue > metricInstance.getUpperThreshold() ||
                    metricInstance.isLowerThresholdEnabled() && doubleValue < metricInstance.getLowerThreshold()) {
                append(stringValue, SimpleTextAttributes.ERROR_ATTRIBUTES);
            } else {
                append(stringValue);
            }
        } else if (value instanceof Pair) { // diff value
            final Pair<Double, Double> pair = (Pair<Double, Double>) value;
            final Double currentValue = pair.getFirst();
            final Double prevValue = pair.getSecond();
            final boolean average = model.hasSummaryRows() && row == model.getRowCount() - 1;
            final StringBuilder stringValue = new StringBuilder(16);
            final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
            Color backgroundColor = null;
            if (currentValue != null && prevValue != null && prevValue.equals(currentValue)) {
                stringValue.append(FormatUtils.formatValue(metric, currentValue, average));
            } else {
                if (currentValue != null) {
                    stringValue.append(FormatUtils.formatValue(metric, currentValue, average));
                    if (prevValue == null) {
                        backgroundColor = colorsScheme.getColor(EditorColors.ADDED_LINES_COLOR);
                    }
                }
                if (currentValue != null && prevValue != null) {
                    stringValue.append('/');
                    backgroundColor = colorsScheme.getColor(EditorColors.MODIFIED_LINES_COLOR);
                }
                if (prevValue != null) {
                    stringValue.append(FormatUtils.formatValue(metric, prevValue, average));
                    if (currentValue == null) {
                        backgroundColor = colorsScheme.getColor(EditorColors.DELETED_LINES_COLOR);
                    }
                }
            }
            final int style =
                    (model.hasSummaryRows() && row == model.getRowCount() - 2) ? SimpleTextAttributes.STYLE_BOLD : -1;
            final SimpleTextAttributes attributes =
                    SimpleTextAttributes.REGULAR_ATTRIBUTES.derive(style, null, backgroundColor, null);
            append(stringValue.toString(), attributes);
        }
    }
}
