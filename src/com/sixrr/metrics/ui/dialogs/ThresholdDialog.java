/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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
package com.sixrr.metrics.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ThresholdDialog extends DialogWrapper {

    private final JBTable thresholdTable;

    public ThresholdDialog(Project project, String profileName,
                           List<MetricInstance> metrics, MetricsResult result) {
        super(project, false);
        final ThresholdTableModel model = new ThresholdTableModel(metrics, result);
        thresholdTable = new JBTable(model);
        setTitle(MetricsReloadedBundle.message("thresholds.for.profile", profileName));
        init();
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(thresholdTable);
        final Dimension preferredSize = thresholdTable.getPreferredSize();
        preferredSize.height += thresholdTable.getRowHeight() + 2; // header height
        scrollPane.setPreferredSize(preferredSize);
        panel.add(scrollPane, constraints);
        return panel;
    }
}
