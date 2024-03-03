package team.bham.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.domain.Playlist;
import team.bham.domain.PlaylistStats;
import team.bham.domain.Song;
import team.bham.repository.PlaylistRepository;
import team.bham.repository.PlaylistStatsRepository;
import team.bham.repository.SongRepository;

@Service
@Transactional
public class PlaylistStatsService {

    private final PlaylistStatsRepository playlistStatsRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final SongService songService;
    private final PlaylistService playlistService;

    public PlaylistStatsService(
        PlaylistStatsRepository playlistStatsRepository,
        SongService songService,
        SongRepository songRepository,
        PlaylistRepository playlistRepository,
        PlaylistService playlistService
    ) {
        this.playlistStatsRepository = playlistStatsRepository;
        this.songService = songService;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.playlistService = playlistService;
    }

    public PlaylistStats createPlaylistStats(
        se.michaelthelin.spotify.model_objects.specification.Playlist playlist,
        Track[] selectedTracks,
        AudioFeatures[] selectedAudioFeatures
    ) {
        // Check if playlist stats have already been generated, and if playlist even exists
        String spotifyID = playlist.getId();
        Playlist correspondingPlaylist;
        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            correspondingPlaylist = playlistRepository.findPlaylistBySpotifyID(spotifyID);
            return correspondingPlaylist.getPlaylistStats();
        } else {
            // Playlist and stats have to be generated simultaneously because of required on-to-one relationship.
            correspondingPlaylist =
                playlistService.createPlaylist(
                    playlist,
                    new ArrayList<Track>(Arrays.asList(selectedTracks)),
                    new ArrayList<AudioFeatures>(Arrays.asList(selectedAudioFeatures))
                );
        }

        // Create new playlist stats object
        PlaylistStats myPlaylistStats = new PlaylistStats();

        // Set selected songs
        Song happiestSong = songService.createSong(selectedTracks[0], selectedAudioFeatures[0]);
        myPlaylistStats.setHappiestSong(happiestSong);
        Song energeticSong = songService.createSong(selectedTracks[1], selectedAudioFeatures[1]);
        myPlaylistStats.setEnergeticSong(energeticSong);
        Song sumsUpSong = songService.createSong(selectedTracks[2], selectedAudioFeatures[2]);
        myPlaylistStats.setSumsUpSong(sumsUpSong);
        Song anomalousSong = songService.createSong(selectedTracks[3], selectedAudioFeatures[3]);
        myPlaylistStats.setAnonmalousSong(anomalousSong);

        // Update playlist with stats, and vice versa
        myPlaylistStats.setPlaylist(correspondingPlaylist);
        myPlaylistStats.setPlaylistLength(playlist.getTracks().getTotal());
        myPlaylistStats.setLastUpdated(Instant.now());

        playlistStatsRepository.save(myPlaylistStats);
        playlistRepository.save(correspondingPlaylist);

        return myPlaylistStats;
    }
}
