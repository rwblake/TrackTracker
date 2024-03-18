package team.bham.service.spotify;

import java.time.Instant;
import java.time.ZonedDateTime;

public class TimeListenedCountMapInsights {

    public Instant hours;
    public ZonedDateTime when;

    public TimeListenedCountMapInsights(Instant hours, ZonedDateTime when) {
        this.hours = hours;
        this.when = when;
    }
}
