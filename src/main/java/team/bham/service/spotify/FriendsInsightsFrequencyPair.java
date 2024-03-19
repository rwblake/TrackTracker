package team.bham.service.spotify;

import java.io.Serializable;

public class FriendsInsightsFrequencyPair<T> implements Serializable {

    private final T value;
    private final Long frequency;

    public FriendsInsightsFrequencyPair(T value, Integer frequency) {
        this.value = value;
        this.frequency = frequency.longValue();
    }

    public FriendsInsightsFrequencyPair(T value, Long frequency) {
        this.value = value;
        this.frequency = frequency;
    }

    public T getValue() {
        return value;
    }

    public Long getFrequency() {
        return frequency;
    }
}
