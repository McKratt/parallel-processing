package net.bakaar.batch;

import reactor.core.publisher.Mono;

public class ReactiveTask<T> {

    private final ReactiveRunnable<T> runnable;

    public ReactiveTask(ReactiveRunnable<T> runnable) {
        this.runnable = runnable;
    }

    public Mono<T> run() {
        return this.runnable.run();
    }
}
