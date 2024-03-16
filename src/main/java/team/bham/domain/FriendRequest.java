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
 * Each user can send a friend request, which is related to the user\ninitiating it, and who it is for.
 */
@Schema(description = "Each user can send a friend request, which is related to the user\ninitiating it, and who it is for.")
@Entity
@Table(name = "friend_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FriendRequest implements Serializable {

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
     * Each FriendRequest is associated with a particular user, who initiated it.
     */
    @Schema(description = "Each FriendRequest is associated with a particular user, who initiated it.")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
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
    private AppUser initiatingAppUser;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
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
    private AppUser toAppUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FriendRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public FriendRequest createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public AppUser getInitiatingAppUser() {
        return this.initiatingAppUser;
    }

    public void setInitiatingAppUser(AppUser appUser) {
        this.initiatingAppUser = appUser;
    }

    public FriendRequest initiatingAppUser(AppUser appUser) {
        this.setInitiatingAppUser(appUser);
        return this;
    }

    public AppUser getToAppUser() {
        return this.toAppUser;
    }

    public void setToAppUser(AppUser appUser) {
        this.toAppUser = appUser;
    }

    public FriendRequest toAppUser(AppUser appUser) {
        this.setToAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FriendRequest)) {
            return false;
        }
        return id != null && id.equals(((FriendRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FriendRequest{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
