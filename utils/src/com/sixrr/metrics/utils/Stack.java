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

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Simple stack which does not allocate an array when it is empty or contains only
 * one element. Can contain {@code null} elements. The internal element array can
 * grow, but does not shrink. It is however discarded any time the stack reaches
 * size 1.
 *
 * @author Bas Leijdekkers
 */
public class Stack<E> {

    private Object elements = null;
    private int size = 0;

    public void push(E e) {
        if (size == 0) {
            elements = e;
        }
        else if (size == 1) {
            final Object[] newArray = new Object[10];
            newArray[0] = elements;
            newArray[1] = e;
            elements = newArray;
        }
        else {
            Object[] array = (Object[]) elements;
            if (array.length == size) {
                elements = array = Arrays.copyOf(array, size + size);
            }
            array[size] = e;
        }
        size++;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        else if (size == 1) {
            final E result = (E) elements;
            elements = null;
            size = 0;
            return result;
        }
        else if (size == 2) {
            final Object[] array = (Object[]) elements;
            final E result = (E) array[1];
            elements = array[0];
            size = 1;
            return result;
        }
        else {
            final Object[] array = (Object[]) elements;
            size--;
            final E result = (E) array[size];
            array[size] = null;
            return result;
        }
    }

    public int size() {
        return size;
    }

    public String toString() {
        if (size == 0) {
            return "[]";
        }
        else if (size == 1) {
            return "[" + elements + "]";
        }
        else {
            final Object[] array = (Object[]) elements;
            final StringBuilder result = new StringBuilder("[");
            for (int i = size - 1; i >= 0; i--) {
                result.append(array[i]);
                if (i > 0) {
                    result.append(", ");
                }
            }
            result.append("]");
            return result.toString();
        }
    }
}
