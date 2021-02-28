/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ProfileSelectionPanel extends JPanel {

    public ProfileSelectionPanel(Project project) {
        super(new GridBagLayout());

        final ComboboxWithBrowseButton comboboxWithBrowseButton =
                buildComboBoxWithBrowseButton(project, MetricsProfileRepository.getInstance());

        final JComponent separator = new TitledSeparator(MetricsReloadedBundle.message("metrics.profile"));
        final JCheckBox checkBox = buildCheckBox(MetricsReloadedConfig.getInstance());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets.left = 0;
        constraints.insets.bottom = 8;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        add(separator, constraints);

        constraints.insets.left = 12;
        constraints.gridy = 1;
        add(comboboxWithBrowseButton, constraints);

        constraints.gridy = 2;
        constraints.weighty = 1.0;
        add(checkBox, constraints);
    }

    private static JCheckBox buildCheckBox(MetricsReloadedConfig configuration) {
        final JCheckBox checkBox = new JCheckBox(
                MetricsReloadedBundle.message("show.only.results.which.exceed.metrics.thresholds"));
        checkBox.setSelected(configuration.isShowOnlyWarnings());
        checkBox.addActionListener(event -> configuration.setShowOnlyWarnings(checkBox.isSelected()));
        return checkBox;
    }

    private static ComboboxWithBrowseButton buildComboBoxWithBrowseButton(Project project,
                                                                          MetricsProfileRepository repository) {
        final ComboBox<MetricsProfile> comboBox = new ComboBox<>(new DefaultComboBoxModel<>(repository.getProfiles()));
        comboBox.setRenderer(new ProfileListCellRenderer());
        final ComboboxWithBrowseButton comboboxWithBrowseButton = new ComboboxWithBrowseButton(comboBox);
        comboBox.setSelectedItem(repository.getSelectedProfile());
        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.DESELECTED) {
                return;
            }
            final MetricsProfile selectedProfile = (MetricsProfile) comboBox.getSelectedItem();
            if (selectedProfile != null) {
                repository.setSelectedProfile(selectedProfile);
            }
        });
        comboboxWithBrowseButton.addActionListener(e -> {
            new MetricsConfigurationDialog(project, repository).show();
            comboBox.setModel(new DefaultComboBoxModel<>(repository.getProfiles()));
            comboBox.setSelectedItem(repository.getSelectedProfile());
        });
        return comboboxWithBrowseButton;
    }
}
