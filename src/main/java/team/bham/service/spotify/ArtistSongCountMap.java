package team.bham.service.spotify;

/** Associates artist data to the number of occurrences in a playlist
 * Used to form the Playlist Insights HTTP response.
 */
public class ArtistSongCountMap {

    public String artistName;
    public int occurrencesInPlaylist;

    public ArtistSongCountMap(String artistName, int occurrencesInPlaylist) {
        this.artistName = artistName;
        this.occurrencesInPlaylist = occurrencesInPlaylist;
    }
}
