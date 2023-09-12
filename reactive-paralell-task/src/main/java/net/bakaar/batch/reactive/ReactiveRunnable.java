package net.bakaar.batch.reactive;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveRunnable<T> {

    Mono<T> run();

}
