package team.bham.service.spotify;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Song;

public class FriendsInsightsPopularCategoriesResponse implements Serializable {

    private List<FriendsInsightsFrequencyPair<Song>> tracks;
    private List<FriendsInsightsFrequencyPair<Artist>> artists;
    private List<FriendsInsightsFrequencyPair<Album>> albums;

    private <K> FriendsInsightsFrequencyPair<K> entryToFriendsInsightsFrequencyPair(Map.Entry<K, Integer> entry) {
        return new FriendsInsightsFrequencyPair<>(entry.getKey(), entry.getValue());
    }

    private <K> List<FriendsInsightsFrequencyPair<K>> entriesToFriendsInsightsFrequencyPairs(List<Map.Entry<K, Integer>> entries) {
        return entries.stream().map(this::entryToFriendsInsightsFrequencyPair).collect(Collectors.toList());
    }

    public FriendsInsightsPopularCategoriesResponse(
        List<Map.Entry<Song, Integer>> tracks,
        List<Map.Entry<Artist, Integer>> artists,
        List<Map.Entry<Album, Integer>> albums
    ) {
        this.tracks = entriesToFriendsInsightsFrequencyPairs(tracks);
        this.artists = entriesToFriendsInsightsFrequencyPairs(artists);
        this.albums = entriesToFriendsInsightsFrequencyPairs(albums);
    }

    public List<FriendsInsightsFrequencyPair<Song>> getTracks() {
        return tracks;
    }

    public void setTracks(List<FriendsInsightsFrequencyPair<Song>> tracks) {
        this.tracks = tracks;
    }

    public List<FriendsInsightsFrequencyPair<Artist>> getArtists() {
        return artists;
    }

    public void setArtists(List<FriendsInsightsFrequencyPair<Artist>> artists) {
        this.artists = artists;
    }

    public List<FriendsInsightsFrequencyPair<Album>> getAlbums() {
        return albums;
    }

    public void setAlbums(List<FriendsInsightsFrequencyPair<Album>> albums) {
        this.albums = albums;
    }
}
