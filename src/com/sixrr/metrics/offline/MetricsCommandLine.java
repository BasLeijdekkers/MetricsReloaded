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

package com.sixrr.metrics.offline;

import com.intellij.analysis.AnalysisScope;
import com.intellij.ide.impl.PatchProjectUtil;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ApplicationStarter;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.search.scope.packageSet.NamedScope;
import com.intellij.psi.search.scope.packageSet.NamedScopesHolder;
import com.sixrr.metrics.export.Exporter;
import com.sixrr.metrics.export.XMLExporter;
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.metricModel.TimeStamp;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import org.jetbrains.annotations.Contract;
import org.kohsuke.args4j.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class MetricsCommandLine implements ApplicationStarter {

    private static final Logger LOG = Logger.getInstance(MetricsCommandLine.class);

    @Argument(index = 0, required = true, metaVar = "<project_path>", usage = "the project to calculate metrics for")
    private String projectPath = null;

    @Argument(index = 1, required = true, metaVar = "<metrics_profile_name>",
            usage = "name of the metrics profile to use")
    private String metricsProfileName = "";

    @Argument(index = 2, metaVar = "<output_path>",
            usage = "the path to write the output xml to, default writes to STDOUT")
    private String outputXmlPath = null;

    @Option(name = "-d", aliases = "--directory", metaVar = "<path>", forbids = "-s",
            usage = "directory to calculate metrics for, default is the whole project")
    private String directory = null;

    @Option(name = "-s", aliases = "--scope", metaVar = "<scope_name>", forbids = "-d",
            usage = "name of scope to calculate metrics for, default is the whole project")
    private String scope = null;

    @Option(name = "-v", aliases = "--verbose", usage = "show more progress information", forbids = "-q")
    private boolean verbose = false;

    @Option(name = "-q", aliases = "--quiet", usage = "show less information", forbids = "-v")
    private boolean quiet = false;

    @Option(name = "-h", aliases = "--help", usage = "show this message", help = true)
    private boolean help = false;

    @Override
    public String getCommandName() {
        return "metrics";
    }

    private static void printUsage(CmdLineParser parser, PrintStream out) {
        final String scriptName = ApplicationNamesInfo.getInstance().getScriptName();
        out.println("Usage: " + scriptName +
                " metrics [options] <project_path> <metrics_profile_name> [<output_xml_file>]");
        parser.printUsage(out);
    }

    @Override
    public void premain(String[] args) {
        final ParserProperties properties = ParserProperties.defaults()
                .withShowDefaults(false)
                .withOptionSorter(null);
        final CmdLineParser parser = new CmdLineParser(this, properties);
        try {
            parser.parseArgument(Arrays.copyOfRange(args, 1, args.length));
            if (help) {
                printUsage(parser, System.out);
                System.exit(0);
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            printUsage(parser, System.err);
            System.exit(1);
        }
    }

    @Override
    public void main(String[] args) {
        if (outputXmlPath != null) {
            final File file = new File(outputXmlPath);
            final File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                error("Could not find directory " + parentFile.getAbsolutePath());
            }
        }
        final ApplicationEx application = (ApplicationEx) ApplicationManager.getApplication();
        try {
            final ApplicationInfoEx applicationInfo = (ApplicationInfoEx) ApplicationInfo.getInstance();
            info("MetricsReloaded running on " + applicationInfo.getFullApplicationName());
            application.setSaveAllowed(false);
            try {
                info("Opening project...");
                if (projectPath == null) {
                    projectPath = new File("").getAbsolutePath();
                }
                projectPath = projectPath.replace(File.separatorChar, '/');
                final Project project = ProjectUtil.openOrImport(projectPath, null, false);
                if (project == null) {
                    error("Unable to open project: " + projectPath);
                }

                application.runWriteAction(() -> VirtualFileManager.getInstance().refreshWithoutFileWatcher(false));
                PatchProjectUtil.patchProject(project);
                info("Project " + project.getName() + " opened.");

                final MetricsProfile profile = getMetricsProfile(metricsProfileName);
                if (profile == null) {
                    error("Profile not found: " + metricsProfileName);
                }
                info("Calculating metrics");
                final AnalysisScope analysisScope;
                if (scope != null) {
                    final NamedScope namedScope = NamedScopesHolder.getScope(project, scope);
                    if (namedScope == null) {
                        error("Scope not found: " + scope);
                    }
                    analysisScope = new AnalysisScope(GlobalSearchScopesCore.filterScope(project, namedScope), project);
                } else if (directory != null) {
                    directory = directory.replace(File.separatorChar, '/');

                    final VirtualFile vfsDir = LocalFileSystem.getInstance().findFileByPath(directory);
                    if (vfsDir == null) {
                        error("Directory not found: " + directory);
                    }
                    final PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(vfsDir);
                    if (psiDirectory == null) {
                        error("Directory not found: " + directory);
                    }
                    analysisScope = new AnalysisScope(psiDirectory);
                } else {
                    analysisScope = new AnalysisScope(project);
                }
                ProgressManager.getInstance().runProcess(() -> {
                    final MetricsRunImpl metricsRun = new MetricsRunImpl();
                    metricsRun.setProfileName(profile.getName());
                    metricsRun.setTimestamp(new TimeStamp());
                    metricsRun.setContext(analysisScope);
                    final MetricsExecutionContextImpl metricsExecutionContext =
                            new MetricsExecutionContextImpl(project, analysisScope);
                    metricsExecutionContext.calculateMetrics(profile, metricsRun);
                    final Exporter exporter = new XMLExporter(metricsRun);
                    try {
                        if (outputXmlPath == null) {
                            final PrintWriter writer = new PrintWriter(System.out, true);
                            exporter.export(writer);
                        } else {
                            exporter.export(outputXmlPath);
                        }
                    } catch (IOException e) {
                        error(e.getMessage());
                    }
                }, new ProgressIndicatorBase() {
                    private int lastPercent = 0;

                    @Override
                    public void setFraction(double fraction) {
                        final int percent = (int)(fraction * 100);
                        if (lastPercent != percent && !isIndeterminate()) {
                            lastPercent = percent;
                            trace("Calculating metrics " + lastPercent + "%");
                        }
                    }
                });
                info("Finished.");
            } catch (Exception ex) {
                error(ex);
            }
            application.exit(true, true);
        } catch (Exception e) {
            LOG.error(e);
            error(e);
        }
    }

    private static MetricsProfile getMetricsProfile(String profileName) {
        final MetricsProfileRepository repository = MetricsProfileRepository.getInstance();
        final List<String> metricsProfileNames = Arrays.asList(repository.getProfileNames());
        if (!metricsProfileNames.contains(profileName)) {
            return null;
        }
        repository.setSelectedProfile(profileName);
        return repository.getCurrentProfile();
    }

    @Contract("_ -> fail")
    private static void error(Throwable throwable) {
        System.err.println(throwable.getMessage());
        LOG.error(throwable);
        System.exit(1);
    }

    @Contract("_ -> fail")
    private static void error(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private void info(String message) {
        if (quiet) {
            return;
        }
        System.out.println(message);
    }

    private void trace(String message) {
        if (!verbose) {
            return;
        }
        System.out.println(message);
    }
}

