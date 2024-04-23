package team.bham.service.spotify;

import java.io.Serializable;
import java.util.Map;

public class Entry<T> implements Serializable {

    private final T metric;
    private final Integer value;

    public Entry(Map.Entry<T, Integer> counterList) {
        this.metric = counterList.getKey();
        this.value = counterList.getValue();
    }

    public T getMetric() {
        return metric;
    }

    public Integer getValue() {
        return value;
    }
}
