package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The system creates friend recommendations based on how similar two\nAppUsers' listening statistics are. The timestamp allows them to be\nrecalculated when statistics are recalculated.
 */
@Schema(
    description = "The system creates friend recommendations based on how similar two\nAppUsers' listening statistics are. The timestamp allows them to be\nrecalculated when statistics are recalculated."
)
@Entity
@Table(name = "friend_recommendation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FriendRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "similarity", nullable = false)
    private Float similarity;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    /**
     * Each FriendRecommendation relates to the AppUser recommended.
     */
    @Schema(description = "Each FriendRecommendation relates to the AppUser recommended.")
    @JsonIgnoreProperties(
        value = {
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
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private AppUser aboutAppUser;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
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
    private AppUser forAppUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FriendRecommendation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getSimilarity() {
        return this.similarity;
    }

    public FriendRecommendation similarity(Float similarity) {
        this.setSimilarity(similarity);
        return this;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    public FriendRecommendation createdAt(LocalDate createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public AppUser getAboutAppUser() {
        return this.aboutAppUser;
    }

    public void setAboutAppUser(AppUser appUser) {
        this.aboutAppUser = appUser;
    }

    public FriendRecommendation aboutAppUser(AppUser appUser) {
        this.setAboutAppUser(appUser);
        return this;
    }

    public AppUser getForAppUser() {
        return this.forAppUser;
    }

    public void setForAppUser(AppUser appUser) {
        this.forAppUser = appUser;
    }

    public FriendRecommendation forAppUser(AppUser appUser) {
        this.setForAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FriendRecommendation)) {
            return false;
        }
        return id != null && id.equals(((FriendRecommendation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FriendRecommendation{" +
            "id=" + getId() +
            ", similarity=" + getSimilarity() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
