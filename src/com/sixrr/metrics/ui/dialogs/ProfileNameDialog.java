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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileNameDialog extends  DialogWrapper {
    private JPanel panel;
    private JButton cancelButton;
    private JButton okButton;
    private JTextField nameField;
    private String newName;
    private boolean okSelected;

    public ProfileNameDialog(Project project) {
        super(project, false);
        bindOKButton();
        bindCancelButton();
        bindNameField();
        nameField.setText("");
        okButton.setEnabled(false);
        init();
    }

    private void bindNameField() {

        final DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                final String text = nameField.getText();
                okButton.setEnabled(text != null && text.length() != 0);
            }
        };
        final Document nameDocument = nameField.getDocument();
        nameDocument.addDocumentListener(listener);
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return panel;
    }

    private void bindCancelButton() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close(0);
            }
        });
    }

    private void bindOKButton() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newName = nameField.getText();
                okSelected = true;
                close(0);
            }
        });
    }

    public void run() {
        okSelected = false;
        show();
    }

    public String getNewName() {
        return newName;
    }

    public boolean isOkSelected() {
        return okSelected;
    }

    public JComponent getContentPanel() {
        return panel;
    }

    public Action[] createActions() {
        return new Action[0];
    }

    public String getTitle() {
        return MetricsReloadedBundle.message("enter.profile.name");
    }

    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.ProfileNameDialog";

    }

}
