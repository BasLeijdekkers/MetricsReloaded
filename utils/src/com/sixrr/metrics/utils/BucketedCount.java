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

package com.sixrr.metrics.utils;

import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectProcedure;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BucketedCount<T> {

    private final TObjectIntHashMap<T> buckets = new TObjectIntHashMap<T>();

    public void createBucket(@NotNull T bucketName) {
        if (!buckets.containsKey(bucketName)) {
            buckets.put(bucketName, 0);
        }
    }

    public Set<T> getBuckets() {
        final Set<T> result = new HashSet<T>(buckets.size());
        buckets.forEachKey(new TObjectProcedure<T>() {
            @Override
            public boolean execute(T t) {
                result.add(t);
                return true;
            }
        });
        return result;
    }

    public void incrementBucketValue(@NotNull T bucketName, int increment) {
        if (buckets.containsKey(bucketName)) {
            buckets.adjustValue(bucketName, increment);
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
