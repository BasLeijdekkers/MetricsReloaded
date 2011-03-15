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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class ExplanationDialog extends DialogWrapper {

    private final JTextPane textPane = new JTextPane();
    private final JLabel urlLabel = new JLabel();
    private final JLabel moreInformationLabel = new JLabel(MetricsReloadedBundle.message(
            "for.more.information.go.to"));

    public ExplanationDialog(Project project) {
        super(project, false);
        setModal(true);
        init();
    }

    public void run(Metric metric) {
        @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
        final boolean resourceFound = setDescriptionFromResource(descriptionName, metric);
        if (!resourceFound) {
            setDescriptionFromResource("/metricsDescriptions/UnderConstruction.html", metric);
        }
        setTitle(MetricsReloadedBundle.message("explanation.dialog.title", metric.getDisplayName()));
        final String helpString = metric.getHelpDisplayString();
        final String helpURL = metric.getHelpURL();
        if (helpString == null) {
            urlLabel.setVisible(false);
            moreInformationLabel.setVisible(false);
        } else {
            urlLabel.setVisible(true);
            urlLabel.setText(helpString);
            urlLabel.setForeground(Color.BLUE);
            moreInformationLabel.setVisible(true);
        }
        urlLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (helpURL != null) {
                    BrowserUtil.launchBrowser("http://" + helpURL);
                }
            }
        });
        show();
    }

    private boolean setDescriptionFromResource(@NonNls String resourceName, Metric metric) {
        try {
            final URL resourceURL = metric.getClass().getResource(resourceName);
            textPane.setPage(resourceURL);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.ExplanationDialog";

    }

    @Override
    public Action[] createActions() {
        return new Action[] {
                getOKAction()
        };
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
        panel.add(ScrollPaneFactory.createScrollPane(textPane), constraints);
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.gridy = 1;
        panel.add(moreInformationLabel, constraints);
        constraints.gridx = 1;
        constraints.insets.left = 5;
        panel.add(urlLabel, constraints);
        return panel;
    }

}
