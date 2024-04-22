package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.VisibilityPreference;

/**
 * A UserPreferences.
 */
@Entity
@Table(name = "user_preferences")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private VisibilityPreference visibility;

    @Column(name = "is_dark_mode")
    private Boolean isDarkMode;

    @Column(name = "is_high_contrast")
    private Boolean isHighContrast;

    @Enumerated(EnumType.STRING)
    @Column(name = "playlist_privacy")
    private VisibilityPreference playlistPrivacy;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "appUser" }, allowSetters = true)
    private Set<SharingPreference> sharingPreferences = new HashSet<>();

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
    @OneToOne(mappedBy = "userPreferences", cascade = CascadeType.ALL)
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserPreferences id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VisibilityPreference getVisibility() {
        return this.visibility;
    }

    public UserPreferences visibility(VisibilityPreference visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(VisibilityPreference visibility) {
        this.visibility = visibility;
    }

    public Boolean getIsDarkMode() {
        return this.isDarkMode;
    }

    public UserPreferences isDarkMode(Boolean isDarkMode) {
        this.setIsDarkMode(isDarkMode);
        return this;
    }

    public void setIsDarkMode(Boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
    }

    public Boolean getIsHighContrast() {
        return this.isHighContrast;
    }

    public UserPreferences isHighContrast(Boolean isHighContrast) {
        this.setIsHighContrast(isHighContrast);
        return this;
    }

    public void setIsHighContrast(Boolean isHighContrast) {
        this.isHighContrast = isHighContrast;
    }

    public VisibilityPreference getPlaylistPrivacy() {
        return this.playlistPrivacy;
    }

    public UserPreferences playlistPrivacy(VisibilityPreference playlistPrivacy) {
        this.setPlaylistPrivacy(playlistPrivacy);
        return this;
    }

    public void setPlaylistPrivacy(VisibilityPreference playlistPrivacy) {
        this.playlistPrivacy = playlistPrivacy;
    }

    public Set<SharingPreference> getSharingPreferences() {
        return this.sharingPreferences;
    }

    public void setSharingPreferences(Set<SharingPreference> sharingPreferences) {
        if (this.sharingPreferences != null) {
            this.sharingPreferences.forEach(i -> i.setAppUser(null));
        }
        if (sharingPreferences != null) {
            sharingPreferences.forEach(i -> i.setAppUser(this));
        }
        this.sharingPreferences = sharingPreferences;
    }

    public UserPreferences sharingPreferences(Set<SharingPreference> sharingPreferences) {
        this.setSharingPreferences(sharingPreferences);
        return this;
    }

    public UserPreferences addSharingPreference(SharingPreference sharingPreference) {
        this.sharingPreferences.add(sharingPreference);
        sharingPreference.setAppUser(this);
        return this;
    }

    public UserPreferences removeSharingPreference(SharingPreference sharingPreference) {
        this.sharingPreferences.remove(sharingPreference);
        sharingPreference.setAppUser(null);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        if (this.appUser != null) {
            this.appUser.setUserPreferences(null);
        }
        if (appUser != null) {
            appUser.setUserPreferences(this);
        }
        this.appUser = appUser;
    }

    public UserPreferences appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPreferences)) {
            return false;
        }
        return id != null && id.equals(((UserPreferences) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPreferences{" +
            "id=" + getId() +
            ", visibility='" + getVisibility() + "'" +
            ", isDarkMode='" + getIsDarkMode() + "'" +
            ", isHighContrast='" + getIsHighContrast() + "'" +
            ", playlistPrivacy='" + getPlaylistPrivacy() + "'" +
            "}";
    }
}
