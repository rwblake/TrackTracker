package team.bham.service.spotify;

public class GenreSongCountMapInsights {

    public String genreName;
    public int occurrencesInSpotify;

    public GenreSongCountMapInsights(String genreName, int occurrencesInSpotify) {
        this.genreName = genreName;
        this.occurrencesInSpotify = occurrencesInSpotify;
    }
}
