package team.bham.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.domain.Playlist;
import team.bham.domain.Song;
import team.bham.repository.PlaylistRepository;
import team.bham.repository.SongRepository;

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

    public Playlist createPlaylist(
        se.michaelthelin.spotify.model_objects.specification.Playlist playlist,
        List<Track> tracks,
        List<AudioFeatures> audioFeaturesList
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

        playlistStatsService.createPlaylistStats(myPlaylist);
        playlistRepository.save(myPlaylist);

        return myPlaylist;
    }
}
