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

package com.sixrr.metrics.test;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.metricModel.MetricsRunImpl;
import com.sixrr.metrics.metricModel.TimeStamp;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.profile.MetricInstanceImpl;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileImpl;
import org.junit.Assert;

import java.io.File;
import java.util.Collections;

/**
 * @author Bas Leijdekkers
 */
public abstract class MetricTestCase extends LightCodeInsightFixtureTestCase {
    @Override
    protected final String getTestDataPath() {
        final String path = new File(".").getAbsolutePath();
        final String packageName = getClass().getPackage().getName();
        final int i = packageName.lastIndexOf('.') + 1;
        return path + "/stockmetrics/testdata/" + packageName.substring(i) + '/' + getBasePath();
    }

    protected abstract Metric getMetric();

    protected void doTest(double... expected) {
        final Metric metric = getMetric();
        final MetricsResult metricsResult = calculateMetric(metric);
        Assert.assertArrayEquals(expected, metricsResult.getValuesForMetric(metric), 0.0);
    }

    protected MetricsResult calculateMetric(Metric metric) {
        final String testName = camelCaseToSnakeCase(getTestName(true));
        final File file = new File(getTestDataPath(), testName);
        final VirtualFile projectDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        assertNotNull(file.getAbsolutePath() + " not found", projectDir);

        final VirtualFile srcDir;
        if (projectDir.findChild("src") != null) {
            srcDir = myFixture.copyDirectoryToProject(testName + "/src", "");
        }
        else {
            srcDir = myFixture.copyDirectoryToProject(testName, "");
        }
        final Project project = getProject();
        final PsiDirectory directory = PsiManager.getInstance(project).findDirectory(srcDir);
        assertNotNull(directory);
        final AnalysisScope scope = new AnalysisScope(directory);

        final MetricInstance metricInstance = new MetricInstanceImpl(metric);
        metricInstance.setEnabled(true);
        final MetricsProfile profile = new MetricsProfileImpl("test", Collections.singletonList(metricInstance));
        return ProgressManager.getInstance().runProcess(() -> {
            final MetricsRunImpl metricsRun = new MetricsRunImpl();
            metricsRun.setProfileName(profile.getName());
            metricsRun.setTimestamp(new TimeStamp());
            metricsRun.setContext(scope);
            final MetricsExecutionContextImpl metricsExecutionContext = new MetricsExecutionContextImpl(project, scope);
            metricsExecutionContext.calculateMetrics(profile, metricsRun);
            return metricsRun.getResultsForCategory(metric.getCategory());
        }, new EmptyProgressIndicator());
    }

    private static String camelCaseToSnakeCase(CharSequence camelCase) {
        final StringBuilder builder = new StringBuilder();
        final int length = camelCase.length();
        for(int i = 0; i < length; ++i) {
            final char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append("_").append(Character.toLowerCase(ch));
            }
            else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
