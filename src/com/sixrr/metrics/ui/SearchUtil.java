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
package com.sixrr.metrics.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public final class SearchUtil {

    private SearchUtil() {}

    public static List<String> tokenizeFilter(CharSequence filter) {
        final int length = filter.length();
        final List<String> result = new ArrayList();
        final StringBuilder token = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < length; i++) {
            final char c = filter.charAt(i);
            if (Character.isWhitespace(c) && !quoted) {
                if (token.length() > 0) {
                    result.add(token.toString());
                    token.setLength(0);
                }
            }
            else if (c == '\'' || c == '"') {
                quoted = !quoted;
            }
            else {
                token.append(c);
            }
        }
        if (token.length() > 0) {
            result.add(token.toString());
        }
        return result;
    }
}
