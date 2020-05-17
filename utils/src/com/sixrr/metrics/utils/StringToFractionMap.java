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

package com.sixrr.metrics.utils;

public class StringToFractionMap {
    private int size = 0;
    private String[] keys;
    private double[] numerators;
    private double[] denominators;
    private static final int INITIAL_SIZE = 11;

    public StringToFractionMap() {
        keys = new String[INITIAL_SIZE];
        numerators = new double[INITIAL_SIZE];
        denominators = new double[INITIAL_SIZE];
    }

    public boolean containsKey(String key) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = this.keys;
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

        return cur != null;
    }

    public void put(String key, double numerator, double denominator) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = this.keys;
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
            numerators[index] = numerator;
            denominators[index] = denominator;
            size++;
            if (size > keys.length >> 1) {
                rehash();
            }
        } else {
            numerators[index] = numerator;
            denominators[index] = denominator;
        }
    }

    public double get(String key) {
        final int hash = key.hashCode() & 0x7fffffff;
        final String[] keys = this.keys;
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
        final double denominator = denominators[index];
        final double numerator = numerators[index];
        if (denominator == 0.0) {
            return 1.0;
        }
        return numerator / denominator;
    }

    private void rehash() {
        final int length = PrimeFinder.nextPrime(keys.length << 1);
        final String[] oldKeys = keys;
        final double[] oldNumerators = numerators;
        final double[] oldDenominators = denominators;

        final String[] keys = this.keys = new String[length];
        final double[] numerators = this.numerators = new double[length];
        final double[] denominators = this.denominators = new double[length];
        final int oldLength = oldKeys.length;
        for (int i = 0; i < oldLength; i++) {
            final String key = oldKeys[i];
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

    public String[] getKeys() {
        final String[] result = new String[size];
        int i = 0;
        for (String key : keys) {
            if (key != null) {
                result[i++] = key;
            }
        }
        return result;
    }

    public double getMinimum() {
        double minimum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numerators.length; i++) {
            final String key = keys[i];
            if (key != null) {
                minimum = Math.min(minimum, numerators[i] / denominators[i]);
            }
        }
        return minimum;
    }
    public double getMaximum() {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numerators.length; i++) {
            final String key = keys[i];
            if (key != null) {
                max = Math.max(max, numerators[i] / denominators[i]);
            }
        }
        return max;
    }

    public double getTotal() {
        double total = 0.0;
        for (int i = 0; i < numerators.length; i++) {
            final String key = keys[i];
            if (key != null) {
                total += numerators[i] / denominators[i];
            }
        }
        return total;
    }

    public double getAverage() {
        double totalNumerator = 0.0;
        double totalDenominator = 0.0;
        for (int i = 0; i < numerators.length; i++) {
            final String key = keys[i];
            if (key != null) {
                totalNumerator += numerators[i];
                totalDenominator += denominators[i];
            }
        }
        return (totalDenominator == 0.0) ? 1.0 : totalNumerator / totalDenominator;
    }

    public int size() {
        return size;
    }
}
