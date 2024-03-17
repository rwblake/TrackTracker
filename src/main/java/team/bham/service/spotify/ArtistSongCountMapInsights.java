package team.bham.service.spotify;

public class ArtistSongCountMapInsights {

    public String artistName;
    public int occurrencesInSpotify;

    public ArtistSongCountMapInsights(String artistName, int occurrencesInSpotify) {
        this.artistName = artistName;
        this.occurrencesInSpotify = occurrencesInSpotify;
    }
}
