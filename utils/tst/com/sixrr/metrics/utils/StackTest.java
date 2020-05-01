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

import org.junit.Test;

import java.util.EmptyStackException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Bas Leijdekkers
 */
public class StackTest {

    @Test(expected = EmptyStackException.class)
    public void testOne() {
        final Stack<String> stack = new Stack<>();
        assertEquals("[]", stack.toString());
        stack.push("one");
        assertEquals(1, stack.size());
        assertEquals("[one]", stack.toString());
        stack.push("two");
        assertEquals(2, stack.size());
        assertEquals("[two, one]", stack.toString());
        assertEquals("two", stack.pop());
        assertEquals(1, stack.size());
        assertEquals("[one]", stack.toString());
        assertEquals("one", stack.pop());
        assertEquals(0, stack.size());
        stack.pop();
    }

    @Test
    public void testBigger() {
        final Stack<Integer> stack = new Stack<>();
        stack.push(0);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        stack.push(6);
        stack.push(7);
        stack.push(8);
        stack.push(9);
        stack.push(10);
        assertEquals("[10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]", stack.toString());
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        assertEquals("[]", stack.toString());
    }
}
