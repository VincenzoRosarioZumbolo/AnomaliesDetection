package app.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class DataRecord {
    private Instant timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}