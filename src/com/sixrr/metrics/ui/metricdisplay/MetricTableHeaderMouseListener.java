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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.project.Project;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricType;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MetricTableHeaderMouseListener extends MouseAdapter {
    private final Project project;
    private final JTable table;

    MetricTableHeaderMouseListener(Project project, JTable table) {
        super();
        this.project = project;
        this.table = table;
    }

    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        final int button = e.getButton();
        final int x = e.getX();
        final int y = e.getY();
        final JTableHeader tableHeader = table.getTableHeader();
        final Point point = new Point(x, y);
        final int column = tableHeader.columnAtPoint(point);
        if (column == -1) {
            return;
        }
        table.setColumnSelectionInterval(column, column);
        table.repaint();

        if (button == MouseEvent.BUTTON3 || e.isControlDown()) {
            showPopupMenu(e);
        } else if (button == MouseEvent.BUTTON1) {
            final MetricTableModel model = (MetricTableModel) table.getModel();
            final int modelColumn = table.convertColumnIndexToModel(column);
            final int sortColumn = model.getSortColumn();
            if (sortColumn == modelColumn) {
                final boolean ascending = model.isAscending();
                model.changeSort(modelColumn, !ascending);
            } else {
                model.changeSort(modelColumn, true);
            }
            tableHeader.repaint();
            table.setColumnSelectionInterval(column, column);
            table.repaint();
        }
    }

    private void showPopupMenu(MouseEvent e) {
        final int selectedColumn = table.getSelectedColumn();
        if (selectedColumn == -1 || selectedColumn == 0) {
            return;
        }
        final JPopupMenu popup = new JPopupMenu();

        final MetricTableModel model = (MetricTableModel) table.getModel();
        final Metric metric = model.getMetricForColumn(selectedColumn).getMetric();
        final MetricType metricType = metric.getType();
        if (model.hasDiff()) {
            popup.add(new JMenuItem(new ShowDiffDistributionAction(project, table)));
            popup.add(new JMenuItem(new ShowDiffHistogramAction(project, table)));
        } else {
            if (!metricType.equals(MetricType.RecursiveCount) && !metricType.equals(MetricType.RecursiveRatio)) {
                popup.add(new JMenuItem(new ShowDistributionAction(project, table)));
                popup.add(new JMenuItem(new ShowHistogramAction(project, table)));
            }
            if (metricType.equals(MetricType.Count)) {
                popup.add(new JMenuItem(new ShowPieChartAction(project, table)));
            }
            popup.add(new JMenuItem(new ShowExplanationAction(project, table)));
        }
        final int x = e.getX();
        final int y = e.getY();

        final JTableHeader tableHeader = table.getTableHeader();
        popup.show(tableHeader, x, y);
    }
}
