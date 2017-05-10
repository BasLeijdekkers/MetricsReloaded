/*
 * Copyright 2005-2017 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.yourkit.util.Asserts.assertEqual;

/**
 * @author Aleksandr Chudov.
 */
public class SetUtilTest {
    @Test
    public void testHasIntersec() {
        assertEqual(false, SetUtil.hasIntersec(null, new HashSet<Integer>(Arrays.asList(1, 2, 3))));
        assertEqual(false, SetUtil.hasIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), null));
        assertEqual(true, SetUtil.hasIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(3, 4, 5))));
        assertEqual(true, SetUtil.hasIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(1, 2, 3))));
        assertEqual(false, SetUtil.hasIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(6, 4, 5))));
    }

    @Test
    public void testSizeOfIntersec() {
        assertEqual(0, SetUtil.sizeOfIntersec(null, new HashSet<Integer>(Arrays.asList(1, 2, 3))));
        assertEqual(0, SetUtil.sizeOfIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), null));
        assertEqual(1, SetUtil.sizeOfIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(3, 4, 5))));
        assertEqual(3, SetUtil.sizeOfIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(1, 2, 3))));
        assertEqual(0, SetUtil.sizeOfIntersec(new HashSet<Integer>(Arrays.asList(1, 2, 3)), new HashSet<Integer>(Arrays.asList(6, 4, 5))));
    }
}
