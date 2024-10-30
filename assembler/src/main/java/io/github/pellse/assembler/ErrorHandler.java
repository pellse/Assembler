package io.github.pellse.assembler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.github.pellse.util.ObjectUtils.doNothing;

public sealed interface ErrorHandler {
    <T> Function<Flux<T>, Flux<T>> toFluxErrorHandler();

    record OnErrorContinue<E extends Throwable>(
            Predicate<E> errorPredicate,
            BiConsumer<Throwable, Object> errorConsumer) implements ErrorHandler {

        public static OnErrorContinue<?> onErrorContinue() {
            return onErrorContinue(doNothing());
        }

        public static OnErrorContinue<?> onErrorContinue(Consumer<Throwable> errorConsumer) {
            return onErrorContinue((t, o) -> errorConsumer.accept(t));
        }

        public static OnErrorContinue<?> onErrorContinue(BiConsumer<Throwable, Object> errorConsumer) {
            return onErrorContinue(e -> true, errorConsumer);
        }

        public static <E extends Throwable> OnErrorContinue<E> onErrorContinue(Predicate<E> errorPredicate, BiConsumer<Throwable, Object> errorConsumer) {
            return new OnErrorContinue<>(errorPredicate, errorConsumer);
        }

        @Override
        public <T> Function<Flux<T>, Flux<T>> toFluxErrorHandler() {
            return flux -> flux.onErrorContinue(errorPredicate(), errorConsumer());
        }
    }

    record OnErrorResume(
            Predicate<Throwable> errorPredicate,
            Consumer<Throwable> errorConsumer) implements ErrorHandler {

        public static OnErrorResume onErrorResume() {
            return onErrorResume(doNothing());
        }

        public static OnErrorResume onErrorResume(Consumer<Throwable> errorConsumer) {
            return onErrorResume(__ -> true, errorConsumer);
        }

        public static OnErrorResume onErrorResume(Predicate<Throwable> errorPredicate, Consumer<Throwable> errorConsumer) {
            return new OnErrorResume(errorPredicate, errorConsumer);
        }

        @Override
        public <T> Function<Flux<T>, Flux<T>> toFluxErrorHandler() {
            return flux -> flux
                    .doOnError(errorPredicate(), errorConsumer())
                    .onErrorResume(errorPredicate(), __ -> Mono.empty());
        }
    }

    record OnErrorMap(Function<? super Throwable, ? extends Throwable> mapper) implements ErrorHandler {

        public static OnErrorMap onErrorMap(Function<? super Throwable, ? extends Throwable> mapper) {
            return new OnErrorMap(mapper);
        }

        @Override
        public <T> Function<Flux<T>, Flux<T>> toFluxErrorHandler() {
            return flux -> flux.onErrorMap(mapper());
        }
    }

    record OnErrorStop() implements ErrorHandler {

        public static OnErrorStop onErrorStop() {
            return new OnErrorStop();
        }

        @Override
        public <T> Function<Flux<T>, Flux<T>> toFluxErrorHandler() {
            return Flux::onErrorStop;
        }
    }
}

