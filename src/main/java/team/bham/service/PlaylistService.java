package team.bham.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import team.bham.domain.AppUser;
import team.bham.domain.Playlist;
import team.bham.domain.Song;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.spotify.PlaylistRetriever;

@Service
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongService songService;
    private final PlaylistStatsService playlistStatsService;
    private final UserService userService;
    private final AppUserRepository appUserRepository;

    public PlaylistService(
        PlaylistRepository playlistRepository,
        SongService songService,
        PlaylistStatsService playlistStatsService,
        UserService userService,
        AppUserRepository appUserRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.playlistStatsService = playlistStatsService;
        this.userService = userService;
        this.appUserRepository = appUserRepository;
    }

    public Playlist createPlaylist(se.michaelthelin.spotify.model_objects.specification.Playlist playlist, SpotifyApi spotifyApi)
        throws IOException, ParseException, SpotifyWebApiException {
        boolean playlistExists = false;
        Playlist myPlaylist = null;
        AppUser currentUser = null;

        String spotifyID = playlist.getId();

        // Check for user. We'll need this later.
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            currentUser = this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        }

        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            playlistExists = true;
            myPlaylist = playlistRepository.findPlaylistBySpotifyID(spotifyID);

            // Check the playlist owner matches. If not, we'll try to pull it, but
            // we'll be rejected by Spotify if the user doesn't have the right to
            if (!(currentUser != null && currentUser != myPlaylist.getAppUser())) {
                // Check if its data is outdated.
                long minutesOld = Duration.between(myPlaylist.getPlaylistStats().getLastUpdated(), Instant.now()).toMinutes();
                if (minutesOld < 60) {
                    return myPlaylist;
                }
            }
        }

        // Create new playlist object
        if (!playlistExists) {
            myPlaylist = new Playlist();
        }

        // If there's a user logged in, make them the owner of the playlist!
        if (currentUser != null) {
            myPlaylist.setAppUser(currentUser);
        }

        myPlaylist.setSpotifyID(playlist.getId());
        myPlaylist.setName(playlist.getName());
        myPlaylist.setImageURL(playlist.getImages()[0].getUrl());

        // Create songs and add to playlist
        List<Song> mySongs = this.songService.createSongs(PlaylistRetriever.getTracks(spotifyApi, playlist.getId()), spotifyApi);
        for (Song mySong : mySongs) {
            myPlaylist.addSong(mySong);
        }

        playlistStatsService.createPlaylistStats(myPlaylist, playlistExists);
        playlistRepository.save(myPlaylist);

        return myPlaylist;
    }

    public void removePlaylist(Playlist playlist) {
        playlistRepository.deletePlaylistById(playlist.getId());
    }
}
