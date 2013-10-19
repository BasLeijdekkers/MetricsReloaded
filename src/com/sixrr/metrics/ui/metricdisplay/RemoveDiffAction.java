/*
 * Copyright 2005-2013 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

class RemoveDiffAction extends AnAction {
    
    private final MetricsToolWindow toolWindow;

    RemoveDiffAction(MetricsToolWindow toolWindow) {
        super(MetricsReloadedBundle.message("hide.comparison.action"),
                MetricsReloadedBundle.message("hide.comparison.description"), AllIcons.Actions.Reset);
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        toolWindow.removeDiffOverlay();
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        final Presentation presentation = event.getPresentation();
        final boolean hasDiffOverlay = toolWindow.hasDiffOverlay();
        presentation.setEnabled(hasDiffOverlay);
    }
}
