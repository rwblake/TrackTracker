package team.bham.service.spotify;

public class MyInsightsGraphData {

    YearSongCountMapInsights[] decadeMaps;
    SongCountMapInsights[] songMaps;
    ArtistSongCountMapInsights[] artistMaps;
    GenreSongCountMapInsights[] genreMaps;

    public MyInsightsGraphData(
        YearSongCountMapInsights[] decadeMaps,
        SongCountMapInsights[] songMaps,
        ArtistSongCountMapInsights[] artistMaps,
        GenreSongCountMapInsights[] genreMaps
    ) {
        this.decadeMaps = decadeMaps;
        this.songMaps = songMaps;
        this.artistMaps = artistMaps;
        this.genreMaps = genreMaps;
    }
}
