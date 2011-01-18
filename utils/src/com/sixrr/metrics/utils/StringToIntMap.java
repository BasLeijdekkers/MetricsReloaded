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

import java.util.HashSet;
import java.util.Set;

class StringToIntMap {
    private int m_count = 0;
    private String[] m_keys;
    private int[] m_values;
    private static final int INITIAL_SIZE = 11;
    private static final int NEGATIVE_MASK = 0x7fffffff;

    StringToIntMap() {
        super();
        m_keys = new String[INITIAL_SIZE];
        m_values = new int[INITIAL_SIZE];
    }

    public boolean containsKey(String key) {
        final int hash = key.hashCode() & NEGATIVE_MASK;
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

    public void put(String key, int value) {
        final int hash = key.hashCode() & NEGATIVE_MASK;
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
            m_values[index] = value;
            m_count++;
            if (m_count > m_keys.length >> 1) {
                rehash();
            }
        } else {
            m_values[index] = value;
        }
    }

    public int get(String key) {
        final int hash = key.hashCode() & NEGATIVE_MASK;
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
        return m_values[index];
    }

    public void clear() {
        m_keys = new String[INITIAL_SIZE];
        m_values = new int[INITIAL_SIZE];
        m_count = 0;
    }

    private void rehash() {
        final int length = PrimeFinder.nextPrime(m_keys.length << 1);
        final String[] oldkeys = m_keys;
        final int[] oldValues = m_values;
        m_keys = new String[length];
        m_values = new int[length];

        final String[] keys = m_keys;
        final int[] values = m_values;
        final int oldLength = oldkeys.length;
        for (int i = 0; i < oldLength; i++) {
            final String key = oldkeys[i];
            if (key != null) {
                final int value = oldValues[i];
                final int hash = key.hashCode() & NEGATIVE_MASK;

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
                values[index] = value;
            }
        }
    }

    public void increment(String key, int increment) {
        final int hash = key.hashCode() & NEGATIVE_MASK;
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
            m_values[index] = increment;
            m_count++;
            if (m_count > m_keys.length >> 1) {
                rehash();
            }
        } else {
            m_values[index] += increment;
        }
    }

    public Set<String> getKeys() {
        final Set<String> out = new HashSet<String>(m_count << 1);
        for (final String key : m_keys) {
            if (key != null) {
                out.add(key);
            }
        }
        return out;
    }
}
