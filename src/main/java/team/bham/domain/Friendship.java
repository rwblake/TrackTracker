package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Each Friendship is related to two users. Friendships are setup by backend\ncode when FriendRequests are accepted.
 */
@Schema(description = "Each Friendship is related to two users. Friendships are setup by backend\ncode when FriendRequests are accepted.")
@Entity
@Table(name = "friendship")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Friendship implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Friendships relate to two AppUsers: initiating, and accepting users
     */
    @Schema(description = "Friendships relate to two AppUsers: initiating, and accepting users")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "internalUser",
            "userPreferences",
            "spotifyToken",
            "feed",
            "playlists",
            "streams",
            "cards",
            "cardTemplates",
            "blockedUsers",
            "aboutFriendRecommendations",
            "forFriendRecommendations",
            "intitiatingFriendRequests",
            "toFriendRequests",
            "friendshipInitiateds",
            "friendshipAccepteds",
            "blockedByUsers",
        },
        allowSetters = true
    )
    private AppUser friendInitiating;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "internalUser",
            "userPreferences",
            "spotifyToken",
            "feed",
            "playlists",
            "streams",
            "cards",
            "cardTemplates",
            "blockedUsers",
            "aboutFriendRecommendations",
            "forFriendRecommendations",
            "intitiatingFriendRequests",
            "toFriendRequests",
            "friendshipInitiateds",
            "friendshipAccepteds",
            "blockedByUsers",
        },
        allowSetters = true
    )
    private AppUser friendAccepting;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Friendship id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Friendship createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public AppUser getFriendInitiating() {
        return this.friendInitiating;
    }

    public void setFriendInitiating(AppUser appUser) {
        this.friendInitiating = appUser;
    }

    public Friendship friendInitiating(AppUser appUser) {
        this.setFriendInitiating(appUser);
        return this;
    }

    public AppUser getFriendAccepting() {
        return this.friendAccepting;
    }

    public void setFriendAccepting(AppUser appUser) {
        this.friendAccepting = appUser;
    }

    public Friendship friendAccepting(AppUser appUser) {
        this.setFriendAccepting(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friendship)) {
            return false;
        }
        return id != null && id.equals(((Friendship) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Friendship{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
