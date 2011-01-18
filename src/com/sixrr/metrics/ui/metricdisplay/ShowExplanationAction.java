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
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import com.sixrr.metrics.ui.dialogs.ExplanationDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

class ShowExplanationAction extends AbstractAction {
    private final Project project;
    private final JTable table;
    private final MetricTableModel model;

    ShowExplanationAction(Project project, JTable table) {
        super(MetricsReloadedBundle.message("show.explanation.action"));
        this.project = project;
        this.table = table;
        model = (MetricTableModel) table.getModel();
    }

    public void actionPerformed(ActionEvent e) {
        final ExplanationDialog dialog = new ExplanationDialog(project);
        final int selectedColumn = table.getSelectedColumn();
        final int modelColumn = table.convertColumnIndexToModel(selectedColumn);
        final Metric metric = model.getMetricForColumn(modelColumn).getMetric();
        dialog.run(metric);
    }
}
