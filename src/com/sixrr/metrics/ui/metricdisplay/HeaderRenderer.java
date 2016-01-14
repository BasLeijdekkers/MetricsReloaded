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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.icons.AllIcons;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class HeaderRenderer extends DefaultTableCellRenderer {

    private static final Icon UP_ARROW = AllIcons.Actions.UP;
    private static final Icon DOWN_ARROW = AllIcons.Actions.Down;
    private final String toolTipText;
    private final MetricTableModel model;
    private final int alignment;

    HeaderRenderer(String toolTipText, MetricTableModel model,
                   @MagicConstant(intValues = {LEFT, RIGHT, CENTER}) int alignment) {
        this.toolTipText = toolTipText;
        this.model = model;
        this.alignment = alignment;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setToolTipText(toolTipText);
        setHorizontalAlignment(alignment);
        setHorizontalTextPosition(alignment);
        //noinspection HardCodedStringLiteral
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        if (!isSelected) {
            setBackground(table.getTableHeader().getBackground());
        }
        final int sortColumn = model.getSortColumn();
        final int modelColumn = table.convertColumnIndexToModel(column);
        if (sortColumn == modelColumn) {
            setIcon(model.isAscending() ? UP_ARROW : DOWN_ARROW);
        } else {
            setIcon(null);
        }
        setFont(table.getTableHeader().getFont());
        setText(value.toString());
        return this;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (getIcon() != null) {
            final int textWidth = getFontMetrics(getFont()).stringWidth(getText());
            final Insets insets = getInsets();
            final int iconTextGap = width - textWidth - getIcon().getIconWidth() - insets.left - insets.right;
            setIconTextGap(iconTextGap);
        } else {
            setIconTextGap(0);
        }
    }
}
