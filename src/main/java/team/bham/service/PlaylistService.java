package team.bham.service;

import java.util.ArrayList;
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
    private final SongRepository songRepository;
    private final SongService songService;

    public PlaylistService(PlaylistRepository playlistRepository, SongService songService, SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.songRepository = songRepository;
    }

    public Playlist createPlaylist(
        se.michaelthelin.spotify.model_objects.specification.Playlist playlist,
        ArrayList<Track> tracks,
        ArrayList<AudioFeatures> audioFeaturesList
    ) {
        // Check if playlist is already in the database
        String spotifyID = playlist.getId();
        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            // If yes: return that song
            return playlistRepository.findPlaylistBySpotifyID(spotifyID);
        }

        // Create new playlist object
        Playlist myPlaylist = new Playlist();
        myPlaylist.setSpotifyID(playlist.getId());
        myPlaylist.setName(playlist.getName());
        myPlaylist.setImageURL(playlist.getImages()[0].getUrl());
        // for each song in playlist
        if (tracks.size() != audioFeaturesList.size()) {
            throw new IllegalArgumentException(
                "tracks and audioFeaturesList are different sizes, respectively: " + tracks.size() + " " + audioFeaturesList.size()
            );
        }
        Song tmpSong;
        for (int i = 0; i < tracks.size(); i++) {
            // create song
            tmpSong = songService.createSong(tracks.get(i), audioFeaturesList.get(i));
            // link to playlist
            tmpSong.addPlaylist(myPlaylist);
            myPlaylist.addSong(tmpSong);
            songRepository.save(tmpSong);
        }
        // save playlist
        playlistRepository.save(myPlaylist);

        return myPlaylist;
    }
}
