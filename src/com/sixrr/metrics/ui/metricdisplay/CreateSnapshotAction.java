/*
 * Copyright 2005-2013 Bas Leijdekkers, Sixth and Red River Software
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

class CreateSnapshotAction extends AnAction {
    
    private final MetricsToolWindow toolWindow;
    private final Project project;

    CreateSnapshotAction(MetricsToolWindow toolWindow, Project project) {
        super(MetricsReloadedBundle.message("create.snapshot.action"),
                MetricsReloadedBundle.message("create.snapshot.description"), AllIcons.Actions.Dump);
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final MetricsRun currentResults = toolWindow.getCurrentRun();
        final JFileChooser chooser = new JFileChooser();
        final int returnVal = selectFile(chooser);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = chooser.getSelectedFile();
            @NonNls final String fileName = selectedFile.getAbsolutePath();
            if (fileName.endsWith(".met")) {
                currentResults.writeToFile(fileName);
            } else {
                currentResults.writeToFile(fileName + ".met");
            }
        }
    }

    private int selectFile(JFileChooser chooser) {
        final FileFilter filter = new SnapshotFileFilter();
        chooser.setFileFilter(filter);
        final WindowManager myWindowManager = WindowManager.getInstance();
        final Window parent = myWindowManager.suggestParentWindow(project);
        return chooser.showSaveDialog(parent);
    }
}
