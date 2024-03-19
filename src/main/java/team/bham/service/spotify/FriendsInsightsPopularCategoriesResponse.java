package team.bham.service.spotify;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Song;

public class FriendsInsightsPopularCategoriesResponse implements Serializable {

    public static class FrequencyPair<T> implements Serializable {

        private final T value;
        private final Integer frequency;

        public FrequencyPair(T value, Integer frequency) {
            this.value = value;
            this.frequency = frequency;
        }

        public T getValue() {
            return value;
        }

        public Integer getFrequency() {
            return frequency;
        }
    }

    private List<FrequencyPair<Song>> tracks;
    private List<FrequencyPair<Artist>> artists;
    private List<FrequencyPair<Album>> albums;

    private <K> FrequencyPair<K> entryToFrequencyPair(Map.Entry<K, Integer> entry) {
        return new FrequencyPair<>(entry.getKey(), entry.getValue());
    }

    private <K> List<FrequencyPair<K>> entriesToFrequencyPairs(List<Map.Entry<K, Integer>> entries) {
        return entries.stream().map(this::entryToFrequencyPair).collect(Collectors.toList());
    }

    public FriendsInsightsPopularCategoriesResponse(
        List<Map.Entry<Song, Integer>> tracks,
        List<Map.Entry<Artist, Integer>> artists,
        List<Map.Entry<Album, Integer>> albums
    ) {
        this.tracks = entriesToFrequencyPairs(tracks);
        this.artists = entriesToFrequencyPairs(artists);
        this.albums = entriesToFrequencyPairs(albums);
    }

    public List<FrequencyPair<Song>> getTracks() {
        return tracks;
    }

    public void setTracks(List<FrequencyPair<Song>> tracks) {
        this.tracks = tracks;
    }

    public List<FrequencyPair<Artist>> getArtists() {
        return artists;
    }

    public void setArtists(List<FrequencyPair<Artist>> artists) {
        this.artists = artists;
    }

    public List<FrequencyPair<Album>> getAlbums() {
        return albums;
    }

    public void setAlbums(List<FrequencyPair<Album>> albums) {
        this.albums = albums;
    }
}
