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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.metricModel.MetricInstanceAbbreviationComparator;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.util.*;

class MetricTableModel extends AbstractTableModel {

    private final int[] columnPermutation;
    private final MetricTableSpecification tableSpecification;
    private final String type;
    private String[] measuredObjects;
    private MetricInstance[] metricsInstances;
    private MetricsResult prevResults;
    private MetricsResult results;
    private int[] rowPermutation;

    MetricTableModel(@NotNull MetricsResult results, @NotNull String type,
                     @NotNull MetricTableSpecification tableSpecification) {
        this.results = results;
        this.type = type;
        this.tableSpecification = tableSpecification;
        prevResults = null;
        measuredObjects = results.getMeasuredObjects();
        metricsInstances = findMetricInstances(results.getMetrics());
        Arrays.sort(metricsInstances, new MetricInstanceAbbreviationComparator());
        final Map<MetricInstance, Integer> remainingMetrics = new LinkedHashMap<>();
        for (int i = 0; i < metricsInstances.length; i++) {
            final MetricInstance metric = metricsInstances[i];
            remainingMetrics.put(metric, Integer.valueOf(i + 1));
        }
        columnPermutation = new int[metricsInstances.length + 1];
        final List<String> columnOrder = tableSpecification.getColumnOrder();
        final Set<String> strippedColumnOrder = new LinkedHashSet<>();
        for (String columnName : columnOrder) {
            boolean found = false;
            if (columnName.equals(type)) {
                found = true;
            } else {
                for (MetricInstance metricInstance : metricsInstances) {
                    if (metricInstance.getMetric().getAbbreviation().equals(columnName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                strippedColumnOrder.add(columnName);
            }
        }
        int columnCount = 0;
        int leftOverCount = strippedColumnOrder.size();
        for (String columnName : strippedColumnOrder) {
            if (columnName.equals(type)) {
                columnPermutation[columnCount] = 0;
            } else {
                boolean found = false;
                for (int i = 0; i < metricsInstances.length; i++) {
                    final MetricInstance metricInstance = metricsInstances[i];
                    if (metricInstance.getMetric().getAbbreviation().equals(columnName)) {
                        if (columnCount < columnPermutation.length) {
                            columnPermutation[columnCount] = i + 1;
                            remainingMetrics.remove(metricInstance);
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    columnPermutation[columnCount] = leftOverCount;
                    leftOverCount++;
                }
            }
            columnCount++;
        }
        if (columnCount == 0) {
            columnCount = 1;
        }
        final Collection<Integer> remainingMetricSlots = remainingMetrics.values();
        for (Integer position : remainingMetricSlots) {
            columnPermutation[columnCount] = position.intValue();
            columnCount++;
        }
        rowPermutation = new int[measuredObjects.length];
        sort();
    }

    public void changeSort(int column, boolean ascending) {
        tableSpecification.setSortColumn(column);
        tableSpecification.setAscending(ascending);
        sort();
        fireTableDataChanged();
    }

    private static MetricInstance[] findMetricInstances(@NotNull Metric[] metrics) {
        final MetricsProfile profile = MetricsProfileRepository.getInstance().getSelectedProfile();
        final MetricInstance[] metricInstances = new MetricInstance[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            final MetricInstance metricInstance = profile.getMetricInstance(metrics[i]);
            assert metricInstance != null;
            metricInstances[i] = metricInstance;
        }
        return metricInstances;
    }

    @Override
    public int getColumnCount() {
        return metricsInstances.length + 1;
    }

    @Override
    public String getColumnName(int column) {
        final int permutedColumn = columnPermutation[column];
        if (permutedColumn == 0) {
            return type;
        } else {
            final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
            final Metric metric = metricInstance.getMetric();
            return metric.getAbbreviation();
        }
    }

    @Nullable
    public PsiElement getElementAtRow(int row) {
        if (row >= rowPermutation.length) {
            return null;
        }
        final String measuredObject = measuredObjects[rowPermutation[row]];

        return results.getElementForMeasuredObject(measuredObject);
    }

    public Object getOriginalAtRow(int row) {
        if (row >= rowPermutation.length) {
            return null;
        }
        final String measuredObject = measuredObjects[rowPermutation[row]];
        return results.getOriginalForMeasuredObject(measuredObject);
    }

    public MetricInstance getMetricForColumn(int column) {
        final int permutedColumn = columnPermutation[column];
        return metricsInstances[permutedColumn - 1];
    }

    public MetricInstance[] getMetricsInstances() {
        return metricsInstances.clone();
    }

    public MetricsResult getResults() {
        return results;
    }

    public void setResults(MetricsResult newResults) {
        results = newResults;
        tabulateMetrics();
        tabulateMeasuredObjects();
        rowPermutation = new int[measuredObjects.length];
        sort();
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return hasSummaryRows() ? measuredObjects.length + 2 : measuredObjects.length;
    }

    public int getSortColumn() {
        return tableSpecification.getSortColumn();
    }

    @Override
    @Nullable
    public Object getValueAt(int rowIndex, int columnIndex) {
        final int permutedColumn = columnPermutation[columnIndex];

        if (hasSummaryRows()) {
            if (rowIndex == measuredObjects.length) {
                if (permutedColumn == 0) {
                    return MetricsReloadedBundle.message("total");
                } else if (prevResults == null) {
                    final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                    return results.getTotalForMetric(metricInstance.getMetric());
                } else {
                    final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                    final Double value = results.getTotalForMetric(metricInstance.getMetric());
                    final Double prevValue = prevResults.getTotalForMetric(metricInstance.getMetric());
                    return Pair.create(value, prevValue);
                }
            }
            if (rowIndex == measuredObjects.length + 1) {
                if (permutedColumn == 0) {
                    return MetricsReloadedBundle.message("average");
                } else if (prevResults == null) {
                    final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                    return results.getAverageForMetric(metricInstance.getMetric());
                } else {
                    final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                    final Double value = results.getAverageForMetric(metricInstance.getMetric());
                    final Double prevValue = prevResults.getAverageForMetric(metricInstance.getMetric());
                    return Pair.create(value, prevValue);
                }
            }
        }
        final String measuredObject = measuredObjects[rowPermutation[rowIndex]];
        if (permutedColumn == 0) {
            return measuredObject;
        } else if (prevResults == null) {
            final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
            return results.getValueForMetric(metricInstance.getMetric(), measuredObject);
        } else {
            final MetricInstance metric = metricsInstances[permutedColumn - 1];
            final Double value = results.getValueForMetric(metric.getMetric(), measuredObject);
            final Double prevValue = prevResults.getValueForMetric(metric.getMetric(), measuredObject);
            return Pair.create(value, prevValue);
        }
    }

    public boolean hasDiff() {
        return prevResults != null;
    }

    public boolean hasSummaryRows() {
        return measuredObjects.length > 1;
    }

    public boolean isAscending() {
        return tableSpecification.isAscending();
    }

    public void setPrevResults(MetricsResult newResults) {
        prevResults = newResults;
        tabulateMetrics();
        tabulateMeasuredObjects();
        rowPermutation = new int[measuredObjects.length];
        sort();
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    private void sort() {
        // change the rowPermutation
        int sortColumn = tableSpecification.getSortColumn();
        if (sortColumn >= columnPermutation.length) {
            tableSpecification.setAscending(true);
            tableSpecification.setSortColumn(0);
            sortColumn = 0;
        }
        final Pair<Integer, ? extends Comparable<?>>[] tempArray = new Pair[rowPermutation.length];
        final int permutedColumn = columnPermutation[sortColumn];
        if (permutedColumn == 0) {
            for (int i = 0; i < rowPermutation.length; i++) {
                final String name = measuredObjects[i];
                tempArray[i] = Pair.create(Integer.valueOf(i), name);
            }
        } else {
            for (int i = 0; i < rowPermutation.length; i++) {
                final String name = measuredObjects[i];
                final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                final Double value = results.getValueForMetric(metricInstance.getMetric(), name);
                tempArray[i] = Pair.create(Integer.valueOf(i), value);
            }
        }
        Arrays.sort(tempArray, new PairComparator(tableSpecification.isAscending()));
        for (int i = 0; i < tempArray.length; i++) {
            rowPermutation[i] = tempArray[i].getFirst().intValue();
        }
    }

    private void tabulateMeasuredObjects() {
        final String[] resultObjects = results.getMeasuredObjects();
        final Set<String> allObjects = new HashSet<>(resultObjects.length);
        if (prevResults != null) {
            final String[] prevResultObjects = prevResults.getMeasuredObjects();
            Collections.addAll(allObjects, prevResultObjects);
        }
        Collections.addAll(allObjects, resultObjects);
        measuredObjects = allObjects.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
    }

    private void tabulateMetrics() {
        final Metric[] currentMetrics = results.getMetrics();
        final MetricInstance[] resultMetrics = findMetricInstances(currentMetrics);
        final Set<MetricInstance> allMetrics = new HashSet<>(resultMetrics.length);
        Collections.addAll(allMetrics, resultMetrics);
        if (prevResults != null) {
            final MetricInstance[] prevResultMetrics = findMetricInstances(prevResults.getMetrics());
            Collections.addAll(allMetrics, prevResultMetrics);
        }
        metricsInstances = allMetrics.toArray(MetricInstance.EMPTY_ARRAY);
        Arrays.sort(metricsInstances, new MetricInstanceAbbreviationComparator());
    }

    private static class PairComparator implements Comparator<Pair<Integer, ? extends Comparable>> {
        private final boolean ascending;

        PairComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Pair<Integer, ? extends Comparable> pair1, Pair<Integer, ? extends Comparable> pair2) {
            final Comparable value1 = pair1.getSecond();
            final Comparable value2 = pair2.getSecond();
            if (value1 == null && value2 == null) {
                return 0;
            }
            final int comparison;
            if (value1 == null) {
                comparison = -1;
            } else if (value2 == null) {
                comparison = 1;
            } else {
                if (value1 instanceof String && value2 instanceof String) {
                    final String s1 = (String) value1;
                    final String s2 = (String) value2;
                    comparison = StringUtil.naturalCompare(s1, s2);
                } else {
                    comparison = value1.compareTo(value2);
                }
            }
            return ascending ? comparison : -comparison;
        }
    }
}
