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

package com.sixrr.metrics.offline;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationStarter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.sixrr.metrics.export.XMLExporter;
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.metricModel.TimeStamp;
import com.sixrr.metrics.plugin.MetricsPlugin;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MetricsCommandLine implements ApplicationStarter {
    
    private static final Logger logger = Logger.getInstance("MetricsReloaded");

    @Override
    public String getCommandName() {
        return "metrics";
    }

    @Override
    public void premain(String[] args) {
        if (args.length != 4) {
            usage(args);
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private static void usage(String[] args) {
        System.err.println("MetricsReloaded command line error " + Arrays.toString(args));
        System.err
                .println("Expected parameters : " + "<project_filename> <metrics_profile_name> <output_xml_filename>");
        System.exit(1);
    }

    /**
     * <pre>
     * args[0] : metrics
     * args[1] : project_file (.ipr)
     * args[2] : metrics_profile_name
     * args[2] : output_file  (.xml)
     * </pre>
     *
     * @param args
     */
    public void main(String[] args) {
        int exitCode = 0;

        try {
            final String projectFileName = args[1];
            final String metricsProfileName = args[2];

            final String outputXMLFileName = args[3];

            logger.info("MetricsReloaded command line");
            logger.info("  project file         : " + projectFileName);
            logger.info("  metrics profile      : " + metricsProfileName);
            logger.info("  output XML file name : " + outputXMLFileName);

            final Project project = ProjectManager.getInstance().loadAndOpenProject(projectFileName);
            final MetricsProfile profile = getMetricsProfile(project, metricsProfileName);
            final AnalysisScope scope = new AnalysisScope(project);
            final MetricsRunImpl metricsRun = new MetricsRunImpl();
            new MetricsExecutionContextImpl(project, scope) {
                @Override
                public void onFinish() {
                    metricsRun.setProfileName(profile.getName());
                    metricsRun.setTimestamp(new TimeStamp());
                    metricsRun.setContext(scope);
                    final XMLExporter exporter = new XMLExporter(metricsRun);
                    try {
                        exporter.export(outputXMLFileName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.execute(profile, metricsRun);
        } catch (Exception ex) {
            logger.info("Unexpected exception", ex);
            // logger.error() activate the IDEA fatal error dialog stuff (?)

            ex.printStackTrace(System.err);

            exitCode = 1;
        }

        logger.info("EXIT CODE :: " + exitCode);
        System.exit(exitCode);
    }

    private static MetricsProfile getMetricsProfile(Project project, String profileName) {
        final MetricsPlugin plugin = project.getComponent(MetricsPlugin.class);
        final MetricsProfileRepository repository = plugin.getProfileRepository();

        final List<String> metricsProfileNames = Arrays.asList(repository.getProfileNames());
        if (!metricsProfileNames.contains(profileName)) {
            throw new RuntimeException("The metrics profile [" + profileName + "] does not exist!");
        }

        repository.setSelectedProfile(profileName);

        return repository.getCurrentProfile();
    }
}

