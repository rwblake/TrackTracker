package team.bham.service.spotify;

/** Associates artist data to the number of occurrences in a playlist
 * Used to form the Playlist Insights HTTP response.
 */
public class ArtistProportionMap {

    public String artistSpotifyID;
    public String artistName;
    public String artistImageURL;
    public int occurrencesInPlaylist;

    public ArtistProportionMap(String artistSpotifyID, String artistName, String artistImageURL, int occurrencesInPlaylist) {
        this.artistSpotifyID = artistSpotifyID;
        this.artistName = artistName;
        this.artistImageURL = artistImageURL;
        this.occurrencesInPlaylist = occurrencesInPlaylist;
    }
}
