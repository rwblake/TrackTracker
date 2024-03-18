package team.bham.service.spotify;

public class MyInsightsGraphData {

    YearSongCountMapInsights[] decadeMapsWeek;
    YearSongCountMapInsights[] decadeMapsMonth;
    YearSongCountMapInsights[] decadeMapsYear;
    YearSongCountMapInsights[] decadeMapsAllTime;
    SongCountMapInsights[] songMapsWeek;
    SongCountMapInsights[] songMapsMonth;
    SongCountMapInsights[] songMapsYear;
    SongCountMapInsights[] songMapsAllTime;
    ArtistSongCountMapInsights[] artistMapsWeek;
    ArtistSongCountMapInsights[] artistMapsMonth;
    ArtistSongCountMapInsights[] artistMapsYear;
    ArtistSongCountMapInsights[] artistMapsAllTime;
    GenreSongCountMapInsights[] genreMapsWeek;
    GenreSongCountMapInsights[] genreMapsMonth;
    GenreSongCountMapInsights[] genreMapsYear;
    GenreSongCountMapInsights[] genreMapsAllTime;
    AlbumSongCountMapInsights[] albumMapsWeek;
    AlbumSongCountMapInsights[] albumMapsMonth;
    AlbumSongCountMapInsights[] albumMapsYear;
    AlbumSongCountMapInsights[] albumMapsAllTime;
    TimeListenedCountMapInsights[] timeListenedMapsWeek;
    TimeListenedCountMapInsights[] timeListenedMapsMonth;
    TimeListenedCountMapInsights[] timeListenedMapsYear;
    TimeListenedCountMapInsights[] timeListenedMapsAllTime;

    public MyInsightsGraphData(
        YearSongCountMapInsights[] decadeMapsWeek,
        YearSongCountMapInsights[] decadeMapsMonth,
        YearSongCountMapInsights[] decadeMapsYear,
        YearSongCountMapInsights[] decadeMapsAllTime,
        SongCountMapInsights[] songMapsWeek,
        SongCountMapInsights[] songMapsMonth,
        SongCountMapInsights[] songMapsYear,
        SongCountMapInsights[] songMapsAllTime,
        ArtistSongCountMapInsights[] artistMapsWeek,
        ArtistSongCountMapInsights[] artistMapsMonth,
        ArtistSongCountMapInsights[] artistMapsYear,
        ArtistSongCountMapInsights[] artistMapsAllTime,
        GenreSongCountMapInsights[] genreMapsWeek,
        GenreSongCountMapInsights[] genreMapsMonth,
        GenreSongCountMapInsights[] genreMapsYear,
        GenreSongCountMapInsights[] genreMapsAllTime,
        AlbumSongCountMapInsights[] albumMapsWeek,
        AlbumSongCountMapInsights[] albumMapsMonth,
        AlbumSongCountMapInsights[] albumMapsYear,
        AlbumSongCountMapInsights[] albumMapsAllTime,
        TimeListenedCountMapInsights[] timeListenedMapsWeek,
        TimeListenedCountMapInsights[] timeListenedMapsMonth,
        TimeListenedCountMapInsights[] timeListenedMapsYear,
        TimeListenedCountMapInsights[] timeListenedMapsAllTime
    ) {
        this.decadeMapsWeek = decadeMapsWeek;
        this.decadeMapsMonth = decadeMapsMonth;
        this.decadeMapsYear = decadeMapsYear;
        this.decadeMapsAllTime = decadeMapsAllTime;

        this.songMapsWeek = songMapsWeek;
        this.songMapsMonth = songMapsMonth;
        this.songMapsYear = songMapsYear;
        this.songMapsAllTime = songMapsAllTime;

        this.artistMapsWeek = artistMapsWeek;
        this.artistMapsMonth = artistMapsMonth;
        this.artistMapsYear = artistMapsYear;
        this.artistMapsAllTime = artistMapsAllTime;

        this.genreMapsWeek = genreMapsWeek;
        this.genreMapsMonth = genreMapsMonth;
        this.genreMapsYear = genreMapsYear;
        this.genreMapsAllTime = genreMapsAllTime;

        this.albumMapsWeek = albumMapsWeek;
        this.albumMapsMonth = albumMapsMonth;
        this.albumMapsYear = albumMapsYear;
        this.albumMapsAllTime = albumMapsAllTime;

        this.timeListenedMapsWeek = timeListenedMapsWeek;
        this.timeListenedMapsMonth = timeListenedMapsMonth;
        this.timeListenedMapsYear = timeListenedMapsYear;
        this.timeListenedMapsAllTime = timeListenedMapsAllTime;
    }
}
