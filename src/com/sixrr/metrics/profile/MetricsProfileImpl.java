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

package com.sixrr.metrics.profile;

import com.sixrr.metrics.Metric;
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
    private final Map<String, MetricInstance> id2instance = new HashMap<>();
    private boolean prebuilt = false;

    public MetricsProfileImpl(String name, Collection<MetricInstance> metrics) {
        this.name = name;
        for (MetricInstance metricInstance : metrics) {
            addMetricInstance(metricInstance);
        }
    }

    public MetricsProfileImpl(MetricsProfile copy) {
        this(copy.getName(), copy.getMetricInstances());
    }

    @Override
    public void addMetricInstance(@NotNull MetricInstance metricInstance) {
        id2instance.put(metricInstance.getMetric().getID(), metricInstance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MetricsProfileImpl that = (MetricsProfileImpl) o;
        return name.equals(that.name) && prebuilt == that.prebuilt;
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + (prebuilt ? 1 : 0);
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
    public boolean isPrebuilt() {
        return prebuilt;
    }

    @Override
    public void setPrebuilt(boolean prebuilt) {
        this.prebuilt = prebuilt;
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
        final List<MetricInstance> result = new ArrayList<>(id2instance.values());
        Collections.sort(result);
        return result;
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
        final List<MetricInstance> profileMetrics = new ArrayList<>(200);
        for (Element metricElement : children) {
            final MetricInstance metric = parseMetric(metricElement, metrics);
            if (metric != null) {
                profileMetrics.add(metric);
            }
        }
        return new MetricsProfileImpl(profileName, profileMetrics);
    }

    @SuppressWarnings("HardCodedStringLiteral")
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
    @SuppressWarnings("HardCodedStringLiteral")
    public void writeToFile(File profileFile) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(profileFile))) {
            writer.println("<METRICS_PROFILE name = \"" + name + "\">");
            for (MetricInstance metric : getMetricInstances()) {
                writeMetric(metric, writer);
            }
            writer.println("</METRICS_PROFILE>");
        }
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

    @SuppressWarnings("HardCodedStringLiteral")
    @Override
    public String toString() {
        return name + (prebuilt ? " (prebuilt)" : "");
    }
}
