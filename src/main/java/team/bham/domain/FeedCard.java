package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Contains each feed's cards.\nEach row is a card, respective to the user who will see the feed that the card belongs to.
 */
@Schema(
    description = "Contains each feed's cards.\nEach row is a card, respective to the user who will see the feed that the card belongs to."
)
@Entity
@Table(name = "feed_card")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeedCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "liked", nullable = false)
    private Boolean liked;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JsonIgnoreProperties(value = { "cards", "appUser" }, allowSetters = true)
    private Feed feed;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JsonIgnoreProperties(value = { "usages", "appUser" }, allowSetters = true)
    private Card card;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FeedCard id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLiked() {
        return this.liked;
    }

    public FeedCard liked(Boolean liked) {
        this.setLiked(liked);
        return this;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Feed getFeed() {
        return this.feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public FeedCard feed(Feed feed) {
        this.setFeed(feed);
        return this;
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public FeedCard card(Card card) {
        this.setCard(card);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeedCard)) {
            return false;
        }
        return id != null && id.equals(((FeedCard) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeedCard{" +
            "id=" + getId() +
            ", liked='" + getLiked() + "'" +
            "}";
    }
}
