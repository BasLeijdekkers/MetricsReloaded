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
import com.intellij.openapi.ui.FixedSizeButton;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.metricdisplay.MetricsConfigurationPanel;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.plugin.MetricsPlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ProfileSelectionPanel {
    private final Project project;
    private JComboBox profilesDropdown;
    private MetricsProfileRepository repository;
    private JComponent panel;
    private FixedSizeButton editProfileButton;
    private JCheckBox showOnlyWarningsCheckbox;

    public ProfileSelectionPanel(Project project,
                                 MetricsProfileRepository repository) {
        this.repository = repository;
        this.project = project;
        setupProfilesDropdown();
        final MetricsPlugin plugin = project.getComponent(MetricsPlugin.class);
        final MetricsReloadedConfig configuration = plugin.getConfiguration();
        showOnlyWarningsCheckbox.setSelected(configuration.isShowOnlyWarnings());
        showOnlyWarningsCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                configuration.setShowOnlyWarnings(showOnlyWarningsCheckbox.isSelected());
            }
        });
    }

    private void setupProfilesDropdown() {
        final String[] profiles = repository.getProfileNames();
        final MutableComboBoxModel profilesModel = new DefaultComboBoxModel(
                profiles);
        profilesDropdown.setModel(profilesModel);
        final MetricsProfile currentProfile = repository.getCurrentProfile();
        final String currentProfileName = currentProfile.getName();
        profilesDropdown.setSelectedItem(currentProfileName);
        profilesDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    return;
                }
                final String selectedProfile =
                        (String) profilesDropdown.getSelectedItem();
                repository.setSelectedProfile(selectedProfile);
            }
        });
        editProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final MetricsConfigurationPanel configurationPanel =
                        new MetricsConfigurationPanel(project, repository);
                configurationPanel.run();
                final MetricsProfile currentProfile =
                        repository.getCurrentProfile();
                final String currentProfileName = currentProfile.getName();
                profilesDropdown.setSelectedItem(currentProfileName);
            }
        });
    }

    public JComponent getPanel() {
        return panel;
    }

    public boolean showOnlyWarnings()
    {
        return showOnlyWarningsCheckbox.isSelected();
    }
    
    private void createUIComponents() {
        editProfileButton = new FixedSizeButton(25);
    }
}
