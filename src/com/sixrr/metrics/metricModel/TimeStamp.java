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

package com.sixrr.metrics.metricModel;

import org.jetbrains.annotations.NonNls;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Currently use in file/DB exporter.
 */
public class TimeStamp {
    @NonNls public static final String TIMESTAMP_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
    @NonNls public static final String TIMESTAMP_DB_FORMAT = "yyyy-MM-dd";

    private final Date timestamp;

    public TimeStamp() {
        timestamp = new Date();
    }

    public TimeStamp(String timestamp) {
        try {
            DateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
            this.timestamp = df.parse(timestamp);
        }
        catch (ParseException ex) {
            throw new RuntimeException("Internal error :: could not parse the timestamp [" + timestamp + "]");
        }
    }

    public String toString() {
        DateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
        return df.format(timestamp);
    }

    public String toSQLString() {
        DateFormat df = new SimpleDateFormat(TIMESTAMP_DB_FORMAT);
        return df.format(timestamp);
    }
}
