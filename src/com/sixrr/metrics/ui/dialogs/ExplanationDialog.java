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

package com.sixrr.metrics.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.sixrr.metrics.Metric;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ExplanationDialog extends DialogWrapper {

    private static final Action[] EMPTY_ACTION_ARRAY = new Action[0];

    private final JEditorPane descriptionPane = new JEditorPane("text/html", "<html><body></body></html>");

    public ExplanationDialog(Project project) {
        super(project, false);
        descriptionPane.addHyperlinkListener(new DescriptionHyperlinkListener(project));
        descriptionPane.setEditable(false);
        setModal(true);
        init();
        pack();
    }

    public void show(Metric metric) {
        MetricsConfigurationDialog.loadDescription(metric, descriptionPane);
        setTitle(metric.getDisplayName());
        show();
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.ExplanationDialog";
    }

    @NotNull
    @Override
    public Action[] createActions() {
        return EMPTY_ACTION_ARRAY;
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(ScrollPaneFactory.createScrollPane(descriptionPane), constraints);
        return panel;
    }

}
