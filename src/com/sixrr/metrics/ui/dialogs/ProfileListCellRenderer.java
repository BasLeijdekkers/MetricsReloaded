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

package com.sixrr.metrics.ui.dialogs;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.sixrr.metrics.profile.MetricsProfile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Bas Leijdekkers
 */
public class ProfileListCellRenderer extends ColoredListCellRenderer<MetricsProfile> {
    @Override
    protected void customizeCellRenderer(@NotNull JList<? extends MetricsProfile> list, MetricsProfile profile,
                                         int index, boolean selected, boolean hasFocus) {
        append(profile.getName(), profile.isPrebuilt() ? SimpleTextAttributes.REGULAR_ATTRIBUTES
                                                       : SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        if (index == -1) {
            append("  " + (profile.isPrebuilt() ? "(system)" : "(user)"),
                   SimpleTextAttributes.GRAY_ATTRIBUTES);
        }

    }
}
