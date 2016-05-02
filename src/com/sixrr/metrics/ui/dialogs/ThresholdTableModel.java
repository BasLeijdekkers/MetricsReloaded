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
package com.sixrr.metrics.ui.dialogs;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.utils.MetricsReloadedBundle;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ThresholdTableModel extends AbstractTableModel {

    private final List<MetricInstance> metrics;
    private final MetricsResult result;

    public ThresholdTableModel(List<MetricInstance> metrics, MetricsResult result) {
        this.metrics = metrics;
        this.result = result;
    }

    @Override
    public int getRowCount() {
        return metrics.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public boolean isCellEditable(int rowNum, int columnNum) {
        return columnNum > 3;
    }

    @Override
    public String getColumnName(int columnNum) {
        switch (columnNum) {
            case 0:
                return MetricsReloadedBundle.message("name");
            case 1:
                return MetricsReloadedBundle.message("abbreviation");
            case 2:
                return MetricsReloadedBundle.message("minimum");
            case 3:
                return MetricsReloadedBundle.message("maximum");
            case 4:
                return MetricsReloadedBundle.message("warn.if.less.than1");
            case 5:
                return MetricsReloadedBundle.message("warn.if.greater.than1");
            default:
                return null;
        }

    }

    @Override
    public Class<?> getColumnClass(int columnNum) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowNum, int columnNum) {
        final MetricInstance instance = metrics.get(rowNum);
        if (instance == null) {
            return null;
        }
        final Metric metric = instance.getMetric();
        switch (columnNum) {
            case 0:
                return metric.getDisplayName();
            case 1:
                return metric.getAbbreviation();
            case 2:
                return calculateMinimumForMetric(metric).toString();
            case 3:
                return calculateMaximumForMetric(metric).toString();
            case 4:
                return instance.isLowerThresholdEnabled() ? Double.toString(instance.getLowerThreshold()) : "";
            case 5:
                return instance.isUpperThresholdEnabled() ? Double.toString(instance.getUpperThreshold()) : "";
            default:
                return null;
        }
    }

    private Double calculateMinimumForMetric(Metric metric) {
        return  result.getMinimumForMetric(metric);
    }

    private Double calculateMaximumForMetric(Metric metric) {
        return  result.getMaximumForMetric(metric);
    }

    @Override
    public void setValueAt(Object object, int rowNum, int columnNum) {
        final MetricInstance instance = metrics.get(rowNum);
        if (instance == null) {
            return;
        }
        if (columnNum == 4) {
            final String valueString = ((String) object).trim();
            if(valueString.isEmpty()) {
                instance.setLowerThresholdEnabled(false);
            } else {
                try {
                    final double newThreshold = Double.parseDouble(valueString);
                    instance.setLowerThresholdEnabled(true);
                    instance.setLowerThreshold(newThreshold);
                } catch (NumberFormatException ignore) {
                    instance.setLowerThresholdEnabled(false);
                }
            }
        } else {
            final String valueString = ((String) object).trim();
            if (valueString.isEmpty()) {
                instance.setUpperThresholdEnabled(false);
            } else {
                try {
                    final double newThreshold = Double.parseDouble(valueString);
                    instance.setUpperThresholdEnabled(true);
                    instance.setUpperThreshold(newThreshold);
                } catch (NumberFormatException ignore) {
                    instance.setUpperThresholdEnabled(false);
                }
            }
        }
    }
}
