package team.bham.service.spotify;

public class ArtistProportionMap {

    public String artistSpotifyID;
    public String artistName;
    public String artistImageURL;
    public int occurencesInPlaylist;

    public ArtistProportionMap(String artistSpotifyID, String artistName, String artistImageURL, int occurencesInPlaylist) {
        this.artistSpotifyID = artistSpotifyID;
        this.artistName = artistName;
        this.artistImageURL = artistImageURL;
        this.occurencesInPlaylist = occurencesInPlaylist;
    }
}
