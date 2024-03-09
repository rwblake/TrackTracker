package team.bham.service.spotify;

/** Holds graph data for Playlist Insights*/

public class PlaylistInsightGraphData {

    YearSongCountMap[] yearMaps;
    ArtistSongCountMap[] artistMaps;
    GenreSongCountMap[] genreMaps;

    public PlaylistInsightGraphData(YearSongCountMap[] yearMaps, ArtistSongCountMap[] artistMaps, GenreSongCountMap[] genreMaps) {
        this.yearMaps = yearMaps;
        this.artistMaps = artistMaps;
        this.genreMaps = genreMaps;
    }
}
