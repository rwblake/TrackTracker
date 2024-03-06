package team.bham.service.spotify;

/** Associates a year with a number of song occurrences.
 * Used to form the Playlist Insights HTTP response.
 */
public class YearSongCountMap {

    public String year;
    public int songCount;

    public YearSongCountMap(String year, int songCount) {
        this.year = year;
        this.songCount = songCount;
    }
}
