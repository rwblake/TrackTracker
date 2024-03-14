package team.bham.service.spotify;

/** Holds graph data for Playlist Insights*/

public class PlaylistInsightGraphData {

    YearSongCountMap[] yearMaps;
    YearSongCountMap[] decadeMaps;
    ArtistSongCountMap[] artistMaps;
    GenreSongCountMap[] genreMaps;

    public PlaylistInsightGraphData(
        YearSongCountMap[] yearMaps,
        YearSongCountMap[] decadeMaps,
        ArtistSongCountMap[] artistMaps,
        GenreSongCountMap[] genreMaps
    ) {
        this.yearMaps = yearMaps;
        this.decadeMaps = decadeMaps;
        this.artistMaps = artistMaps;
        this.genreMaps = genreMaps;
    }
}
