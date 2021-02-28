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
package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.ui.UIBundle;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import org.jetbrains.annotations.NotNull;

class ToggleAutoscrollAction extends DumbAwareToggleAction {

    ToggleAutoscrollAction() {
        super(UIBundle.message("autoscroll.to.source.action.name"),
              UIBundle.message("autoscroll.to.source.action.description"), AllIcons.General.AutoscrollToSource);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent event) {
        return MetricsReloadedConfig.getInstance().isAutoscroll();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent event, boolean b) {
        MetricsReloadedConfig.getInstance().setAutoscroll(b);
    }
}
