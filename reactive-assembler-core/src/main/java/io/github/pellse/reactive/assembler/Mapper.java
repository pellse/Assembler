/*
 * Copyright 2018 Sebastien Pelletier
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

package io.github.pellse.reactive.assembler;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.pellse.reactive.assembler.RuleContext.ruleContext;

@FunctionalInterface
public interface Mapper<ID, R> extends Function<Iterable<ID>, Mono<Map<ID, R>>> {

    static <ID, T, R> Mapper<ID, R> rule(Function<T, ID> idExtractor, RuleMapper<ID, List<ID>, T, R> mapper) {
        return rule(ruleContext(idExtractor), mapper);
    }

    static <ID, IDC extends Collection<ID>, T, R> Mapper<ID, R> rule(
            Function<T, ID> idExtractor,
            Supplier<IDC> idCollectionFactory,
            RuleMapper<ID, IDC, T, R> mapper) {
        return ids -> mapper.apply(ruleContext(idExtractor, idCollectionFactory), ids);
    }

    static <ID, IDC extends Collection<ID>, T, R> Mapper<ID, R> rule(RuleContext<ID, IDC, T> ruleContext, RuleMapper<ID, IDC, T, R> mapper) {
        return ids -> mapper.apply(ruleContext, ids);
    }
}
