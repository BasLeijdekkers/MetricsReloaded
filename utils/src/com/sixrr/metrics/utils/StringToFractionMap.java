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

package com.sixrr.metrics.utils;

public class StringToFractionMap {
    private int m_count = 0;
    private String[] m_keys;
    private double[] m_numerators;
    private double[] m_denominators;
    private static final int INITIAL_SIZE = 11;

    public StringToFractionMap() {
        super();
        m_keys = new String[INITIAL_SIZE];
        m_numerators = new double[INITIAL_SIZE];
        m_denominators = new double[INITIAL_SIZE];
    }

    public boolean containsKey(String key) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = m_keys;
        final int length = keys.length;
        int index = hash % length;
        String cur = keys[index];
        if (cur != null
                && !cur.equals(key)) {
            final int probe = 1 + hash % (length - 2);
            do {
                index -= probe;
                if (index < 0) {
                    index += length;
                }
                cur = keys[index];
            } while (cur != null && !cur.equals(key));
        }

        if (cur == null) {
            return false;
        } else {
            return cur.equals(key);
        }
    }

    public void put(String key, double numerator, double denominator) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = m_keys;
        final int length = keys.length;
        int index = hash % length;
        String cur = keys[index];
        if (cur != null && !cur.equals(key)) {
            final int probe = 1 + hash % (length - 2);
            do {
                index -= probe;
                if (index < 0) {
                    index += length;
                }
                cur = keys[index];
            } while (cur != null && !cur.equals(key));
        }
        if (cur == null) {
            keys[index] = key;
            m_numerators[index] = numerator;
            m_denominators[index] = denominator;
            m_count++;
            if (m_count > m_keys.length >> 1) {
                rehash();
            }
        } else {
            m_numerators[index] = numerator;
            m_denominators[index] = denominator;
        }
    }

    public double get(String key) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = m_keys;
        final int length = keys.length;
        int index = hash % length;
        String cur = keys[index];
        if (cur != null && !cur.equals(key)) {
            final int probe = 1 + hash % (length - 2);
            do {
                index -= probe;
                if (index < 0) {
                    index += length;
                }
                cur = keys[index];
            } while (cur != null && !cur.equals(key));
        }
        final double denominator = m_denominators[index];
        final double numerator = m_numerators[index];
        if (denominator == 0.0) {
            return 1.0;
        }
        return numerator / denominator;
    }

    private void rehash() {
        final int length = PrimeFinder.nextPrime(m_keys.length << 1);
        final String[] oldkeys = m_keys;
        final double[] oldNumerators = m_numerators;
        final double[] oldDenominators = m_denominators;
        m_keys = new String[length];
        m_numerators = new double[length];
        m_denominators = new double[length];

        final String[] keys = m_keys;
        final double[] numerators = m_numerators;
        final double[] denominators = m_denominators;
        final int oldLength = oldkeys.length;
        for (int i = 0; i < oldLength; i++) {
            final String key = oldkeys[i];
            if (key != null) {
                final double numerator = oldNumerators[i];
                final double denominator = oldDenominators[i];
                final int hash = key.hashCode() & 0x7fffffff;

                int index = hash % length;
                String cur = keys[index];
                if (cur != null) {
                    final int probe = 1 + hash % (length - 2);
                    do {
                        index -= probe;
                        if (index < 0) {
                            index += length;
                        }
                        cur = keys[index];
                    } while (cur != null);
                }
                keys[index] = key;
                numerators[index] = numerator;
                denominators[index] = denominator;
            }
        }
    }

    public double getMinimum() {
        double minimum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < m_numerators.length; i++) {
            final String key = m_keys[i];
            if (key != null) {
                minimum = Math.min(minimum, m_numerators[i] / m_denominators[i]);
            }
        }
        return minimum;
    }
    public double getMaximum() {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < m_numerators.length; i++) {
            final String key = m_keys[i];
            if (key != null) {
                max = Math.max(max, m_numerators[i] / m_denominators[i]);
            }
        }
        return max;
    }

    public double getTotal() {
        double total = 0.0;
        for (int i = 0; i < m_numerators.length; i++) {
            final String key = m_keys[i];
            if (key != null) {
                total += m_numerators[i] / m_denominators[i];
            }
        }
        return total;
    }

    public double getAverage() {
        double totalNumerator = 0.0;
        double totalDenominator = 0.0;
        for (int i = 0; i < m_numerators.length; i++) {
            final String key = m_keys[i];
            if (key != null) {
                totalNumerator += m_numerators[i];
                totalDenominator += m_denominators[i];
            }
        }
        if (totalDenominator == 0.0) {
            return 1.0;
        } else {
            return totalNumerator / totalDenominator;
        }
    }
}
