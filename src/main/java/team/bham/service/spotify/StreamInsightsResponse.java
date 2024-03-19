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

public class StreamInsightsResponse implements Serializable {

    private final Counter<Song> songCounter;
    private final Counter<Artist> artistCounter;
    private final Counter<String> decadeCounter;
    private final Counter<Genre> genreCounter;
    private final Counter<Album> albumCounter;

    public StreamInsightsResponse(
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

    public Counter<Song> getSongCounter() {
        return songCounter;
    }

    public Counter<Artist> getArtistCounter() {
        return artistCounter;
    }

    public Counter<String> getDecadeCounter() {
        return decadeCounter;
    }

    public Counter<Genre> getGenreCounter() {
        return genreCounter;
    }

    public Counter<Album> getAlbumCounter() {
        return albumCounter;
    }
}

class Counter<T> implements Serializable {

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

class Entry<T> implements Serializable {

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
