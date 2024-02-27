package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Song.
 */
@Entity
@Table(name = "song")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "spotify_id", nullable = false, unique = true)
    private String spotifyID;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "release_date")
    private Instant releaseDate;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Duration duration;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "acousticness")
    private Float acousticness;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "danceability")
    private Float danceability;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "energy")
    private Float energy;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "instrumentalness")
    private Float instrumentalness;

    @Column(name = "musical_key")
    private Integer musicalKey;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "liveness")
    private Float liveness;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "loudness")
    private Float loudness;

    @Column(name = "mode")
    private Boolean mode;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "speechiness")
    private Float speechiness;

    @Column(name = "tempo")
    private Float tempo;

    @Column(name = "time_signature")
    private Integer timeSignature;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "valence")
    private Float valence;

    @ManyToOne
    @JsonIgnoreProperties(value = { "artists" }, allowSetters = true)
    private Album album;

    @OneToMany(mappedBy = "song")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "song", "appUser" }, allowSetters = true)
    private Set<Stream> streams = new HashSet<>();

    @OneToMany(mappedBy = "happiestSong")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "happiestSong", "fastestSong", "sumsUpSong", "anonmalousSong", "playlist" }, allowSetters = true)
    private Set<PlaylistStats> happiestPlaylistStats = new HashSet<>();

    @OneToMany(mappedBy = "fastestSong")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "happiestSong", "fastestSong", "sumsUpSong", "anonmalousSong", "playlist" }, allowSetters = true)
    private Set<PlaylistStats> fastestPlaylistStats = new HashSet<>();

    @OneToMany(mappedBy = "sumsUpSong")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "happiestSong", "fastestSong", "sumsUpSong", "anonmalousSong", "playlist" }, allowSetters = true)
    private Set<PlaylistStats> sumsUpPlaylistStats = new HashSet<>();

    @OneToMany(mappedBy = "anonmalousSong")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "happiestSong", "fastestSong", "sumsUpSong", "anonmalousSong", "playlist" }, allowSetters = true)
    private Set<PlaylistStats> anomalousPlaylistStats = new HashSet<>();

    @ManyToMany(mappedBy = "songs")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "playlistStats", "songs", "appUser" }, allowSetters = true)
    private Set<Playlist> playlists = new HashSet<>();

    @ManyToMany(mappedBy = "songs")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "songs", "albums", "genres" }, allowSetters = true)
    private Set<Artist> artists = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Song id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return this.spotifyID;
    }

    public Song spotifyID(String spotifyID) {
        this.setSpotifyID(spotifyID);
        return this;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return this.name;
    }

    public Song name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Song imageURL(String imageURL) {
        this.setImageURL(imageURL);
        return this;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Instant getReleaseDate() {
        return this.releaseDate;
    }

    public Song releaseDate(Instant releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Song duration(Duration duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Float getAcousticness() {
        return this.acousticness;
    }

    public Song acousticness(Float acousticness) {
        this.setAcousticness(acousticness);
        return this;
    }

    public void setAcousticness(Float acousticness) {
        this.acousticness = acousticness;
    }

    public Float getDanceability() {
        return this.danceability;
    }

    public Song danceability(Float danceability) {
        this.setDanceability(danceability);
        return this;
    }

    public void setDanceability(Float danceability) {
        this.danceability = danceability;
    }

    public Float getEnergy() {
        return this.energy;
    }

    public Song energy(Float energy) {
        this.setEnergy(energy);
        return this;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

    public Float getInstrumentalness() {
        return this.instrumentalness;
    }

    public Song instrumentalness(Float instrumentalness) {
        this.setInstrumentalness(instrumentalness);
        return this;
    }

    public void setInstrumentalness(Float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public Integer getMusicalKey() {
        return this.musicalKey;
    }

    public Song musicalKey(Integer musicalKey) {
        this.setMusicalKey(musicalKey);
        return this;
    }

    public void setMusicalKey(Integer musicalKey) {
        this.musicalKey = musicalKey;
    }

    public Float getLiveness() {
        return this.liveness;
    }

    public Song liveness(Float liveness) {
        this.setLiveness(liveness);
        return this;
    }

    public void setLiveness(Float liveness) {
        this.liveness = liveness;
    }

    public Float getLoudness() {
        return this.loudness;
    }

    public Song loudness(Float loudness) {
        this.setLoudness(loudness);
        return this;
    }

    public void setLoudness(Float loudness) {
        this.loudness = loudness;
    }

    public Boolean getMode() {
        return this.mode;
    }

    public Song mode(Boolean mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(Boolean mode) {
        this.mode = mode;
    }

    public Float getSpeechiness() {
        return this.speechiness;
    }

    public Song speechiness(Float speechiness) {
        this.setSpeechiness(speechiness);
        return this;
    }

    public void setSpeechiness(Float speechiness) {
        this.speechiness = speechiness;
    }

    public Float getTempo() {
        return this.tempo;
    }

    public Song tempo(Float tempo) {
        this.setTempo(tempo);
        return this;
    }

    public void setTempo(Float tempo) {
        this.tempo = tempo;
    }

    public Integer getTimeSignature() {
        return this.timeSignature;
    }

    public Song timeSignature(Integer timeSignature) {
        this.setTimeSignature(timeSignature);
        return this;
    }

    public void setTimeSignature(Integer timeSignature) {
        this.timeSignature = timeSignature;
    }

    public Float getValence() {
        return this.valence;
    }

    public Song valence(Float valence) {
        this.setValence(valence);
        return this;
    }

    public void setValence(Float valence) {
        this.valence = valence;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Song album(Album album) {
        this.setAlbum(album);
        return this;
    }

    public Set<Stream> getStreams() {
        return this.streams;
    }

    public void setStreams(Set<Stream> streams) {
        if (this.streams != null) {
            this.streams.forEach(i -> i.setSong(null));
        }
        if (streams != null) {
            streams.forEach(i -> i.setSong(this));
        }
        this.streams = streams;
    }

    public Song streams(Set<Stream> streams) {
        this.setStreams(streams);
        return this;
    }

    public Song addStream(Stream stream) {
        this.streams.add(stream);
        stream.setSong(this);
        return this;
    }

    public Song removeStream(Stream stream) {
        this.streams.remove(stream);
        stream.setSong(null);
        return this;
    }

    public Set<PlaylistStats> getHappiestPlaylistStats() {
        return this.happiestPlaylistStats;
    }

    public void setHappiestPlaylistStats(Set<PlaylistStats> playlistStats) {
        if (this.happiestPlaylistStats != null) {
            this.happiestPlaylistStats.forEach(i -> i.setHappiestSong(null));
        }
        if (playlistStats != null) {
            playlistStats.forEach(i -> i.setHappiestSong(this));
        }
        this.happiestPlaylistStats = playlistStats;
    }

    public Song happiestPlaylistStats(Set<PlaylistStats> playlistStats) {
        this.setHappiestPlaylistStats(playlistStats);
        return this;
    }

    public Song addHappiestPlaylistStats(PlaylistStats playlistStats) {
        this.happiestPlaylistStats.add(playlistStats);
        playlistStats.setHappiestSong(this);
        return this;
    }

    public Song removeHappiestPlaylistStats(PlaylistStats playlistStats) {
        this.happiestPlaylistStats.remove(playlistStats);
        playlistStats.setHappiestSong(null);
        return this;
    }

    public Set<PlaylistStats> getFastestPlaylistStats() {
        return this.fastestPlaylistStats;
    }

    public void setFastestPlaylistStats(Set<PlaylistStats> playlistStats) {
        if (this.fastestPlaylistStats != null) {
            this.fastestPlaylistStats.forEach(i -> i.setFastestSong(null));
        }
        if (playlistStats != null) {
            playlistStats.forEach(i -> i.setFastestSong(this));
        }
        this.fastestPlaylistStats = playlistStats;
    }

    public Song fastestPlaylistStats(Set<PlaylistStats> playlistStats) {
        this.setFastestPlaylistStats(playlistStats);
        return this;
    }

    public Song addFastestPlaylistStats(PlaylistStats playlistStats) {
        this.fastestPlaylistStats.add(playlistStats);
        playlistStats.setFastestSong(this);
        return this;
    }

    public Song removeFastestPlaylistStats(PlaylistStats playlistStats) {
        this.fastestPlaylistStats.remove(playlistStats);
        playlistStats.setFastestSong(null);
        return this;
    }

    public Set<PlaylistStats> getSumsUpPlaylistStats() {
        return this.sumsUpPlaylistStats;
    }

    public void setSumsUpPlaylistStats(Set<PlaylistStats> playlistStats) {
        if (this.sumsUpPlaylistStats != null) {
            this.sumsUpPlaylistStats.forEach(i -> i.setSumsUpSong(null));
        }
        if (playlistStats != null) {
            playlistStats.forEach(i -> i.setSumsUpSong(this));
        }
        this.sumsUpPlaylistStats = playlistStats;
    }

    public Song sumsUpPlaylistStats(Set<PlaylistStats> playlistStats) {
        this.setSumsUpPlaylistStats(playlistStats);
        return this;
    }

    public Song addSumsUpPlaylistStats(PlaylistStats playlistStats) {
        this.sumsUpPlaylistStats.add(playlistStats);
        playlistStats.setSumsUpSong(this);
        return this;
    }

    public Song removeSumsUpPlaylistStats(PlaylistStats playlistStats) {
        this.sumsUpPlaylistStats.remove(playlistStats);
        playlistStats.setSumsUpSong(null);
        return this;
    }

    public Set<PlaylistStats> getAnomalousPlaylistStats() {
        return this.anomalousPlaylistStats;
    }

    public void setAnomalousPlaylistStats(Set<PlaylistStats> playlistStats) {
        if (this.anomalousPlaylistStats != null) {
            this.anomalousPlaylistStats.forEach(i -> i.setAnonmalousSong(null));
        }
        if (playlistStats != null) {
            playlistStats.forEach(i -> i.setAnonmalousSong(this));
        }
        this.anomalousPlaylistStats = playlistStats;
    }

    public Song anomalousPlaylistStats(Set<PlaylistStats> playlistStats) {
        this.setAnomalousPlaylistStats(playlistStats);
        return this;
    }

    public Song addAnomalousPlaylistStats(PlaylistStats playlistStats) {
        this.anomalousPlaylistStats.add(playlistStats);
        playlistStats.setAnonmalousSong(this);
        return this;
    }

    public Song removeAnomalousPlaylistStats(PlaylistStats playlistStats) {
        this.anomalousPlaylistStats.remove(playlistStats);
        playlistStats.setAnonmalousSong(null);
        return this;
    }

    public Set<Playlist> getPlaylists() {
        return this.playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        if (this.playlists != null) {
            this.playlists.forEach(i -> i.removeSong(this));
        }
        if (playlists != null) {
            playlists.forEach(i -> i.addSong(this));
        }
        this.playlists = playlists;
    }

    public Song playlists(Set<Playlist> playlists) {
        this.setPlaylists(playlists);
        return this;
    }

    public Song addPlaylist(Playlist playlist) {
        this.playlists.add(playlist);
        playlist.getSongs().add(this);
        return this;
    }

    public Song removePlaylist(Playlist playlist) {
        this.playlists.remove(playlist);
        playlist.getSongs().remove(this);
        return this;
    }

    public Set<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(Set<Artist> artists) {
        if (this.artists != null) {
            this.artists.forEach(i -> i.removeSong(this));
        }
        if (artists != null) {
            artists.forEach(i -> i.addSong(this));
        }
        this.artists = artists;
    }

    public Song artists(Set<Artist> artists) {
        this.setArtists(artists);
        return this;
    }

    public Song addArtist(Artist artist) {
        this.artists.add(artist);
        artist.getSongs().add(this);
        return this;
    }

    public Song removeArtist(Artist artist) {
        this.artists.remove(artist);
        artist.getSongs().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Song)) {
            return false;
        }
        return id != null && id.equals(((Song) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Song{" +
            "id=" + getId() +
            ", spotifyID='" + getSpotifyID() + "'" +
            ", name='" + getName() + "'" +
            ", imageURL='" + getImageURL() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", duration='" + getDuration() + "'" +
            ", acousticness=" + getAcousticness() +
            ", danceability=" + getDanceability() +
            ", energy=" + getEnergy() +
            ", instrumentalness=" + getInstrumentalness() +
            ", musicalKey=" + getMusicalKey() +
            ", liveness=" + getLiveness() +
            ", loudness=" + getLoudness() +
            ", mode='" + getMode() + "'" +
            ", speechiness=" + getSpeechiness() +
            ", tempo=" + getTempo() +
            ", timeSignature=" + getTimeSignature() +
            ", valence=" + getValence() +
            "}";
    }
}
