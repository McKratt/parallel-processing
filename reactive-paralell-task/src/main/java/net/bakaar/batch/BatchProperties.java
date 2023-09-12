package net.bakaar.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties("batch")
public class BatchProperties {

    private final AccountingDates accountingDates = new AccountingDates();
    private ReportType[] types;

    private final List<String> entities = new ArrayList<>();

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

    @Setter
    @Getter
    public static class AccountingDates {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate from;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate to;
    }
}
