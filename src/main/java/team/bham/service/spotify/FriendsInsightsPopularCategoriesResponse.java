package team.bham.service.spotify;

import java.util.List;
import java.util.Map;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Song;

public class FriendsInsightsPopularCategoriesResponse {

    public List<Map.Entry<Song, Integer>> tracks;
    public List<Map.Entry<Artist, Integer>> artists;
    public List<Map.Entry<Album, Integer>> albums;

    public FriendsInsightsPopularCategoriesResponse(
        List<Map.Entry<Song, Integer>> tracks,
        List<Map.Entry<Artist, Integer>> artists,
        List<Map.Entry<Album, Integer>> albums
    ) {
        this.tracks = tracks;
        this.artists = artists;
        this.albums = albums;
    }
}
