package team.bham.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.Modality;
import se.michaelthelin.spotify.enums.ReleaseDatePrecision;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Genre;
import team.bham.domain.Song;
import team.bham.domain.enumeration.AlbumType;
import team.bham.repository.AlbumRepository;
import team.bham.repository.ArtistRepository;
import team.bham.repository.GenreRepository;
import team.bham.repository.SongRepository;
import team.bham.service.spotify.PlaylistRetriever;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    public SongService(
        SongRepository songRepository,
        ArtistRepository artistRepository,
        AlbumRepository albumRepository,
        GenreRepository genreRepository
    ) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
    }

    public List<Song> createSongs(List<Track> tracks, SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        // Create return list
        List<Song> mySongs = new ArrayList<>(tracks.size());

        // Query the songs which don't already exist in the database
        List<Track> querySongs = new ArrayList<>(tracks.size());
        for (Track track : tracks) {
            if (this.songRepository.existsBySpotifyID(track.getId())) {
                mySongs.add(this.songRepository.findSongBySpotifyID(track.getId()));
            } else {
                querySongs.add(track);
            }
        }

        if (querySongs.isEmpty()) {
            return mySongs;
        }

        // Get audio features
        List<AudioFeatures> audioFeaturesList = PlaylistRetriever.getAudioFeatures(spotifyApi, querySongs);

        // Check all audio features could be obtained
        if (querySongs.size() != audioFeaturesList.size()) {
            throw new IllegalArgumentException(
                "querySongs and audioFeaturesList are different sizes, respectively: " + querySongs.size() + " " + audioFeaturesList.size()
            );
        }

        // Create songs (EXCEPT artist genres and artist image URLs)
        Song tmpSong;
        for (int i = 0; i < querySongs.size(); i++) {
            mySongs.add(this.createSong(querySongs.get(i), audioFeaturesList.get(i)));
        }

        // Collate all artists into one array
        List<ArtistSimplified> simplifiedArtists = new ArrayList<>(querySongs.size());
        for (Track querySong : querySongs) {
            simplifiedArtists.addAll(List.of(querySong.getArtists()));
        }
        // Get non-simplified artist objects which include genres and image URLs
        se.michaelthelin.spotify.model_objects.specification.Artist[] artists = PlaylistRetriever.getArtists(spotifyApi, simplifiedArtists);
        // For each artist
        Genre myGenre;
        team.bham.domain.Artist myArtist;
        for (se.michaelthelin.spotify.model_objects.specification.Artist artist : artists) {
            // Set artist genres (by setting genre artists which cascade)
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
            // Set artist image
            myArtist = artistRepository.findArtistBySpotifyID(artist.getId());
            if (artist.getImages() != null && artist.getImages().length > 0) {
                myArtist.setImageURL(artist.getImages()[0].getUrl());
            }
            artistRepository.save(myArtist);
        }

        return mySongs;
    }

    private Instant toInstant(String releaseDate, ReleaseDatePrecision precision) {
        // Convert to ISO-8601 format
        switch (precision) {
            case YEAR:
                releaseDate = releaseDate + "-01";
            case MONTH:
                releaseDate = releaseDate + "-01";
            case DAY:
                releaseDate = releaseDate + "T00:00:00Z";
        }
        return Instant.parse(releaseDate);
    }

    private Album createAlbum(AlbumSimplified album) {
        // Check if album is already in the database
        String spotifyID = album.getId();
        if (albumRepository.existsBySpotifyID(spotifyID)) {
            // Return existing album
            return albumRepository.findAlbumBySpotifyID(spotifyID);
        }

        // Create new album object
        Album myAlbum = new Album();
        myAlbum.setSpotifyID(album.getId());
        myAlbum.setName(album.getName());
        myAlbum.setImageURL(album.getImages()[0].getUrl());
        myAlbum.setReleaseDate(toInstant(album.getReleaseDate(), album.getReleaseDatePrecision())); // Convert into required ISO-8601 format

        AlbumType myAlbumType;
        switch (album.getAlbumType()) {
            case ALBUM:
                myAlbumType = AlbumType.ALBUM;
                break;
            case SINGLE:
                myAlbumType = AlbumType.SINGLE;
                break;
            case COMPILATION:
                myAlbumType = AlbumType.COMPILATION;
                break;
            default:
                throw new IllegalStateException("Invalid album type: " + album.getAlbumType());
        }
        myAlbum.setAlbumType(myAlbumType);

        return myAlbum;
    }

    private Artist createArtist(ArtistSimplified artist) {
        // Check if album is already in the database
        String spotifyID = artist.getId();
        if (artistRepository.existsBySpotifyID(spotifyID)) {
            // Return existing album
            return artistRepository.findArtistBySpotifyID(spotifyID);
        }

        // Create the new artist object
        Artist myArtist = new Artist();
        myArtist.setSpotifyID(artist.getId());
        myArtist.setName(artist.getName());
        return myArtist;
    }

    public Song createSong(Track track, AudioFeatures audioFeatures) {
        // Check if song is already in the database
        String spotifyID = track.getId();
        if (songRepository.existsBySpotifyID(spotifyID)) {
            // If yes: return that song
            return songRepository.findSongBySpotifyID(spotifyID);
        }
        // If no:
        // create song
        // set standard fields
        Song song = new Song();

        song.setSpotifyID(track.getId());
        song.setName(track.getName());
        song.setImageURL(track.getAlbum().getImages()[0].getUrl());
        song.setReleaseDate(toInstant(track.getAlbum().getReleaseDate(), track.getAlbum().getReleaseDatePrecision())); // Convert into required ISO-8601 format
        song.setDuration(Duration.ofMillis(track.getDurationMs()));
        // set audio feature fields
        song.setAcousticness(audioFeatures.getAcousticness());
        song.setDanceability(audioFeatures.getDanceability());
        song.setEnergy(audioFeatures.getEnergy());
        song.setInstrumentalness(audioFeatures.getInstrumentalness());
        song.setMusicalKey(audioFeatures.getKey());
        song.setLiveness(audioFeatures.getLiveness());
        song.setLoudness(audioFeatures.getLoudness());
        song.setMode(audioFeatures.getMode() == Modality.MAJOR);
        song.setSpeechiness(audioFeatures.getSpeechiness());
        song.setTempo(audioFeatures.getTempo());
        song.setTimeSignature(audioFeatures.getTimeSignature());
        song.setValence(audioFeatures.getValence());
        // create album
        Album album = createAlbum(track.getAlbum());
        this.albumRepository.save(album);
        song.setAlbum(album);
        this.songRepository.save(song);
        // create artists
        Artist tmpArtist;
        for (ArtistSimplified artist : track.getArtists()) {
            tmpArtist = createArtist(artist);
            artistRepository.save(tmpArtist);

            song.addArtist(tmpArtist);
            album.addArtist(tmpArtist);
        }
        // save objects to database
        this.albumRepository.save(album);
        this.songRepository.save(song);
        // return that song
        return song;
    }
}
