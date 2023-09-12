package net.bakaar.batch.structured;

import net.bakaar.batch.commons.BatchProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch")
public class StructuredTaskBatchProperties extends BatchProperties {
}
