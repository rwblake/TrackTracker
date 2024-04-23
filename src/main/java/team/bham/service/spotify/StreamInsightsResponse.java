package team.bham.service.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Genre;
import team.bham.domain.Song;

public class StreamInsightsResponse implements Serializable {

    private final Counter<Song> songCounter;
    private final Counter<Artist> artistCounter;
    private final Counter<String> decadeCounter;
    private final Counter<Genre> genreCounter;
    private final Counter<Album> albumCounter;

    public StreamInsightsResponse(
        Counter<Song> songCounter,
        Counter<Artist> artistCounter,
        Counter<String> decadeCounter,
        Counter<Genre> genreCounter,
        Counter<Album> albumCounter
    ) {
        this.songCounter = songCounter;
        this.artistCounter = artistCounter;
        this.decadeCounter = decadeCounter;
        this.genreCounter = genreCounter;
        this.albumCounter = albumCounter;
    }

    public Counter<Song> getSongCounter() {
        return songCounter;
    }

    public Counter<Artist> getArtistCounter() {
        return artistCounter;
    }

    public Counter<String> getDecadeCounter() {
        return decadeCounter;
    }

    public Counter<Genre> getGenreCounter() {
        return genreCounter;
    }

    public Counter<Album> getAlbumCounter() {
        return albumCounter;
    }
}
