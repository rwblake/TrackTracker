package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.AlbumType;

/**
 * A Album.
 */
@Entity
@Table(name = "album")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Album implements Serializable {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "album_type")
    private AlbumType albumType;

    @OneToMany(mappedBy = "album")
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

    @ManyToMany(mappedBy = "albums")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "songs", "albums", "genres" }, allowSetters = true)
    private Set<Artist> artists = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Album id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return this.spotifyID;
    }

    public Album spotifyID(String spotifyID) {
        this.setSpotifyID(spotifyID);
        return this;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return this.name;
    }

    public Album name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Album imageURL(String imageURL) {
        this.setImageURL(imageURL);
        return this;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Instant getReleaseDate() {
        return this.releaseDate;
    }

    public Album releaseDate(Instant releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }

    public AlbumType getAlbumType() {
        return this.albumType;
    }

    public Album albumType(AlbumType albumType) {
        this.setAlbumType(albumType);
        return this;
    }

    public void setAlbumType(AlbumType albumType) {
        this.albumType = albumType;
    }

    public Set<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(Set<Song> songs) {
        if (this.songs != null) {
            this.songs.forEach(i -> i.setAlbum(null));
        }
        if (songs != null) {
            songs.forEach(i -> i.setAlbum(this));
        }
        this.songs = songs;
    }

    public Album songs(Set<Song> songs) {
        this.setSongs(songs);
        return this;
    }

    public Album addSong(Song song) {
        this.songs.add(song);
        song.setAlbum(this);
        return this;
    }

    public Album removeSong(Song song) {
        this.songs.remove(song);
        song.setAlbum(null);
        return this;
    }

    public Set<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(Set<Artist> artists) {
        if (this.artists != null) {
            this.artists.forEach(i -> i.removeAlbum(this));
        }
        if (artists != null) {
            artists.forEach(i -> i.addAlbum(this));
        }
        this.artists = artists;
    }

    public Album artists(Set<Artist> artists) {
        this.setArtists(artists);
        return this;
    }

    public Album addArtist(Artist artist) {
        this.artists.add(artist);
        artist.getAlbums().add(this);
        return this;
    }

    public Album removeArtist(Artist artist) {
        this.artists.remove(artist);
        artist.getAlbums().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }
        return id != null && id.equals(((Album) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Album{" +
            "id=" + getId() +
            ", spotifyID='" + getSpotifyID() + "'" +
            ", name='" + getName() + "'" +
            ", imageURL='" + getImageURL() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", albumType='" + getAlbumType() + "'" +
            "}";
    }
}
