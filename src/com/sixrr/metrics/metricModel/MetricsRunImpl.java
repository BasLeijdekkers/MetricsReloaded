/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.metricModel;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.utils.MethodUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MetricsRunImpl implements MetricsRun {

    private static final Logger logger = Logger.getInstance("MetricsReloaded");
    private final Map<MetricCategory, MetricsResult> metricResults =
            new EnumMap<MetricCategory, MetricsResult>(MetricCategory.class);
    private String profileName = null;
    private AnalysisScope context = null;
    private TimeStamp timestamp = null;

    public MetricsRunImpl() {
        final MetricCategory[] categories = MetricCategory.values();
        for (MetricCategory category : categories) {
            metricResults.put(category, new MetricsResultImpl());
        }
    }

    @Override
    public List<Metric> getMetrics() {
        final Set<Metric> allMetrics = new HashSet<Metric>();
        final Collection<MetricsResult> results = metricResults.values();
        for (MetricsResult result : results) {
            final Metric[] metrics = result.getMetrics();
            allMetrics.addAll(Arrays.asList(metrics));
        }
        return new ArrayList<Metric>(allMetrics);
    }

    @NotNull
    private static String getFileTypeString(FileType fileType) {
        final String description = fileType.getDescription();
        return StringUtil.trimEnd(StringUtil.trimEnd(StringUtil.trimEnd(description,
                " (syntax highlighting only)"), " files"), " source");
    }

    @Override
    public void postProjectMetric(@NotNull Metric metric, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Project);
        results.postValue(metric, "project", value);
    }

    @Override
    public void postFileTypeMetric(Metric metric, FileType fileType, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.FileType);
        results.postValue(metric, getFileTypeString(fileType), value);
    }

    @Override
    public void postModuleMetric(@NotNull Metric metric, @NotNull Module module, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Module);
        results.postValue(metric, module.getName(), value);
    }

    @Override
    public void postPackageMetric(@NotNull Metric metric, @NotNull PsiPackage aPackage, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Package);
        results.postValue(metric, aPackage.getQualifiedName(), value);
    }

    @Override
    public void postClassMetric(@NotNull Metric metric, @NotNull PsiClass aClass, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Class);
        final String qualifiedName = aClass.getQualifiedName();
        results.postValue(metric, qualifiedName, value);
        results.setElementForMeasuredObject(qualifiedName, aClass);
    }

    @Override
    public void postInterfaceMetric(@NotNull Metric metric, @NotNull PsiClass anInterface, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Interface);
        final String qualifiedName = anInterface.getQualifiedName();
        results.postValue(metric, qualifiedName, value);
        results.setElementForMeasuredObject(qualifiedName, anInterface);
    }

    @Override
    public void postMethodMetric(@NotNull Metric metric, @NotNull PsiMethod method, double value) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Method);
        final String signature = MethodUtils.calculateSignature(method);
        results.postValue(metric, signature, value);
        results.setElementForMeasuredObject(signature, method);
    }

    @Override
    public void postProjectMetric(@NotNull Metric metric, double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Project);
        results.postValue(metric, "project", numerator, denominator);
    }

    @Override
    public void postModuleMetric(@NotNull Metric metric, @NotNull Module module, double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Module);
        results.postValue(metric, module.getName(), numerator, denominator);
    }

    @Override
    public void postPackageMetric(@NotNull Metric metric, @NotNull PsiPackage aPackage,
                                  double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Package);
        results.postValue(metric, aPackage.getQualifiedName(), numerator, denominator);
    }

    @Override
    public void postFileTypeMetric(@NotNull Metric metric, @NotNull FileType fileType,
                                   double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.FileType);
        results.postValue(metric, getFileTypeString(fileType), numerator, denominator);
    }

    @Override
    public void postClassMetric(@NotNull Metric metric, @NotNull PsiClass aClass,
                                double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Class);
        results.postValue(metric, aClass.getQualifiedName(), numerator, denominator);
    }

    @Override
    public void postInterfaceMetric(@NotNull Metric metric, @NotNull PsiClass anInterface,
                                    double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Interface);
        results.postValue(metric, anInterface.getQualifiedName(), numerator, denominator);
    }

    @Override
    public void postMethodMetric(@NotNull Metric metric, @NotNull PsiMethod method,
                                 double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Method);
        final String signature = MethodUtils.calculateSignature(method);
        results.postValue(metric, signature, numerator, denominator);
        results.setElementForMeasuredObject(signature, method);
    }

    private void postRawMetric(@NotNull Metric metric, @NotNull String measured, double value) {
        final MetricCategory category = metric.getCategory();
        final MetricsResult result = metricResults.get(category);
        result.postValue(metric, measured, value);
    }

    @Override
    public MetricsResult getResultsForCategory(@NotNull MetricCategory category) {
        return metricResults.get(category);
    }

    private void setResultsForCategory(@NotNull MetricCategory category, @NotNull MetricsResult results) {
        metricResults.put(category, results);
    }

    @Override
    public void writeToFile(@NotNull String fileName) {
        try {
            @NonNls final PrintWriter writer = new PrintWriter(new FileOutputStream(fileName));
            try {
                writer.println("<SNAPSHOT" + " profile = \"" + profileName + '\"' +
                        " timestamp = \"" + timestamp + '\"' + '>');
                final MetricCategory[] categories = MetricCategory.values();
                for (MetricCategory category : categories) {
                    writeResultsForCategory(category, writer);
                }
                writer.println("</SNAPSHOT>");
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    @Override
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setContext(AnalysisScope context) {
        this.context = context;
    }

    public void setTimestamp(TimeStamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public TimeStamp getTimestamp() {
        return timestamp;
    }

    @Override
    public AnalysisScope getContext() {
        return context;
    }

    @Override
    public MetricsRun filterRowsWithoutWarnings(@NotNull MetricsProfile profile) {
        final MetricsRunImpl out = new MetricsRunImpl();
        out.context = context;
        out.profileName = profileName;
        out.timestamp = timestamp;

        final Set<MetricCategory> categories = metricResults.keySet();
        for (MetricCategory category : categories) {
            final MetricsResult results = getResultsForCategory(category);
            final MetricsResult filteredResults = results.filterRowsWithoutWarnings(profile);
            out.setResultsForCategory(category, filteredResults);
        }

        return out;
    }

    private void writeResultsForCategory(MetricCategory category, PrintWriter writer) {
        final MetricsResult results = getResultsForCategory(category);
        final Metric[] metrics = results.getMetrics();
        for (final Metric metric : metrics) {
            writeResultsForMetric(metric, results, writer);
        }
    }

    private static void writeResultsForMetric(Metric metric, MetricsResult results, @NonNls PrintWriter writer) {
        final Class<?> metricClass = metric.getClass();
        final String[] measuredObjects = results.getMeasuredObjects();
        writer.println("\t\t<METRIC class_name= \"" + metricClass.getName() + "\">");
        for (final String measuredObject : measuredObjects) {
            writeValue(results, metric, measuredObject, writer);
        }
        writer.println("\t\t</METRIC>");
    }

    private static void writeValue(MetricsResult results, Metric metric, String measuredObject,
                                   @NonNls PrintWriter writer) {
        final Double value = results.getValueForMetric(metric, measuredObject);
        if (value != null) {
            writer.println("\t\t\t<VALUE measured = \"" + measuredObject + "\" value = \"" + value + "\"/>");
        }
    }

    public static MetricsRun readFromFile(@NotNull File file) {
        final SAXBuilder builder = new SAXBuilder();
        final Document doc;
        try {
            doc = builder.build(file);
        } catch (Exception e) {
            return null;
        }
        @NonNls final Element snapshotElement = doc.getRootElement();
        final MetricsRunImpl run = new MetricsRunImpl();
        run.setTimestamp(new TimeStamp(snapshotElement.getAttributeValue("timestamp")));
        run.setProfileName(snapshotElement.getAttributeValue("profile"));
        final List<Element> metrics = snapshotElement.getChildren("METRIC");
        for (final Element metricElement : metrics) {
            readMetricElement(metricElement, run);
        }
        return run;
    }

    private static void readMetricElement(@NonNls Element metricElement, MetricsRunImpl run) {
        try {
            final String className = metricElement.getAttributeValue("class_name");
            final Class<?> metricClass = Class.forName(className);
            final Metric metric = (Metric) metricClass.newInstance();
            final List<Element> values = metricElement.getChildren("VALUE");
            for (final Element valueElement : values) {
                final String measured = valueElement.getAttributeValue("measured");
                final String valueString = valueElement.getAttributeValue("value");
                final double value = Double.parseDouble(valueString);
                run.postRawMetric(metric, measured, value);
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public boolean hasWarnings(@NotNull MetricsProfile profile) {
        for (MetricCategory category : MetricCategory.values()) {
            final MetricsResult result = metricResults.get(category);
            if (result.hasWarnings(profile)) {
                return true;
            }
        }
        return false;
    }
}
