/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BuckettedCount<T> {

    private final Map<T, Integer> buckets = new HashMap<T, Integer>();

    public void createBucket(@NotNull T bucketName) {
        if (!buckets.containsKey(bucketName)) {
            buckets.put(bucketName, 0);
        }
    }

    public Set<T> getBuckets() {
        return buckets.keySet();
    }

    public void incrementBucketValue(@NotNull T bucketName, int increment) {
        if (buckets.containsKey(bucketName)) {
            buckets.put(bucketName, buckets.get(bucketName) + increment);
        } else {
            buckets.put(bucketName, increment);
        }
    }

    public void incrementBucketValue(@NotNull T bucketName) {
        incrementBucketValue(bucketName, 1);
    }

    public int getBucketValue(T bucketName) {
        if (!buckets.containsKey(bucketName)) {
            return 0;
        }
        return buckets.get(bucketName);
    }

    public void clear() {
        buckets.clear();
    }
}
