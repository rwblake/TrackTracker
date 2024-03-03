package team.bham.service.spotify;

import java.time.Year;

public class PlaylistInsightsHTTPResponse {

    /** This class is used to temporarily store data for a PlaylistInsights HTTP response.
     * It is converted to JSON before being sent.
     */
    String happiestSongID;
    String energeticSongID;
    String sumsUpSongID;
    String anomalousSongID;

    float averageValence;
    float averageEnergy;
    float averageAcousticness;
    float averageDanceability;

    YearSongCountMap[] yearsToSongs;
    ArtistProportionMap[] artistsToProportions;

    public PlaylistInsightsHTTPResponse(
        String[] selectedSongIDs,
        float[] statAverages,
        YearSongCountMap[] yearsToSongs,
        ArtistProportionMap[] artistsToProportions
    ) {
        happiestSongID = selectedSongIDs[0];
        energeticSongID = selectedSongIDs[1];
        sumsUpSongID = selectedSongIDs[2];
        anomalousSongID = selectedSongIDs[3];

        averageValence = statAverages[0];
        averageEnergy = statAverages[1];
        averageAcousticness = statAverages[2];
        averageDanceability = statAverages[3];

        this.yearsToSongs = yearsToSongs;
        this.artistsToProportions = artistsToProportions;
    }
}
