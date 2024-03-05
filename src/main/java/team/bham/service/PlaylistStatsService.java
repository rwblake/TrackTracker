package team.bham.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import team.bham.domain.Playlist;
import team.bham.domain.PlaylistStats;
import team.bham.repository.PlaylistStatsRepository;
import team.bham.repository.SongRepository;
import team.bham.service.spotify.PlaylistInsightCalculator;
import team.bham.service.spotify.SimpleSong;

@Service
public class PlaylistStatsService {

    private final PlaylistStatsRepository playlistStatsRepository;
    private final SongRepository songRepository;

    public PlaylistStatsService(PlaylistStatsRepository playlistStatsRepository, SongRepository songRepository) {
        this.playlistStatsRepository = playlistStatsRepository;
        this.songRepository = songRepository;
    }

    public PlaylistStats createPlaylistStats(Playlist playlist) {
        // Check if playlist stats have already been generated
        PlaylistStats myPlaylistStats = playlist.getPlaylistStats();
        if (myPlaylistStats != null) {
            return myPlaylistStats;
        }

        // Create new playlist stats object
        myPlaylistStats = new PlaylistStats();

        // Calculate stats
        PlaylistInsightCalculator.AllFeaturesStats stats = new PlaylistInsightCalculator.AllFeaturesStats(playlist);
        SimpleSong[] highlightedSongs = PlaylistInsightCalculator.selectHighlightedSongs(playlist, stats);

        // Set selected songs
        myPlaylistStats.setHappiestSong(songRepository.findSongBySpotifyID(highlightedSongs[0].songSpotifyID));
        myPlaylistStats.setEnergeticSong(songRepository.findSongBySpotifyID(highlightedSongs[1].songSpotifyID));
        myPlaylistStats.setSumsUpSong(songRepository.findSongBySpotifyID(highlightedSongs[2].songSpotifyID));
        myPlaylistStats.setAnonmalousSong(songRepository.findSongBySpotifyID(highlightedSongs[3].songSpotifyID));

        // Update playlist with stats, and vice versa
        myPlaylistStats.setPlaylist(playlist);
        myPlaylistStats.setPlaylistLength(playlist.getSongs().size());
        myPlaylistStats.setLastUpdated(Instant.now());

        playlistStatsRepository.save(myPlaylistStats);

        return myPlaylistStats;
    }
}
