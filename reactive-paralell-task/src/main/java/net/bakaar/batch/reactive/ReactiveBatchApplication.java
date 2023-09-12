package net.bakaar.batch.reactive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties(ReactiveBatchProperties.class)
public class ReactiveBatchApplication implements CommandLineRunner {

    private final ReactiveBatchProperties properties;
    private final ReactiveTaskFactory factory;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReactiveBatchApplication.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        var start = System.nanoTime();
        properties.getAccountingDatesFlux()
                .doOnNext(date -> log.info("Begin treatment for date :" + date))
                .mapNotNull(date ->
                        properties.getReportTypesFlux()
                                .publishOn(Schedulers.parallel())
                                .doOnNext(type -> log.info("- Begin treatment for report type : " + type.name().toLowerCase()))
                                .flatMap(type -> properties.getEntitiesFlux()
                                        .publishOn(Schedulers.boundedElastic())
                                        .doOnNext(entity -> log.info(format("- - Begin treatment for Report %s for Entity %s", type, entity)))
                                        .map(entity -> factory.createTask(date, type, entity).run())
                                        .map(mono -> mono.subscribe(report -> log.info("- - - - " + report)))
                                )
                                .doFinally(s -> log.info(format("Treated all the report types for %s!", date)))
                                .blockLast()
                )
                .doFinally(s -> log.info("All Reports done !"))
                .doFinally(s -> {
                    var stop = System.nanoTime();
                    var nanos = stop - start;
                    var duration = Duration.ofNanos(nanos);
                    log.info("Elapsed time = {} s and {} ms ({})", duration.toSecondsPart(), duration.toMillisPart(), nanos);
                })
                .subscribe();
    }
}
