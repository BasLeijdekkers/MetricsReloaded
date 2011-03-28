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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.WindowManager;
import com.sixrr.metrics.export.CSVExporter;
import com.sixrr.metrics.export.Exporter;
import com.sixrr.metrics.export.XMLExporter;
import com.sixrr.metrics.metricModel.MetricsRun;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class ExportAction extends AnAction {

    public static final Logger LOGGER = Logger.getInstance("MetricsReloaded");
    private static final Icon ICON = IconLoader.getIcon("/actions/export.png");

    private final MetricsToolWindow toolWindow;
    private final Project project;

    ExportAction(MetricsToolWindow toolWindow, Project project) {
        super(MetricsReloadedBundle.message("export.action"),
                MetricsReloadedBundle.message("export.description"),
                ICON);
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final MetricsRun currentResults = toolWindow.getCurrentRun();
        final JFileChooser chooser = new JFileChooser();
        final FileTypeFilter xmlFilter =
                new FileTypeFilter(".xml", MetricsReloadedBundle.message("xml.files"));
//        final FileTypeFilter htmlFilter = new FileTypeFilter(".html", "HTML Files");
        final FileTypeFilter csvFilter =
                new FileTypeFilter(".csv", MetricsReloadedBundle.message("csv.files"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(csvFilter);
//        chooser.addChoosableFileFilter(htmlFilter);
        chooser.addChoosableFileFilter(xmlFilter);
        final WindowManager myWindowManager;
        final Application application = ApplicationManager.getApplication();
        if (application != null && application.hasComponent(WindowManager.class)) {
            myWindowManager = WindowManager.getInstance();
        } else {
            return;
        }
        final Window parent = myWindowManager.suggestParentWindow(project);
        final int returnVal = chooser.showSaveDialog(parent);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final File selectedFile = chooser.getSelectedFile();
        final FileTypeFilter filter = (FileTypeFilter) chooser.getFileFilter();
        String fileName = selectedFile.getAbsolutePath();
        final Exporter exporter;
        if (filter.equals(csvFilter)) {
            exporter = new CSVExporter(currentResults);
        }
//        else if (filter.equals(htmlFilter))
//        {
//            exporter = new HTMLExporter(currentResults);
//        }
        else {
            exporter = new XMLExporter(currentResults);
        }
        final String extension = filter.getExtension();
        if (!fileName.endsWith(extension)) {
            fileName += extension;
        }

        try {
            exporter.export(fileName);
        }
        catch (IOException ex) {
            LOGGER.info("Metrics export to file failed", ex);
        }
    }
}
