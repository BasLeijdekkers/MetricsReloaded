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

package com.sixrr.metrics.ui.dialogs;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
    private JTextPane textPane;
    private JPanel contentPanel;
    private JLabel urlLabel;
    private JLabel moreInformationLabel;

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
        if(helpString == null)
        {
            urlLabel.setVisible(false);
            moreInformationLabel.setVisible(false);
        }
        else
        {
            urlLabel.setVisible(true);
            urlLabel.setText(helpString);
            urlLabel.setForeground(Color.BLUE);
            moreInformationLabel.setVisible(true);
        }
        urlLabel.addMouseListener(new MouseAdapter() {
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
        } catch (IOException e) {
            return false;
        }
    }

    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.ExplanationDialog";

    }

    public Action[] createActions() {
        return new Action[0];
    }


    @Nullable
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

}
