package net.bakaar.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.format;

@Slf4j
@Component
public class TaskFactory {
    public ReactiveTask<String> createTask(LocalDate date, ReportType type, String entity) {
        return new ReactiveTask<>(() -> {
            try {
                log.info(format("- - - Call remote for Entity %s and Report %s and the Accounting Date : %s", entity, type, date));
                // Simulate a remote blocking call which is not immediate.
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(format("Report %s for %s at %s", type, entity, date));
        });
    }
}
