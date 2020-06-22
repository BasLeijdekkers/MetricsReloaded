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

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SearchTextField;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Bas Leijdekkers
 */
class DescriptionHyperlinkListener implements HyperlinkListener {

    private static final Logger LOG = Logger.getInstance(DescriptionHyperlinkListener.class);

    private final Project project;

    public DescriptionHyperlinkListener(Project project) {
        this.project = project;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                final URI url = new URI(event.getDescription());
                if (url.getScheme().equals("settings")) {
                    final DataContext context = DataManager.getInstance().getDataContextFromFocus().getResult();
                    if (context != null) {
                        final Settings settings = Settings.KEY.getData(context);
                        final SearchTextField searchTextField = SearchTextField.KEY.getData(context);
                        final String configId = url.getHost();
                        final String search = url.getQuery();
                        if (settings != null) {
                            final Configurable configurable = settings.find(configId);
                            settings.select(configurable).doWhenDone(() -> {
                                if (searchTextField != null && search != null) {
                                    searchTextField.setText(search);
                                }
                            });
                        } else {
                            ShowSettingsUtilImpl.showSettingsDialog(project, configId, search);
                        }
                    }
                } else {
                    BrowserUtil.browse(url);
                }
            } catch (URISyntaxException ex) {
                LOG.error(ex);
            }
        }
    }
}
