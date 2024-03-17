package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Contains each user's feed, which is comprised of cards from different users.\nEach row is a feed, respective to the user who will see it.
 */
@Schema(
    description = "Contains each user's feed, which is comprised of cards from different users.\nEach row is a feed, respective to the user who will see it."
)
@Entity
@Table(name = "feed")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "feed", "card" }, allowSetters = true)
    private Set<FeedCard> cards = new HashSet<>();

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
    @OneToOne(mappedBy = "feed", cascade = CascadeType.ALL)
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Feed id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public Feed lastUpdated(Instant lastUpdated) {
        this.setLastUpdated(lastUpdated);
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<FeedCard> getCards() {
        return this.cards;
    }

    public void setCards(Set<FeedCard> feedCards) {
        if (this.cards != null) {
            this.cards.forEach(i -> i.setFeed(null));
        }
        if (feedCards != null) {
            feedCards.forEach(i -> i.setFeed(this));
        }
        this.cards = feedCards;
    }

    public Feed cards(Set<FeedCard> feedCards) {
        this.setCards(feedCards);
        return this;
    }

    public Feed addCard(FeedCard feedCard) {
        this.cards.add(feedCard);
        feedCard.setFeed(this);
        return this;
    }

    public Feed removeCard(FeedCard feedCard) {
        this.cards.remove(feedCard);
        feedCard.setFeed(null);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        if (this.appUser != null) {
            this.appUser.setFeed(null);
        }
        if (appUser != null) {
            appUser.setFeed(this);
        }
        this.appUser = appUser;
    }

    public Feed appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feed)) {
            return false;
        }
        return id != null && id.equals(((Feed) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Feed{" +
            "id=" + getId() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
