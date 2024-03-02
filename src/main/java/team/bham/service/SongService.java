package team.bham.service;

import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.enums.Modality;
import se.michaelthelin.spotify.enums.ReleaseDatePrecision;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.domain.Album;
import team.bham.domain.Artist;
//import team.bham.domain.Genre;
import team.bham.domain.Song;
import team.bham.domain.enumeration.AlbumType;
import team.bham.repository.AlbumRepository;
import team.bham.repository.ArtistRepository;
//import team.bham.repository.GenreRepository;
import team.bham.repository.SongRepository;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    //private final GenreRepository genreRepository;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository, AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        //this.genreRepository = genreRepository;
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
        // Convert into required ISO-8601 format
        ReleaseDatePrecision precision = album.getReleaseDatePrecision();
        String releaseDate = album.getReleaseDate();
        switch (precision) {
            case YEAR:
                releaseDate = releaseDate + "-01";
            case MONTH:
                releaseDate = releaseDate + "-01";
            case DAY:
                releaseDate = releaseDate + "T00:00:00Z";
        }
        myAlbum.setReleaseDate(Instant.parse(releaseDate));

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
        // CAN'T GET GENRES or ARTIST IMAGE WITH SIMPLIFIEDARTIST OBJECT
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
        // Convert into required ISO-8601 format
        ReleaseDatePrecision precision = track.getAlbum().getReleaseDatePrecision();
        String releaseDate = track.getAlbum().getReleaseDate();
        switch (precision) {
            case YEAR:
                releaseDate = releaseDate + "-01";
            case MONTH:
                releaseDate = releaseDate + "-01";
            case DAY:
                releaseDate = releaseDate + "T00:00:00Z";
        }
        song.setReleaseDate(Instant.parse(releaseDate));

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
        // create genres
        // CAN'T GET GENRES WITH SIMPLIFIED ARTIST OBJECTS
        // create artists
        Artist tmpArtist;
        for (ArtistSimplified artist : track.getArtists()) {
            tmpArtist = createArtist(artist);
            artistRepository.save(tmpArtist);
            //tmpArtist.addSong(song);
            song.addArtist(tmpArtist);
            //tmpArtist.addAlbum(album);
            album.addArtist(tmpArtist);
        }
        // save objects to database
        this.albumRepository.save(album);
        this.songRepository.save(song);
        // return that song
        return song;
    }
}
