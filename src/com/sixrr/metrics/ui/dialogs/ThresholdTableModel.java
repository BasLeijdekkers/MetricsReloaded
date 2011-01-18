package com.sixrr.metrics.ui.dialogs;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.profile.MetricsProfile;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ThresholdTableModel extends AbstractTableModel {

    private final List<Metric> metrics;
    private final MetricsProfile profile;
    private final MetricsResult result;


    public ThresholdTableModel(List<Metric> metrics, MetricsProfile profile, MetricsResult result) {
        this.metrics = metrics;
        this.profile = profile;
        this.result = result;
    }

    public int getRowCount() {
        return metrics.size();
    }

    public int getColumnCount() {
        return 6;
    }


    public boolean isCellEditable(int rowNum, int columnNum) {
        return columnNum > 3;
    }


    public String getColumnName(int columnNum) {
        switch (columnNum) {
            case 0:
                return "Name";
            case 1:
                return "Abbv";
            case 2:
                return "Min";
            case 3:
                return "Max";
            case 4:
                return "Warn if less than";
            case 5:
                return "Warn if greater than";
            default:
                return null;
        }

    }

    public Class<?> getColumnClass(int columnNum) {
        return String.class;
    }

    public Object getValueAt(int rowNum, int columnNum) {
        final Metric metric = metrics.get(rowNum);
        final MetricInstance instance = profile.getMetricForClass(metric.getClass());
        if (instance == null) {
            return null;
        }
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

    public void setValueAt(Object object, int rowNum, int columnNum) {
        final Metric metric = metrics.get(rowNum);
        final MetricInstance instance = profile.getMetricForClass(metric.getClass());
        if (instance == null) {
            return;
        }
        if (columnNum == 4) {
            final String valueString = ((String) object).trim();
            if(valueString.length() == 0)
            {
                instance.setLowerThresholdEnabled(false);
            }
            else
            {
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
            if (valueString.length() == 0) {
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
