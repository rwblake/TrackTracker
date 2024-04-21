package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PlaylistStats.
 */
@Entity
@Table(name = "playlist_stats")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlaylistStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "playlist_length")
    private Integer playlistLength;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
        value = {
            "album",
            "streams",
            "happiestPlaylistStats",
            "energeticPlaylistStats",
            "sumsUpPlaylistStats",
            "anomalousPlaylistStats",
            "playlists",
            "artists",
        },
        allowSetters = true
    )
    private Song happiestSong;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
        value = {
            "album",
            "streams",
            "happiestPlaylistStats",
            "energeticPlaylistStats",
            "sumsUpPlaylistStats",
            "anomalousPlaylistStats",
            "playlists",
            "artists",
        },
        allowSetters = true
    )
    private Song energeticSong;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
        value = {
            "album",
            "streams",
            "happiestPlaylistStats",
            "energeticPlaylistStats",
            "sumsUpPlaylistStats",
            "anomalousPlaylistStats",
            "playlists",
            "artists",
        },
        allowSetters = true
    )
    private Song sumsUpSong;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
        value = {
            "album",
            "streams",
            "happiestPlaylistStats",
            "energeticPlaylistStats",
            "sumsUpPlaylistStats",
            "anomalousPlaylistStats",
            "playlists",
            "artists",
        },
        allowSetters = true
    )
    private Song anonmalousSong;

    @JsonIgnoreProperties(value = { "playlistStats", "songs", "appUser" }, allowSetters = true)
    @OneToOne(mappedBy = "playlistStats")
    private Playlist playlist;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlaylistStats id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlaylistLength() {
        return this.playlistLength;
    }

    public PlaylistStats playlistLength(Integer playlistLength) {
        this.setPlaylistLength(playlistLength);
        return this;
    }

    public void setPlaylistLength(Integer playlistLength) {
        this.playlistLength = playlistLength;
    }

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public PlaylistStats lastUpdated(Instant lastUpdated) {
        this.setLastUpdated(lastUpdated);
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Song getHappiestSong() {
        return this.happiestSong;
    }

    public void setHappiestSong(Song song) {
        this.happiestSong = song;
    }

    public PlaylistStats happiestSong(Song song) {
        this.setHappiestSong(song);
        return this;
    }

    public Song getEnergeticSong() {
        return this.energeticSong;
    }

    public void setEnergeticSong(Song song) {
        this.energeticSong = song;
    }

    public PlaylistStats energeticSong(Song song) {
        this.setEnergeticSong(song);
        return this;
    }

    public Song getSumsUpSong() {
        return this.sumsUpSong;
    }

    public void setSumsUpSong(Song song) {
        this.sumsUpSong = song;
    }

    public PlaylistStats sumsUpSong(Song song) {
        this.setSumsUpSong(song);
        return this;
    }

    public Song getAnonmalousSong() {
        return this.anonmalousSong;
    }

    public void setAnonmalousSong(Song song) {
        this.anonmalousSong = song;
    }

    public PlaylistStats anonmalousSong(Song song) {
        this.setAnonmalousSong(song);
        return this;
    }

    public Playlist getPlaylist() {
        return this.playlist;
    }

    public void setPlaylist(Playlist playlist) {
        if (this.playlist != null) {
            this.playlist.setPlaylistStats(null);
        }
        if (playlist != null) {
            playlist.setPlaylistStats(this);
        }
        this.playlist = playlist;
    }

    public PlaylistStats playlist(Playlist playlist) {
        this.setPlaylist(playlist);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlaylistStats)) {
            return false;
        }
        return id != null && id.equals(((PlaylistStats) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlaylistStats{" +
            "id=" + getId() +
            ", playlistLength=" + getPlaylistLength() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
