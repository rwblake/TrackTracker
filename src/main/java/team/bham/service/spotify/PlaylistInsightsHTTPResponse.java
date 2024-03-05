package team.bham.service.spotify;

public class PlaylistInsightsHTTPResponse {

    /** This class is used to temporarily store data for a PlaylistInsights HTTP response.
     * It is converted to JSON before being sent.
     */

    String playlistTitle;
    String imageURL;
    SimpleSong happiestSong;
    SimpleSong energeticSong;
    SimpleSong sumsUpSong;
    SimpleSong anomalousSong;

    float averageValence;
    float averageEnergy;
    float averageAcousticness;
    float averageDanceability;

    YearSongCountMap[] yearsToSongs;
    ArtistProportionMap[] artistsToProportions;

    public PlaylistInsightsHTTPResponse(
        String playlistTitle,
        String imageURL,
        SimpleSong[] selectedSongs,
        float[] statAverages,
        YearSongCountMap[] yearsToSongs,
        ArtistProportionMap[] artistsToProportions
    ) {
        this.playlistTitle = playlistTitle;
        this.imageURL = imageURL;

        happiestSong = selectedSongs[0];
        energeticSong = selectedSongs[1];
        sumsUpSong = selectedSongs[2];
        anomalousSong = selectedSongs[3];

        averageValence = statAverages[0];
        averageEnergy = statAverages[1];
        averageAcousticness = statAverages[2];
        averageDanceability = statAverages[3];

        this.yearsToSongs = yearsToSongs;
        this.artistsToProportions = artistsToProportions;
    }
}
