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

package com.sixrr.metrics.profile;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricInstanceImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class MetricsProfileImpl implements MetricsProfile {

    private String name;
    private final Map<String, MetricInstance> id2instance = new HashMap<String, MetricInstance>();
    private MetricDisplaySpecification displaySpecification = new MetricDisplaySpecification();
    private boolean builtIn = false;

    public MetricsProfileImpl(String name, List<MetricInstance> metrics) {
        this.name = name;
        for (MetricInstance metricInstance : metrics) {
            id2instance.put(metricInstance.getMetric().getID(), metricInstance);
        }
    }

    @Override
    public void addMetricInstance(@NotNull MetricInstance metricInstance) {
        id2instance.put(metricInstance.getMetric().getID(), metricInstance);
    }

    @Override
    public MetricDisplaySpecification getDisplaySpecification() {
        return displaySpecification;
    }

    @Override
    public void copyFrom(List<MetricInstance> metricInstances) {
        for (MetricInstance newMetricInstance : metricInstances) {
            final MetricInstance metricInstance = getMetricInstance(newMetricInstance.getMetric());
            if (metricInstance != null) {
                metricInstance.copyFrom(newMetricInstance);
            }
        }
    }

    @Override
    public boolean isBuiltIn() {
        return builtIn;
    }

    @Override
    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    @Override
    public void setName(String newProfileName) {
        name = newProfileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<MetricInstance> getMetricInstances() {
        final ArrayList<MetricInstance> result = new ArrayList<MetricInstance>(id2instance.values());
        Collections.sort(result);
        return result;
    }

    @Override
    public MetricsProfileImpl clone() throws CloneNotSupportedException {
        final MetricsProfileImpl out = (MetricsProfileImpl) super.clone();
        out.id2instance.clear();
        for (Map.Entry<String, MetricInstance> entry : id2instance.entrySet()) {
            out.id2instance.put(entry.getKey(), entry.getValue().clone());
        }
        out.displaySpecification = new MetricDisplaySpecification();
        return out;
    }

    @Override
    @Nullable
    public MetricInstance getMetricInstance(Metric metric) {
        return getMetricInstance(metric.getID());
    }

    @Override
    @Nullable
    public MetricInstance getMetricInstance(String metricID) {
        return id2instance.get(metricID);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Nullable
    public static MetricsProfile loadFromFile(File file, MetricRepository metrics) {
        final Document doc;
        try {
            final SAXBuilder builder = new SAXBuilder();
            doc = builder.build(file);
        } catch (Exception e) {
            return null;
        }
        final Element profileRoot = doc.getRootElement();
        final String profileName = profileRoot.getAttributeValue("name");
        final List<Element> children = profileRoot.getChildren("METRIC");
        final List<MetricInstance> profileMetrics = new ArrayList<MetricInstance>(200);
        for (final Element metricElement : children) {
            final MetricInstance metric = parseMetric(metricElement, metrics);
            if (metric != null) {
                profileMetrics.add(metric);
            }
        }
        final MetricsProfileImpl profile = new MetricsProfileImpl(profileName, profileMetrics);
        final Element displaySpecElement = profileRoot.getChild("DISPLAY_SPEC");
        if (displaySpecElement != null) {
            parseDisplaySpec(displaySpecElement, profile.getDisplaySpecification());
        }
        return profile;
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    private static void parseDisplaySpec(Element displaySpecElement, MetricDisplaySpecification spec) {
        for (MetricCategory category : MetricCategory.values()) {
            final String tag = category.name();
            final Element element = displaySpecElement.getChild(tag.toUpperCase());
            if (element == null) {
                continue;
            }
            final MetricTableSpecification specification = spec.getSpecification(category);
            specification.setAscending("true".equals(element.getAttributeValue("ascending")));
            specification.setSortColumn(Integer.parseInt(element.getAttributeValue("sort_column")));
            specification.setColumnOrder(parseStringList(element.getAttributeValue("column_order")));
            specification.setColumnWidths(parseIntList(element.getAttributeValue("column_widths")));
        }
    }

    private static List<Integer> parseIntList(String value) {
        if (value != null && !value.isEmpty()) {
            final StringTokenizer tokenizer = new StringTokenizer(value, "|");
            final List<Integer> out = new ArrayList<Integer>(32);
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                final Integer intValue = new Integer(token);
                out.add(intValue);
            }
            return out;
        } else {
            return Collections.emptyList();
        }
    }

    private static List<String> parseStringList(String value) {
        if (value != null && !value.isEmpty()) {
            final StringTokenizer tokenizer = new StringTokenizer(value, "|");
            final List<String> out = new ArrayList<String>(32);
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                out.add(token);
            }
            return out;
        } else {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    @Nullable
    private static MetricInstance parseMetric(Element metricElement, MetricRepository metrics) {
        final String className = metricElement.getAttributeValue("className");

        final String lowerThresholdEnabledString =
                metricElement.getAttributeValue("lowerThresholdEnabled");
        boolean lowerThresholdEnabled = false;
        if (lowerThresholdEnabledString != null) {
            lowerThresholdEnabled = "true".equals(lowerThresholdEnabledString);
        }
        final String lowerThresholdString = metricElement.getAttributeValue("lowerThreshold");
        double lowerThreshold = 0.0;
        if (lowerThresholdString != null) {
            lowerThreshold = Double.parseDouble(lowerThresholdString);
        }
        final String upperThresholdEnabledString =
                metricElement.getAttributeValue("upperThresholdEnabled");

        boolean upperThresholdEnabled = false;
        if (upperThresholdEnabledString != null) {
            upperThresholdEnabled = "true".equals(upperThresholdEnabledString);
        }
        final String upperThresholdString = metricElement.getAttributeValue("upperThreshold");
        double upperThreshold = 0.0;
        if (upperThresholdString != null) {
            upperThreshold = Double.parseDouble(upperThresholdString);
        }
        boolean enabled = false;
        final String enabledString = metricElement.getAttributeValue("enabled");
        if (enabledString != null) {
            enabled = "true".equals(enabledString);
        }
        final Metric metric = metrics.getMetric(className);
        if (metric == null) {
            return null;
        }
        final MetricInstance metricInstance = new MetricInstanceImpl(metric);
        metricInstance.setEnabled(enabled);
        metricInstance.setLowerThreshold(lowerThreshold);
        metricInstance.setLowerThresholdEnabled(lowerThresholdEnabled);
        metricInstance.setUpperThreshold(upperThreshold);
        metricInstance.setUpperThresholdEnabled(upperThresholdEnabled);
        return metricInstance;
    }

    @Override
    @SuppressWarnings({"HardCodedStringLiteral"})
    public void writeToFile(File profileFile) throws FileNotFoundException {
        final PrintWriter writer = new PrintWriter(new FileOutputStream(profileFile));
        try {
            writer.println("<METRICS_PROFILE name = \"" + name + "\">");
            writeDisplaySpec(writer, displaySpecification);
            for (final MetricInstance metric : getMetricInstances()) {
                writeMetric(metric, writer);
            }
            writer.println("</METRICS_PROFILE>");
        } finally {
            writer.close();
        }
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    private static void writeDisplaySpec(PrintWriter writer,
                                         MetricDisplaySpecification displaySpecification) {
        writer.println("\t<DISPLAY_SPEC>");

        final MetricCategory[] categories = MetricCategory.values();
        for (MetricCategory category : categories) {
            printTableSpec(displaySpecification, category, writer);
        }
        writer.println("\t</DISPLAY_SPEC>");
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    private static void printTableSpec(MetricDisplaySpecification displaySpecification,
                                       MetricCategory category,
                                       PrintWriter writer) {
        final MetricTableSpecification projectSpec = displaySpecification.getSpecification(category);
        final List<String> columnOrder = projectSpec.getColumnOrder();
        final List<Integer> columnWidths = projectSpec.getColumnWidths();
        final String tag = category.name().toUpperCase();
        writer.println("\t\t<" + tag + " ascending = \"" + projectSpec.isAscending() + "\" sort_column = \"" +
                projectSpec.getSortColumn() + "\" " + "column_order = \"" + writeListAsString(columnOrder) +
                "\" column_widths = \"" + writeListAsString(columnWidths) + "\" />");
    }

    private static String writeListAsString(List<?> list) {
        final StringBuffer out = new StringBuffer();

        for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
            final Object element = iterator.next();
            out.append(element);
            if (iterator.hasNext()) {
                out.append('|');
            }
        }
        return out.toString();
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    private static void writeMetric(MetricInstance metric, PrintWriter writer) {
        final Class<? extends Metric> metricClass = metric.getMetric().getClass();
        writer.println("\t<METRIC className = \"" + metricClass.getName() + "\" enabled = \"" +
                metric.isEnabled() + "\" lowerThreshold = \"" + metric.getLowerThreshold() + "\" " +
                "lowerThresholdEnabled = \"" + metric.isLowerThresholdEnabled() + "\" upperThreshold = \"" +
                metric.getUpperThreshold() + "\" " + "upperThresholdEnabled = \"" + metric.isUpperThresholdEnabled() +
                "\" />");
    }

    @Override
    public String toString() {
        return name + (builtIn ? " (built-in)" : "");
    }
}
