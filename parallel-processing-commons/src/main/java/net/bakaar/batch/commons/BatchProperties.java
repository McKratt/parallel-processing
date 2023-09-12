package net.bakaar.batch.commons;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BatchProperties {

    protected final AccountingDates accountingDates = new AccountingDates();
    protected final List<String> entities = new ArrayList<>();
    protected ReportType[] types;

    @Setter
    @Getter
    public static class AccountingDates {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate from;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate to;
    }
}
