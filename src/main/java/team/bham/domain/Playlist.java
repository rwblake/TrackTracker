package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Playlist.
 */
@Entity
@Table(name = "playlist")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Playlist implements Serializable {

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

    @JsonIgnoreProperties(value = { "happiestSong", "energeticSong", "sumsUpSong", "anonmalousSong", "playlist" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private PlaylistStats playlistStats;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "rel_playlist__song",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Song> songs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "internalUser",
            "userPreferences",
            "spotifyToken",
            "feed",
            "friends",
            "toFriendRequests",
            "forFriendRecommendations",
            "blockedUsers",
            "playlists",
            "streams",
            "cards",
            "cardTemplates",
            "aboutFriendRecommendation",
            "intitiatingFriendRequest",
            "friendshipInitiated",
            "friendshipAccepted",
            "blockedByUser",
        },
        allowSetters = true
    )
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Playlist id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return this.spotifyID;
    }

    public Playlist spotifyID(String spotifyID) {
        this.setSpotifyID(spotifyID);
        return this;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return this.name;
    }

    public Playlist name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Playlist imageURL(String imageURL) {
        this.setImageURL(imageURL);
        return this;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public PlaylistStats getPlaylistStats() {
        return this.playlistStats;
    }

    public void setPlaylistStats(PlaylistStats playlistStats) {
        this.playlistStats = playlistStats;
    }

    public Playlist playlistStats(PlaylistStats playlistStats) {
        this.setPlaylistStats(playlistStats);
        return this;
    }

    public Set<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public Playlist songs(Set<Song> songs) {
        this.setSongs(songs);
        return this;
    }

    public Playlist addSong(Song song) {
        this.songs.add(song);
        song.getPlaylists().add(this);
        return this;
    }

    public Playlist removeSong(Song song) {
        this.songs.remove(song);
        song.getPlaylists().remove(this);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Playlist appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Playlist)) {
            return false;
        }
        return id != null && id.equals(((Playlist) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Playlist{" +
            "id=" + getId() +
            ", spotifyID='" + getSpotifyID() + "'" +
            ", name='" + getName() + "'" +
            ", imageURL='" + getImageURL() + "'" +
            "}";
    }
}
