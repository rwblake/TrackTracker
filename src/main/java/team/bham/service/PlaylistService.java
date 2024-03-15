package team.bham.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import team.bham.domain.Playlist;
import team.bham.domain.Song;
import team.bham.repository.PlaylistRepository;
import team.bham.service.spotify.PlaylistRetriever;

@Service
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongService songService;
    private final PlaylistStatsService playlistStatsService;

    public PlaylistService(PlaylistRepository playlistRepository, SongService songService, PlaylistStatsService playlistStatsService) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.playlistStatsService = playlistStatsService;
    }

    public Playlist createPlaylist(se.michaelthelin.spotify.model_objects.specification.Playlist playlist, SpotifyApi spotifyApi)
        throws IOException, ParseException, SpotifyWebApiException {
        boolean playlistExists = false;
        Playlist myPlaylist = null;

        // Check if playlist is already in the database
        String spotifyID = playlist.getId();
        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            // If yes, check if its data is outdated.
            myPlaylist = playlistRepository.findPlaylistBySpotifyID(spotifyID);
            long daysOld = Duration.between(myPlaylist.getPlaylistStats().getLastUpdated(), Instant.now()).toMinutes();
            if (daysOld < 60) {
                return myPlaylist;
            } else {
                playlistExists = true;
            }
        }

        // Create new playlist object
        if (!playlistExists) {
            myPlaylist = new Playlist();
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
}
