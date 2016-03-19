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

package com.sixrr.metrics.ui.charts;

import org.jetbrains.annotations.Nullable;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

class PercentFormatter extends NumberFormat {
    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

    static {
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setMinimumFractionDigits(2);
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo,
                               FieldPosition pos) {
        final double value = number * 100.0;
        toAppendTo.append(numberFormatter.format(value) + '%');
        return toAppendTo;
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo,
                               FieldPosition pos) {
        final double value = (double) number * 100.0;
        toAppendTo.append(numberFormatter.format(value) + '%');
        return toAppendTo;
    }

    @Override
    @Nullable
    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
}
