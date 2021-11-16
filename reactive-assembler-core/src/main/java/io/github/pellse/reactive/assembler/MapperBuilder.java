package io.github.pellse.reactive.assembler;

import org.reactivestreams.Publisher;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface MapperBuilder<ID, R, RC> extends Function<Function<R, ID>, Mapper<ID, RC>> {

    static <ID, R> MapperBuilder<ID, R, R> oneToOne(
            Function<List<ID>, Publisher<R>> queryFunction) {

        return oneToOne(queryFunction, id -> null, ArrayList::new, null);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, R> oneToOne(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<IDC> idCollectionFactory) {

        return oneToOne(queryFunction, id -> null, idCollectionFactory);
    }

    static <ID, R> MapperBuilder<ID, R, R> oneToOne(
            Function<List<ID>, Publisher<R>> queryFunction,
            Function<ID, R> defaultResultProvider) {

        return oneToOne(queryFunction, defaultResultProvider, ArrayList::new, null);
    }

    static <ID, R> MapperBuilder<ID, R, R> oneToOne(
            Function<List<ID>, Publisher<R>> queryFunction,
            Function<ID, R> defaultResultProvider,
            MapFactory<ID, R> mapFactory) {

        return oneToOne(queryFunction, defaultResultProvider, ArrayList::new, mapFactory);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, R> oneToOne(
            Function<IDC, Publisher<R>> queryFunction,
            Function<ID, R> defaultResultProvider,
            Supplier<IDC> idCollectionFactory) {

        return oneToOne(queryFunction, defaultResultProvider, idCollectionFactory, null);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, R> oneToOne(
            Function<IDC, Publisher<R>> queryFunction,
            Function<ID, R> defaultResultProvider,
            Supplier<IDC> idCollectionFactory,
            MapFactory<ID, R> mapFactory) {
        return idExtractor -> Mapper.oneToOne(queryFunction, idExtractor, defaultResultProvider, idCollectionFactory, mapFactory);
    }

    static <ID, R> MapperBuilder<ID, R, List<R>> oneToManyAsList(
            Function<List<ID>, Publisher<R>> queryFunction) {

        return oneToManyAsList(queryFunction, ArrayList::new, null);
    }

    static <ID, R> MapperBuilder<ID, R, List<R>> oneToManyAsList(
            Function<List<ID>, Publisher<R>> queryFunction,
            MapFactory<ID, List<R>> mapFactory) {

        return oneToManyAsList(queryFunction, ArrayList::new, mapFactory);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, List<R>> oneToManyAsList(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<IDC> idCollectionFactory) {

        return oneToMany(queryFunction, ArrayList::new, idCollectionFactory);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, List<R>> oneToManyAsList(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<IDC> idCollectionFactory,
            MapFactory<ID, List<R>> mapFactory) {

        return oneToMany(queryFunction, ArrayList::new, idCollectionFactory, mapFactory);
    }

    static <ID, R> MapperBuilder<ID, R, Set<R>> oneToManyAsSet(
            Function<Set<ID>, Publisher<R>> queryFunction) {

        return oneToManyAsSet(queryFunction, HashSet::new, null);
    }

    static <ID, R> MapperBuilder<ID, R, Set<R>> oneToManyAsSet(
            Function<Set<ID>, Publisher<R>> queryFunction,
            MapFactory<ID, Set<R>> mapFactory) {

        return oneToManyAsSet(queryFunction, HashSet::new, mapFactory);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, Set<R>> oneToManyAsSet(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<IDC> idCollectionFactory) {

        return oneToMany(queryFunction, HashSet::new, idCollectionFactory);
    }

    static <ID, IDC extends Collection<ID>, R> MapperBuilder<ID, R, Set<R>> oneToManyAsSet(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<IDC> idCollectionFactory,
            MapFactory<ID, Set<R>> mapFactory) {

        return oneToMany(queryFunction, HashSet::new, idCollectionFactory, mapFactory);
    }

    static <ID, IDC extends Collection<ID>, R, RC extends Collection<R>> MapperBuilder<ID, R, RC> oneToMany(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<RC> collectionFactory,
            Supplier<IDC> idCollectionFactory) {

        return oneToMany(queryFunction, collectionFactory, idCollectionFactory, null);
    }

    static <ID, IDC extends Collection<ID>, R, RC extends Collection<R>> MapperBuilder<ID, R, RC> oneToMany(
            Function<IDC, Publisher<R>> queryFunction,
            Supplier<RC> collectionFactory,
            Supplier<IDC> idCollectionFactory,
            MapFactory<ID, RC> mapFactory) {

        return idExtractor -> Mapper.oneToMany(queryFunction, idExtractor, collectionFactory, idCollectionFactory, mapFactory);
    }
}