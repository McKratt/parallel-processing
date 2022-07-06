package net.bakaar.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import reactor.core.scheduler.Schedulers;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties(BatchProperties.class)
public class ReactiveBatchApplication implements CommandLineRunner {

    private final BatchProperties properties;
    private final TaskFactory factory;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReactiveBatchApplication.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
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
                .subscribe();
    }
}
