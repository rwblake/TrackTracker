package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.CardType;

/**
 * Contains each user's cards (updates from yours and your friend's listening habits as well as general updates).\nThese will be accessed when generating user feeds. Each row is a Card, respective to the user on which it's based.
 */
@Schema(
    description = "Contains each user's cards (updates from yours and your friend's listening habits as well as general updates).\nThese will be accessed when generating user feeds. Each row is a Card, respective to the user on which it's based."
)
@Entity
@Table(name = "card")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metric", nullable = false)
    private CardType metric;

    @Column(name = "time_frame")
    private Duration timeFrame;

    @NotNull
    @Column(name = "metric_value", nullable = false)
    private Integer metricValue;

    @NotNull
    @Column(name = "time_generated", nullable = false)
    private Instant timeGenerated;

    @OneToMany(mappedBy = "card")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "feed", "card" }, allowSetters = true)
    private Set<FeedCard> usages = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
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
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Card id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CardType getMetric() {
        return this.metric;
    }

    public Card metric(CardType metric) {
        this.setMetric(metric);
        return this;
    }

    public void setMetric(CardType metric) {
        this.metric = metric;
    }

    public Duration getTimeFrame() {
        return this.timeFrame;
    }

    public Card timeFrame(Duration timeFrame) {
        this.setTimeFrame(timeFrame);
        return this;
    }

    public void setTimeFrame(Duration timeFrame) {
        this.timeFrame = timeFrame;
    }

    public Integer getMetricValue() {
        return this.metricValue;
    }

    public Card metricValue(Integer metricValue) {
        this.setMetricValue(metricValue);
        return this;
    }

    public void setMetricValue(Integer metricValue) {
        this.metricValue = metricValue;
    }

    public Instant getTimeGenerated() {
        return this.timeGenerated;
    }

    public Card timeGenerated(Instant timeGenerated) {
        this.setTimeGenerated(timeGenerated);
        return this;
    }

    public void setTimeGenerated(Instant timeGenerated) {
        this.timeGenerated = timeGenerated;
    }

    public Set<FeedCard> getUsages() {
        return this.usages;
    }

    public void setUsages(Set<FeedCard> feedCards) {
        if (this.usages != null) {
            this.usages.forEach(i -> i.setCard(null));
        }
        if (feedCards != null) {
            feedCards.forEach(i -> i.setCard(this));
        }
        this.usages = feedCards;
    }

    public Card usages(Set<FeedCard> feedCards) {
        this.setUsages(feedCards);
        return this;
    }

    public Card addUsage(FeedCard feedCard) {
        this.usages.add(feedCard);
        feedCard.setCard(this);
        return this;
    }

    public Card removeUsage(FeedCard feedCard) {
        this.usages.remove(feedCard);
        feedCard.setCard(null);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Card appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        return id != null && id.equals(((Card) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Card{" +
            "id=" + getId() +
            ", metric='" + getMetric() + "'" +
            ", timeFrame='" + getTimeFrame() + "'" +
            ", metricValue=" + getMetricValue() +
            ", timeGenerated='" + getTimeGenerated() + "'" +
            "}";
    }
}
