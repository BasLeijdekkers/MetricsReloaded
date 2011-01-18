/*
 * Copyright 2005, Sixth and Red River Software
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

import org.jfree.ui.BevelArrowIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class HeaderRenderer extends DefaultTableCellRenderer {
    private static final Icon UP_ARROW = new BevelArrowIcon(BevelArrowIcon.UP,
            false, false);
    private static final Icon DOWN_ARROW = new BevelArrowIcon(BevelArrowIcon.DOWN, false, false);
    private final String toolTipText;
    private final MetricTableModel model;

    HeaderRenderer(String toolTipText, MetricTableModel model) {
        super();
        this.toolTipText = toolTipText;
        this.model = model;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
        final JLabel label =
                (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected,
                        hasFocus, row,
                        column);
        if (toolTipText != null) {
            label.setToolTipText(toolTipText);
        } else {
            label.setToolTipText("");
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        //noinspection HardCodedStringLiteral
        label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        if (!isSelected) {
            label.setBackground(table.getTableHeader().getBackground());
        }
        final int sortColumn = model.getSortColumn();
        final int modelColumn = table.convertColumnIndexToModel(column);
        if (sortColumn == modelColumn) {
            if (model.isAscending()) {
                label.setIcon(DOWN_ARROW);
            } else {
                label.setIcon(UP_ARROW);
            }
        } else {
            label.setIcon(null);
        }
        label.setFont(table.getTableHeader().getFont());
        return label;
    }
}
