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

import com.intellij.openapi.project.Project;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.MetricType;
import com.sixrr.metrics.utils.MetricsCategoryNameUtil;
import com.sixrr.metrics.ui.charts.HistogramDialog;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;

class ShowHistogramAction extends AbstractAction {

    private final Project project;
    private final JTable table;
    private final MetricTableModel model;

    ShowHistogramAction(Project project, JTable table) {
        super(MetricsReloadedBundle.message("show.histogram.action"));
        this.project = project;
        this.table = table;
        model = (MetricTableModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int numRows = model.getRowCount();
        if (numRows > 1) {
            numRows -= 2;
        }
        final Double[] values = new Double[numRows];
        final int selectedColumn = table.getSelectedColumn();
        final int modelColumn = table.convertColumnIndexToModel(selectedColumn);

        for (int i = 0; i < numRows; i++) {
            values[i] = (Double) model.getValueAt(i, modelColumn);
        }
        final Metric metric = model.getMetricForColumn(modelColumn).getMetric();
        final String name = metric.getDisplayName();
        final MetricCategory category = metric.getCategory();
        final String categoryName = MetricsCategoryNameUtil.getShortNameForCategory(category);
        final MetricType metricType = metric.getType();
        final HistogramDialog histogramDialog = new HistogramDialog(project, name, categoryName, values, metricType);
        histogramDialog.show();
    }
}
