package team.bham.service.spotify;

/** Simple, reduced Song class, used to pass data in HTTP responses */
public class SimpleSong {

    public String songSpotifyID;
    public String songTitle;
    public String songImageURL;

    public SimpleSong(String songSpotifyID, String songTitle, String songImageURL) {
        this.songSpotifyID = songSpotifyID;
        this.songTitle = songTitle;
        this.songImageURL = songImageURL;
    }
}
