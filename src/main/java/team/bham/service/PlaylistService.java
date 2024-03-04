package team.bham.service;

import java.io.IOException;
import java.util.List;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.domain.Genre;
import team.bham.domain.Playlist;
import team.bham.domain.Song;
import team.bham.repository.ArtistRepository;
import team.bham.repository.GenreRepository;
import team.bham.repository.PlaylistRepository;

@Service
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongService songService;
    private final PlaylistStatsService playlistStatsService;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    public PlaylistService(
        PlaylistRepository playlistRepository,
        SongService songService,
        PlaylistStatsService playlistStatsService,
        GenreRepository genreRepository,
        ArtistRepository artistRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.playlistStatsService = playlistStatsService;
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
    }

    public Playlist createPlaylist(
        se.michaelthelin.spotify.model_objects.specification.Playlist playlist,
        List<Track> tracks,
        List<AudioFeatures> audioFeaturesList,
        Artist[] artists
    ) {
        // Check if playlist is already in the database
        String spotifyID = playlist.getId();
        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            // If yes: return that song
            return playlistRepository.findPlaylistBySpotifyID(spotifyID);
        }

        if (tracks.size() != audioFeaturesList.size()) {
            throw new IllegalArgumentException(
                "tracks and audioFeaturesList are different sizes, respectively: " + tracks.size() + " " + audioFeaturesList.size()
            );
        }

        // Create new playlist object
        Playlist myPlaylist = new Playlist();
        myPlaylist.setSpotifyID(playlist.getId());
        myPlaylist.setName(playlist.getName());
        myPlaylist.setImageURL(playlist.getImages()[0].getUrl());

        Song tmpSong;
        // for each song in playlist
        for (int i = 0; i < tracks.size(); i++) {
            tmpSong = songService.createSong(tracks.get(i), audioFeaturesList.get(i));
            myPlaylist.addSong(tmpSong);
        }

        // Get artist Genres
        Genre myGenre;
        for (se.michaelthelin.spotify.model_objects.specification.Artist artist : artists) {
            for (String name : artist.getGenres()) {
                if (genreRepository.existsByName(name)) {
                    myGenre = genreRepository.findGenreByName(name);
                } else {
                    myGenre = new Genre();
                    myGenre.setName(name);
                }
                myGenre.addArtist(artistRepository.findArtistBySpotifyID(artist.getId()));
                genreRepository.save(myGenre);
            }
        }

        playlistStatsService.createPlaylistStats(myPlaylist);
        playlistRepository.save(myPlaylist);

        return myPlaylist;
    }
}
