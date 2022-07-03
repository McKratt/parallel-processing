package net.bakaar.batch;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveRunnable<T> {

    Mono<T> run();

}
