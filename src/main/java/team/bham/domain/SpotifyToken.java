package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SpotifyToken.
 */
@Entity
@Table(name = "spotify_token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpotifyToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @NotNull
    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @NotNull
    @Column(name = "user_scope", nullable = false)
    private String userScope;

    @NotNull
    @Column(name = "expires", nullable = false)
    private Instant expires;

    @NotNull
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

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
    @OneToOne(mappedBy = "spotifyToken")
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    /** @deprecated Prefer the new constructor which initialises required fields of the entity */
    @Deprecated
    public SpotifyToken() {}

    /** Used to establish initial required variables */
    public SpotifyToken(String accessToken, String tokenType, String userScope, Instant expires, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userScope = userScope;
        this.expires = expires;
        this.refreshToken = refreshToken;
    }

    public Long getId() {
        return this.id;
    }

    public SpotifyToken id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public SpotifyToken accessToken(String accessToken) {
        this.setAccessToken(accessToken);
        return this;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public SpotifyToken tokenType(String tokenType) {
        this.setTokenType(tokenType);
        return this;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUserScope() {
        return this.userScope;
    }

    public SpotifyToken userScope(String userScope) {
        this.setUserScope(userScope);
        return this;
    }

    public void setUserScope(String userScope) {
        this.userScope = userScope;
    }

    public Instant getExpires() {
        return this.expires;
    }

    public SpotifyToken expires(Instant expires) {
        this.setExpires(expires);
        return this;
    }

    public void setExpires(Instant expires) {
        this.expires = expires;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public SpotifyToken refreshToken(String refreshToken) {
        this.setRefreshToken(refreshToken);
        return this;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        if (this.appUser != null) {
            this.appUser.setSpotifyToken(null);
        }
        if (appUser != null) {
            appUser.setSpotifyToken(this);
        }
        this.appUser = appUser;
    }

    public SpotifyToken appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpotifyToken)) {
            return false;
        }
        return id != null && id.equals(((SpotifyToken) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpotifyToken{" +
            "id=" + getId() +
            ", accessToken='" + getAccessToken() + "'" +
            ", tokenType='" + getTokenType() + "'" +
            ", userScope='" + getUserScope() + "'" +
            ", expires='" + getExpires() + "'" +
            ", refreshToken='" + getRefreshToken() + "'" +
            "}";
    }
}
