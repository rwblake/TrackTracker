package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.Color;
import team.bham.domain.enumeration.Font;
import team.bham.domain.enumeration.Layout;

/**
 * A CardTemplate.
 */
@Entity
@Table(name = "card_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CardTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout")
    private Layout layout;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "font")
    private Font font;

    @OneToMany(mappedBy = "cardTemplate")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cardTemplate" }, allowSetters = true)
    private Set<CardMetric> metrics = new HashSet<>();

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
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CardTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Color getColor() {
        return this.color;
    }

    public CardTemplate color(Color color) {
        this.setColor(color);
        return this;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public CardTemplate layout(Layout layout) {
        this.setLayout(layout);
        return this;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public String getName() {
        return this.name;
    }

    public CardTemplate name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Font getFont() {
        return this.font;
    }

    public CardTemplate font(Font font) {
        this.setFont(font);
        return this;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Set<CardMetric> getMetrics() {
        return this.metrics;
    }

    public void setMetrics(Set<CardMetric> cardMetrics) {
        if (this.metrics != null) {
            this.metrics.forEach(i -> i.setCardTemplate(null));
        }
        if (cardMetrics != null) {
            cardMetrics.forEach(i -> i.setCardTemplate(this));
        }
        this.metrics = cardMetrics;
    }

    public CardTemplate metrics(Set<CardMetric> cardMetrics) {
        this.setMetrics(cardMetrics);
        return this;
    }

    public CardTemplate addMetric(CardMetric cardMetric) {
        this.metrics.add(cardMetric);
        cardMetric.setCardTemplate(this);
        return this;
    }

    public CardTemplate removeMetric(CardMetric cardMetric) {
        this.metrics.remove(cardMetric);
        cardMetric.setCardTemplate(null);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public CardTemplate appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardTemplate)) {
            return false;
        }
        return id != null && id.equals(((CardTemplate) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CardTemplate{" +
            "id=" + getId() +
            ", color='" + getColor() + "'" +
            ", layout='" + getLayout() + "'" +
            ", name='" + getName() + "'" +
            ", font='" + getFont() + "'" +
            "}";
    }
}
