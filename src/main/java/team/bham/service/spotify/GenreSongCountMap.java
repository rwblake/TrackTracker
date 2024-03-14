package team.bham.service.spotify;

/** Associates a genre to a number of occurrences in the playlist.
 * Used to form the Playlist Insights HTTP response.
 */
public class GenreSongCountMap {

    public String genreName;
    public int occurrencesInPlaylist;

    public GenreSongCountMap(String genreName, int occurrencesInPlaylist) {
        this.genreName = genreName;
        this.occurrencesInPlaylist = occurrencesInPlaylist;
    }
}
