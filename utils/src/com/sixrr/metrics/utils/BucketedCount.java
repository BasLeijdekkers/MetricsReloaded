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

import gnu.trove.TObjectIntHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BucketedCount<T> {

    private final TObjectIntHashMap<T> buckets = new TObjectIntHashMap<>();

    public void createBucket(@NotNull T bucket) {
        if (!buckets.containsKey(bucket)) {
            buckets.put(bucket, 0);
        }
    }

    public Set<T> getBuckets() {
        final Set<T> result = new HashSet<>(buckets.size());
        buckets.forEachKey(t -> {
            result.add(t);
            return true;
        });
        return result;
    }

    public void incrementBucketValue(@NotNull T bucket, int increment) {
        if (buckets.containsKey(bucket)) {
            buckets.adjustValue(bucket, increment);
        } else {
            buckets.put(bucket, increment);
        }
    }

    public void incrementBucketValue(@NotNull T bucket) {
        incrementBucketValue(bucket, 1);
    }

    public boolean containsBucket(T bucket) {
        return buckets.containsKey(bucket);
    }

    public int getBucketValue(T bucket) {
        return buckets.get(bucket);
    }

    public void clear() {
        buckets.clear();
    }
}
