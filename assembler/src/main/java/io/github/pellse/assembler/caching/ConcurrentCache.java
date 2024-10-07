/*
 * Copyright 2024 Sebastien Pelletier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.pellse.assembler.caching;

import io.github.pellse.concurrent.ReentrantExecutor;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.lang.Long.MAX_VALUE;

public interface ConcurrentCache<ID, RRC> extends Cache<ID, RRC> {

    static <ID, RRC> ConcurrentCache<ID, RRC> concurrentCache(Cache<ID, RRC> delegateCache) {
        return concurrentCache(delegateCache, MAX_VALUE, MAX_VALUE);
    }

    static <ID, RRC> ConcurrentCache<ID, RRC> concurrentCache(Cache<ID, RRC> delegateCache, long readQueueCapacity, long writeQueueCapacity) {

        if (delegateCache instanceof ConcurrentCache<ID, RRC> concurrentCache) {
            return concurrentCache;
        }

        final var executor = ReentrantExecutor.create(readQueueCapacity, writeQueueCapacity);

        return new ConcurrentCache<>() {

            @Override
            public Mono<Map<ID, RRC>> getAll(Iterable<ID> ids) {
                return executor.withReadLock(delegateCache.getAll(ids), Map::of);
            }

            @Override
            public Mono<Map<ID, RRC>> computeAll(Iterable<ID> ids, FetchFunction<ID, RRC> fetchFunction) {
                return executor.withReadLock(x -> delegateCache.computeAll(ids, idsToFetch -> x.withLock(() -> fetchFunction.apply(idsToFetch))), Map::of);
            }

            @Override
            public Mono<?> putAll(Map<ID, RRC> map) {
                return executor.withWriteLock(delegateCache.putAll(map));
            }

            @Override
            public Mono<?> removeAll(Map<ID, RRC> map) {
                return executor.withWriteLock(delegateCache.removeAll(map));
            }

            @Override
            public Mono<?> updateAll(Map<ID, RRC> mapToAdd, Map<ID, RRC> mapToRemove) {
                return executor.withWriteLock(delegateCache.updateAll(mapToAdd, mapToRemove));
            }
        };
    }
}
