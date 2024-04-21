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
 * A Artist.
 */
@Entity
@Table(name = "artist")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Artist implements Serializable {

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "rel_artist__song", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "rel_artist__album",
        joinColumns = @JoinColumn(name = "artist_id"),
        inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "songs", "artists" }, allowSetters = true)
    private Set<Album> albums = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "rel_artist__genre",
        joinColumns = @JoinColumn(name = "artist_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "artists" }, allowSetters = true)
    private Set<Genre> genres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Artist id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return this.spotifyID;
    }

    public Artist spotifyID(String spotifyID) {
        this.setSpotifyID(spotifyID);
        return this;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return this.name;
    }

    public Artist name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Artist imageURL(String imageURL) {
        this.setImageURL(imageURL);
        return this;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Set<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public Artist songs(Set<Song> songs) {
        this.setSongs(songs);
        return this;
    }

    public Artist addSong(Song song) {
        this.songs.add(song);
        song.getArtists().add(this);
        return this;
    }

    public Artist removeSong(Song song) {
        this.songs.remove(song);
        song.getArtists().remove(this);
        return this;
    }

    public Set<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public Artist albums(Set<Album> albums) {
        this.setAlbums(albums);
        return this;
    }

    public Artist addAlbum(Album album) {
        this.albums.add(album);
        album.getArtists().add(this);
        return this;
    }

    public Artist removeAlbum(Album album) {
        this.albums.remove(album);
        album.getArtists().remove(this);
        return this;
    }

    public Set<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Artist genres(Set<Genre> genres) {
        this.setGenres(genres);
        return this;
    }

    public Artist addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getArtists().add(this);
        return this;
    }

    public Artist removeGenre(Genre genre) {
        this.genres.remove(genre);
        genre.getArtists().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artist)) {
            return false;
        }
        return id != null && id.equals(((Artist) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Artist{" +
            "id=" + getId() +
            ", spotifyID='" + getSpotifyID() + "'" +
            ", name='" + getName() + "'" +
            ", imageURL='" + getImageURL() + "'" +
            "}";
    }
}
