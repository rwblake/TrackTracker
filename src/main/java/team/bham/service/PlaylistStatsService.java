package team.bham.service;

import java.time.Instant;
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

    public PlaylistStatsService(
        PlaylistStatsRepository playlistStatsRepository,
        SongService songService,
        SongRepository songRepository,
        PlaylistRepository playlistRepository
    ) {
        this.playlistStatsRepository = playlistStatsRepository;
        this.songService = songService;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
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
            if (correspondingPlaylist.getPlaylistStats() != null) {
                return correspondingPlaylist.getPlaylistStats();
            }
        } else {
            throw new IllegalArgumentException("Playlist must be generated before stats!");
        }

        // Create new playlist stats object
        PlaylistStats myPlaylistStats = new PlaylistStats();
        if (selectedTracks.length != 4 || selectedAudioFeatures.length != 4) {
            throw new IllegalArgumentException("tracks and audio feature arrays must have size 4");
        }

        // Set selected songs
        Song happiestSong = songService.createSong(selectedTracks[0], selectedAudioFeatures[0]);
        happiestSong.addHappiestPlaylistStats(myPlaylistStats);
        myPlaylistStats.setHappiestSong(happiestSong);
        songRepository.save(happiestSong);

        Song energeticSong = songService.createSong(selectedTracks[1], selectedAudioFeatures[1]);
        energeticSong.addHappiestPlaylistStats(myPlaylistStats);
        myPlaylistStats.setHappiestSong(energeticSong);
        songRepository.save(happiestSong);

        Song sumsUpSong = songService.createSong(selectedTracks[2], selectedAudioFeatures[2]);
        sumsUpSong.addHappiestPlaylistStats(myPlaylistStats);
        myPlaylistStats.setHappiestSong(sumsUpSong);
        songRepository.save(happiestSong);

        Song anomalousSong = songService.createSong(selectedTracks[3], selectedAudioFeatures[3]);
        anomalousSong.addHappiestPlaylistStats(myPlaylistStats);
        myPlaylistStats.setHappiestSong(anomalousSong);
        songRepository.save(happiestSong);

        // Update playlist with stats, and vice versa
        myPlaylistStats.setPlaylist(correspondingPlaylist);
        myPlaylistStats.setPlaylistLength(playlist.getTracks().getTotal());
        myPlaylistStats.setLastUpdated(Instant.now());

        playlistRepository.save(correspondingPlaylist);
        playlistStatsRepository.save(myPlaylistStats);

        return myPlaylistStats;
    }
}
