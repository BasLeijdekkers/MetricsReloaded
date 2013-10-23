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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricInstanceAbbreviationComparator;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.profile.MetricTableSpecification;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.util.*;

class MetricTableModel extends AbstractTableModel {
    private MetricsResult results;
    private final MetricTableSpecification tableSpecification;
    private MetricsResult prevResults;
    private String[] measuredObjects;
    private MetricInstance[] metricsInstances;
    private final int[] columnPermutation;
    private int[] rowPermutation;
    private final String type;
    private final MetricsProfileRepository profileRepository;

    MetricTableModel(MetricsResult results, String type, MetricTableSpecification tableSpecification,
                     MetricsProfileRepository profileRepository) {
        super();
        this.results = results;
        this.type = type;
        this.profileRepository = profileRepository;
        this.tableSpecification = tableSpecification;
        prevResults = null;
        measuredObjects = results.getMeasuredObjects();
        metricsInstances = findInstances(results.getMetrics());
        Arrays.sort(metricsInstances, new MetricInstanceAbbreviationComparator());
        final Map<MetricInstance, Integer> remainingMetrics = new LinkedHashMap<MetricInstance, Integer>();
        for (int i = 0; i < metricsInstances.length; i++) {
            final MetricInstance metric = metricsInstances[i];
            remainingMetrics.put(metric, i + 1);
        }
        columnPermutation = new int[metricsInstances.length + 1];
        final List<String> columnOrder = tableSpecification.getColumnOrder();
        final List<String> strippedColumnOrder = new ArrayList<String>();
        for (final String columnName : columnOrder) {
            boolean found = false;
            if (columnName.equals(type)) {
                found = true;
            } else {
                for (final MetricInstance metricInstance : metricsInstances) {
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
        for (final String columnName : strippedColumnOrder) {
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
        for (final Integer position : remainingMetricSlots) {
            columnPermutation[columnCount] = position;
            columnCount++;
        }
        rowPermutation = new int[measuredObjects.length];
        sort();
    }

    private MetricInstance[] findInstances(Metric[] metrics) {
        final MetricsProfile profile = profileRepository.getCurrentProfile();
        final MetricInstance[] metricInstances = new MetricInstance[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            metricInstances[i] = profile.getMetricForClass(metrics[i].getClass());
        }
        return metricInstances;
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

    public void setPrevResults(MetricsResult newResults) {
        prevResults = newResults;
        tabulateMetrics();
        tabulateMeasuredObjects();
        rowPermutation = new int[measuredObjects.length];
        sort();
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    private void tabulateMeasuredObjects() {
        final String[] resultObjects = results.getMeasuredObjects();
        final Set<String> allObjects = new HashSet<String>(resultObjects.length);
        if (prevResults != null) {
            final String[] prevResultObjects = prevResults.getMeasuredObjects();
            Collections.addAll(allObjects, prevResultObjects);
        }
        Collections.addAll(allObjects, resultObjects);
        measuredObjects = allObjects.toArray(new String[allObjects.size()]);
    }

    private void tabulateMetrics() {
        final Metric[] currentMetrics = results.getMetrics();
        final MetricInstance[] resultMetrics = findInstances(currentMetrics);
        final Set<MetricInstance> allMetrics = new HashSet<MetricInstance>(resultMetrics.length);
        Collections.addAll(allMetrics, resultMetrics);
        if (prevResults != null) {
            final MetricInstance[] prevResultMetrics = findInstances(prevResults.getMetrics());
            Collections.addAll(allMetrics, prevResultMetrics);
        }
        metricsInstances = allMetrics.toArray(new MetricInstance[allMetrics.size()]);
        Arrays.sort(metricsInstances, new MetricInstanceAbbreviationComparator());
    }

    @Override
    public int getRowCount() {
        if (hasSummaryRows()) {
            return measuredObjects.length + 2;
        } else {
            return measuredObjects.length;
        }
    }

    public boolean hasSummaryRows() {
        return measuredObjects.length > 1;
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
            return metricInstance.getMetric().getAbbreviation();
        }
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
                    return new Pair<Double, Double>(value, prevValue);
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
                    return new Pair<Double, Double>(value, prevValue);
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
            return new Pair<Double, Double>(value, prevValue);
        }
    }

    public void changeSort(int column, boolean ascending) {
        tableSpecification.setSortColumn(column);
        tableSpecification.setAscending(ascending);
        sort();
        fireTableDataChanged();
        profileRepository.persistCurrentProfile();
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
                tempArray[i] = new Pair<Integer, String>(i, name);
            }
        } else {
            for (int i = 0; i < rowPermutation.length; i++) {
                final String name = measuredObjects[i];
                final MetricInstance metricInstance = metricsInstances[permutedColumn - 1];
                final Double value = results.getValueForMetric(metricInstance.getMetric(), name);
                tempArray[i] = new Pair<Integer, Double>(i, value);
            }
        }
        Arrays.sort(tempArray, new PairComparator(tableSpecification));
        for (int i = 0; i < tempArray.length; i++) {
            rowPermutation[i] = tempArray[i].getFirst();
        }
    }

    public MetricInstance getMetricForColumn(int column) {
        final int permutedColumn = columnPermutation[column];
        return metricsInstances[permutedColumn - 1];
    }

    public int getSortColumn() {
        return tableSpecification.getSortColumn();
    }

    public boolean isAscending() {
        return tableSpecification.isAscending();
    }

    public boolean hasDiff() {
        return prevResults != null;
    }

    public MetricInstance[] getMetricsInstances() {
        return metricsInstances.clone();
    }

    public MetricsResult getResults() {
        return results;
    }

    @Nullable
    public PsiElement getElementAtRow(int row) {
        if (row >= rowPermutation.length) {
            return null;
        }
        final String measuredObject = measuredObjects[rowPermutation[row]];

        return results.getElementForMeasuredObject(measuredObject);
    }

    private static class PairComparator implements Comparator<Pair> {
        private final MetricTableSpecification tableSpecification;

        private PairComparator(MetricTableSpecification tableSpecification) {
            this.tableSpecification = tableSpecification;
        }

        public int compare(Pair pair1, Pair pair2) {
            final Object value1 = pair1.getSecond();
            final Object value2 = pair2.getSecond();
            if (value1 == null && value2 == null) {
                return 0;
            }
            final int comparison;
            if (value1 == null) {
                comparison = -1;
            } else if (value2 == null) {
                comparison = 1;
            } else {
                comparison = ((Comparable) value1).compareTo(value2);
            }
            if (tableSpecification.isAscending()) {
                return comparison;
            } else {
                return -comparison;
            }
        }
    }
}
