package net.bakaar.batch.reactive;

import net.bakaar.batch.commons.BatchProperties;
import net.bakaar.batch.commons.ReportType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@ConfigurationProperties("batch")
public class ReactiveBatchProperties extends BatchProperties {

    public Flux<LocalDate> getAccountingDatesFlux() {
        if (accountingDates.to == null) {
            return Flux.just(accountingDates.from);
        }
        return Flux.fromStream(accountingDates.from.datesUntil(accountingDates.to.plusDays(1)));
    }

    public Flux<ReportType> getReportTypesFlux() {
        return Flux.fromArray(types);
    }

    public Flux<String> getEntitiesFlux() {
        return Flux.fromStream(entities.stream());
    }
}
