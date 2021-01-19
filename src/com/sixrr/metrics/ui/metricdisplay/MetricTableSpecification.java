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

import com.intellij.util.SmartList;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;

import java.util.Collections;
import java.util.List;

@Tag("table_layout")
public final class MetricTableSpecification {
    @Attribute("sort_column")
    private int sortColumn = 0;
    @Attribute("ascending")
    private boolean ascending = true;
    @XCollection(propertyElementName = "column_order", elementName = "c", valueAttributeName = "", style = XCollection.Style.v2)
    private List<String> columnOrder = new SmartList<>();
    @XCollection(propertyElementName = "column_widths", elementName = "w", valueAttributeName = "", style = XCollection.Style.v2)
    private List<Integer> columnWidths = new SmartList<>();

    public int getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(int sortColumn) {
        this.sortColumn = sortColumn;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public List<String> getColumnOrder() {
        return Collections.unmodifiableList(columnOrder);
    }

    public void setColumnOrder(List<String> columnOrder) {
        this.columnOrder = columnOrder;
    }

    public List<Integer> getColumnWidths() {
        return Collections.unmodifiableList(columnWidths);
    }

    public void setColumnWidths(List<Integer> columnWidths) {
        this.columnWidths = columnWidths;
    }
}
