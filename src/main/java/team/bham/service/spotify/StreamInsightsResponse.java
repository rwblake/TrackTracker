package team.bham.service.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Genre;
import team.bham.domain.Song;

@JsonSerialize
@JsonDeserialize
public class StreamInsightsResponse implements Serializable {

    Counter<Song> songCounter;
    Counter<Artist> artistCounter;
    Counter<String> decadeCounter;
    Counter<Genre> genreCounter;
    Counter<Album> albumCounter;

    StreamInsightsResponse(
        Counter<Song> songCounter,
        Counter<Artist> artistCounter,
        Counter<String> decadeCounter,
        Counter<Genre> genreCounter,
        Counter<Album> albumCounter
    ) {
        this.songCounter = songCounter;
        this.artistCounter = artistCounter;
        this.decadeCounter = decadeCounter;
        this.genreCounter = genreCounter;
        this.albumCounter = albumCounter;
    }

    @Override
    public String toString() {
        return (
            "StreamInsightsResponse{" +
            "songCounter=" +
            songCounter +
            ", artistCounter=" +
            artistCounter +
            ", decadeCounter=" +
            decadeCounter +
            ", genreCounter=" +
            genreCounter +
            ", albumCounter=" +
            albumCounter +
            '}'
        );
    }
}

@JsonSerialize
@JsonDeserialize
class Counter<T> implements Serializable {

    List<Entry<T>> byWeek;
    List<Entry<T>> byMonth;
    List<Entry<T>> byYear;
    List<Entry<T>> ofAllTime;

    Counter(List<List<Map.Entry<T, Integer>>> counterList) {
        if (counterList.size() != 4) throw new IllegalArgumentException("counterList must have 4 counters; invalid list length provided");

        this.byWeek = counterList.get(0).stream().map(Entry::new).collect(Collectors.toList());
        this.byMonth = counterList.get(1).stream().map(Entry::new).collect(Collectors.toList());
        this.byYear = counterList.get(2).stream().map(Entry::new).collect(Collectors.toList());
        this.ofAllTime = counterList.get(3).stream().map(Entry::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Counter{" + "byWeek=" + byWeek + ", byMonth=" + byMonth + ", byYear=" + byYear + ", ofAllTime=" + ofAllTime + '}';
    }
}

@JsonSerialize
@JsonDeserialize
class Entry<T> implements Serializable {

    T metric;
    Integer value;

    Entry(Map.Entry<T, Integer> counterList) {
        this.metric = counterList.getKey();
        this.value = counterList.getValue();
    }

    @Override
    public String toString() {
        return "Entry{" + "metric=" + metric + ", value=" + value + '}';
    }
}
