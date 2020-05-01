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

package com.sixrr.metrics.metricModel;

import com.intellij.analysis.AnalysisScope;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.profile.MetricRepository;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.MethodUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.*;

public class MetricsRunImpl implements MetricsRun {

    private static final Logger LOG = Logger.getInstance(MetricsRunImpl.class);

    private final Map<MetricCategory, MetricsResult> metricResults = new EnumMap<>(MetricCategory.class);
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
        final Set<Metric> allMetrics = new HashSet<>();
        final Collection<MetricsResult> results = metricResults.values();
        for (MetricsResult result : results) {
            final Metric[] metrics = result.getMetrics();
            allMetrics.addAll(Arrays.asList(metrics));
        }
        return new ArrayList<>(allMetrics);
    }

    @NotNull
    private static String getFileTypeString(FileType fileType) {
        final String description = fileType.getDescription();
        return StringUtil.trimEnd(StringUtil.trimEnd(StringUtil.trimEnd(StringUtil.trimEnd(description,
                " (syntax highlighting only)"), " files"), " Files"), " source");
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
        final String qualifiedName = aPackage.getQualifiedName();
        results.postValue(metric, qualifiedName, value);
        results.setElementForMeasuredObject(qualifiedName, aPackage);
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
        final String qualifiedName = aPackage.getQualifiedName();
        results.postValue(metric, qualifiedName, numerator, denominator);
        results.setElementForMeasuredObject(qualifiedName, aPackage);
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
        final String qualifiedName = aClass.getQualifiedName();
        results.postValue(metric, qualifiedName, numerator, denominator);
        results.setElementForMeasuredObject(qualifiedName, aClass);
    }

    @Override
    public void postInterfaceMetric(@NotNull Metric metric, @NotNull PsiClass anInterface,
                                    double numerator, double denominator) {
        final MetricsResult results = getResultsForCategory(MetricCategory.Interface);
        final String qualifiedName = anInterface.getQualifiedName();
        results.postValue(metric, qualifiedName, numerator, denominator);
        results.setElementForMeasuredObject(qualifiedName, anInterface);
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
            @NonNls
            final XMLStreamWriter writer =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(new FileOutputStream(fileName), "UTF-8");
            try {
                writer.writeStartDocument();
                writer.writeCharacters("\n");
                writer.writeStartElement("SNAPSHOT");
                writer.writeAttribute("profile", profileName);
                writer.writeAttribute("timestamp", timestamp.toString());
                final String version = PluginManager.getPlugin(PluginId.getId("MetricsReloaded")).getVersion();
                writer.writeAttribute("version", version);
                writer.writeCharacters("\n");
                final MetricCategory[] categories = MetricCategory.values();
                for (MetricCategory category : categories) {
                    writeResultsForCategory(category, writer);
                }
                writer.writeEndElement();
                writer.writeEndDocument();
            } finally {
                try {
                    writer.close();
                } catch (XMLStreamException e) {
                    LOG.warn(e);
                }
            }
        } catch (IOException | XMLStreamException e) {
            LOG.warn(e);
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

    private void writeResultsForCategory(MetricCategory category, XMLStreamWriter writer) throws XMLStreamException {
        final MetricsResult results = getResultsForCategory(category);
        final Metric[] metrics = results.getMetrics();
        for (final Metric metric : metrics) {
            writeResultsForMetric(metric, results, writer);
        }
    }

    private static void writeResultsForMetric(Metric metric, MetricsResult results, @NonNls XMLStreamWriter writer)
            throws XMLStreamException {
        final Class<?> metricClass = metric.getClass();
        final String[] measuredObjects = results.getMeasuredObjects();
        writer.writeCharacters("  ");
        writer.writeStartElement("METRIC");
        writer.writeAttribute("class_name", metricClass.getName());
        writer.writeCharacters("\n");
        for (final String measuredObject : measuredObjects) {
            writeValue(results, metric, measuredObject, writer);
        }
        writer.writeCharacters("  ");
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }

    private static void writeValue(MetricsResult results, Metric metric, String measuredObject, @NonNls XMLStreamWriter writer)
            throws XMLStreamException {
        final Double value = results.getValueForMetric(metric, measuredObject);
        if (value != null) {
            writer.writeCharacters("    ");
            writer.writeEmptyElement("VALUE");
            writer.writeAttribute("measured", measuredObject);
            writer.writeAttribute("value", value.toString());
            writer.writeCharacters("\n");
        }
    }

    public static MetricsRun readFromFile(@NotNull File file) {
        final SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(file);
        } catch (Exception e) {
            try {
                doc = builder.build(fixBrokenXml(file));
            } catch (Exception e1) {
                LOG.warn(e);
                return null;
            }
        }
        final Element snapshotElement = doc.getRootElement();
        final MetricsRunImpl run = new MetricsRunImpl();
        run.setTimestamp(new TimeStamp(snapshotElement.getAttributeValue("timestamp")));
        run.setProfileName(snapshotElement.getAttributeValue("profile"));
        final String version = snapshotElement.getAttributeValue("version"); // may need this later
        final List<Element> metrics = snapshotElement.getChildren("METRIC");
        final MetricRepository repository = MetricsProfileRepository.getInstance();
        for (final Element metricElement : metrics) {
            readMetricElement(metricElement, repository, run);
        }
        return run;
    }

    @NotNull
    private static Reader fixBrokenXml(@NotNull File file) throws IOException {
        final String s = FileUtilRt.loadFile(file);
        final StringBuilder sb = new StringBuilder();
        boolean insideQuotes = false;
        for (int i = 0, length = s.length(); i < length; i++) {
            final int c = s.codePointAt(i);
            if (c == '"') {
                insideQuotes = !insideQuotes;
                sb.appendCodePoint(c);
            }
            else if (!insideQuotes) {
                sb.appendCodePoint(c);
            }
            else if (c == '<') {
                sb.append("&lt;");
            } else {
                sb.appendCodePoint(c);
            }
        }
        return new StringReader(sb.toString());
    }

    private static void readMetricElement(Element metricElement, MetricRepository repository, MetricsRunImpl run) {
        try {
            final String className = metricElement.getAttributeValue("class_name");
            final Metric metric = repository.getMetric(className);
            if (metric != null) {
                final List<Element> values = metricElement.getChildren("VALUE");
                for (final Element valueElement : values) {
                    final String measured = valueElement.getAttributeValue("measured");
                    final String valueString = valueElement.getAttributeValue("value");
                    final double value = Double.parseDouble(valueString);
                    run.postRawMetric(metric, measured, value);
                }
            }
        } catch (Exception e) {
            LOG.warn(e);
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
