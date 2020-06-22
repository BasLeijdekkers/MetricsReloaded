/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.metricModel;

import com.sixrr.metrics.Metric;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMetric implements Metric {

    protected BaseMetric() {
        final Class<?> aClass = getClass();
        final String className = aClass.getName();
        final int startIndex = className.lastIndexOf((int) '.') + 1;
        @NonNls final int endIndex = className.length() - "Metric".length();
        name = className.substring(startIndex, endIndex);
    }

    private final String name;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Metric)) {
            return false;
        }

        final Metric baseMetric = (Metric) obj;
        return name.equals(baseMetric.getID());
    }

    public int hashCode() {
        return name.hashCode();
    }

    @NotNull
    @Override
    public String getID() {
        return name;
    }

    @Deprecated
    @Override
    @Nullable
    public String getHelpURL() {
        return null;
    }

    @Deprecated
    @Override
    @Nullable
    public String getHelpDisplayString() {
        return null;
    }

    @Override
    public boolean requiresDependents() {
        return false;
    }
}
