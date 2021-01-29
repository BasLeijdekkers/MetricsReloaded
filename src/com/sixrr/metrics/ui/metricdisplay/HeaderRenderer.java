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

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTableCellRenderer;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class HeaderRenderer extends ColoredTableCellRenderer {

    private static final Icon UP_ARROW = AllIcons.General.ArrowUp;
    private static final Icon DOWN_ARROW = AllIcons.General.ArrowDown;
    private final String toolTipText;
    private final MetricTableModel model;
    private final int alignment;

    HeaderRenderer(String toolTipText, MetricTableModel model, @JdkConstants.HorizontalAlignment int alignment) {
        this.toolTipText = toolTipText;
        this.model = model;
        this.alignment = alignment;
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTable table,
                                         @Nullable Object value,
                                         boolean selected,
                                         boolean hasFocus,
                                         int row,
                                         int column) {
        setToolTipText(toolTipText);
        final int sortColumn = model.getSortColumn();
        final int modelColumn = table.convertColumnIndexToModel(column);
        setIconOnTheRight(alignment != SwingConstants.RIGHT);
        if (sortColumn == modelColumn) {
            setTransparentIconBackground(true);
            setIcon(model.isAscending() ? UP_ARROW : DOWN_ARROW);
        }
        if (!selected) {
            setBackground(table.getTableHeader().getBackground());
        }
        if (value != null) {
            setTextAlign(alignment);
            append(value.toString());
        }
    }
}
