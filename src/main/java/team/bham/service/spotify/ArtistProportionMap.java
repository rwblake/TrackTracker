package team.bham.service.spotify;

public class ArtistProportionMap {

    public String artistSpotifyID;
    public float proportionOfPlaylist;

    public ArtistProportionMap(String artistSpotifyID, float proportionOfPlaylist) {
        this.artistSpotifyID = artistSpotifyID;
        this.proportionOfPlaylist = proportionOfPlaylist;
    }
}
