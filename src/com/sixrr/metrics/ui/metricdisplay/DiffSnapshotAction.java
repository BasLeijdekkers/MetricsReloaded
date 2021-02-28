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
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

class DiffSnapshotAction extends DumbAwareAction {

    private final MetricsView toolWindow;
    private final Project project;

    DiffSnapshotAction(MetricsView toolWindow, Project project) {
        super(MetricsReloadedBundle.messagePointer("compare.with.snapshot.action"),
              MetricsReloadedBundle.messagePointer("compare.with.snapshot.description"), AllIcons.Actions.Diff);
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        final FileFilter filter = new SnapshotFileFilter();
        chooser.setFileFilter(filter);
        final WindowManager myWindowManager = WindowManager.getInstance();
        final Window parent = myWindowManager.suggestParentWindow(project);
        final int returnVal = chooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = chooser.getSelectedFile();
            final MetricsRun previousResults = MetricsRunImpl.readFromFile(selectedFile);
            if (previousResults != null) {
                toolWindow.reloadAsDiff(previousResults);
            }
        }
    }
}
