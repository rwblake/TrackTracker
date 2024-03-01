package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.CardType;
import team.bham.domain.enumeration.VisibilityPreference;

/**
 * Stores each user's changes to their sharing preferences.
 */
@Schema(description = "Stores each user's changes to their sharing preferences.")
@Entity
@Table(name = "sharing_preference")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SharingPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric")
    private CardType metric;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private VisibilityPreference visibility;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "sharingPreferences", "appUser" }, allowSetters = true)
    private UserPreferences appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SharingPreference id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CardType getMetric() {
        return this.metric;
    }

    public SharingPreference metric(CardType metric) {
        this.setMetric(metric);
        return this;
    }

    public void setMetric(CardType metric) {
        this.metric = metric;
    }

    public VisibilityPreference getVisibility() {
        return this.visibility;
    }

    public SharingPreference visibility(VisibilityPreference visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(VisibilityPreference visibility) {
        this.visibility = visibility;
    }

    public UserPreferences getAppUser() {
        return this.appUser;
    }

    public void setAppUser(UserPreferences userPreferences) {
        this.appUser = userPreferences;
    }

    public SharingPreference appUser(UserPreferences userPreferences) {
        this.setAppUser(userPreferences);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SharingPreference)) {
            return false;
        }
        return id != null && id.equals(((SharingPreference) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SharingPreference{" +
            "id=" + getId() +
            ", metric='" + getMetric() + "'" +
            ", visibility='" + getVisibility() + "'" +
            "}";
    }
}
