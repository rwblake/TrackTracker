package team.bham.service.spotify;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Counter<T> implements Serializable {

    private final List<Entry<T>> byWeek;
    private final List<Entry<T>> byMonth;
    private final List<Entry<T>> byYear;
    private final List<Entry<T>> ofAllTime;

    public Counter(List<List<Map.Entry<T, Integer>>> counterList) {
        if (counterList.size() != 4) throw new IllegalArgumentException("counterList must have 4 counters; invalid list length provided");

        this.byWeek = counterList.get(0).stream().map(Entry::new).collect(Collectors.toList());
        this.byMonth = counterList.get(1).stream().map(Entry::new).collect(Collectors.toList());
        this.byYear = counterList.get(2).stream().map(Entry::new).collect(Collectors.toList());
        this.ofAllTime = counterList.get(3).stream().map(Entry::new).collect(Collectors.toList());
    }

    public List<Entry<T>> getByWeek() {
        return byWeek;
    }

    public List<Entry<T>> getByMonth() {
        return byMonth;
    }

    public List<Entry<T>> getByYear() {
        return byYear;
    }

    public List<Entry<T>> getOfAllTime() {
        return ofAllTime;
    }
}
