package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * Profile information about a user, identifying them.
 */
@Schema(description = "Profile information about a user, identifying them.")
@Entity
@Table(name = "app_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "spotify_id", nullable = false)
    private String spotifyID;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "bio")
    private String bio;

    @NotNull
    @Column(name = "spotify_username", nullable = false)
    private String spotifyUsername;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(unique = true)
    private User internalUser;

    @JsonIgnoreProperties(value = { "sharingPreferences", "appUser" }, allowSetters = true)
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(unique = true)
    private UserPreferences userPreferences;

    @JsonIgnoreProperties(value = { "appUser" }, allowSetters = true)
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(unique = true)
    private SpotifyToken spotifyToken;

    @JsonIgnoreProperties(value = { "cards", "appUser" }, allowSetters = true)
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(unique = true)
    private Feed feed;

    @OneToMany(mappedBy = "appUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "playlistStats", "songs", "appUser" }, allowSetters = true)
    private Set<Playlist> playlists = new HashSet<>();

    @OneToMany(mappedBy = "appUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "song", "appUser" }, allowSetters = true)
    private Set<Stream> streams = new HashSet<>();

    @OneToMany(mappedBy = "appUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "usages", "appUser" }, allowSetters = true)
    private Set<Card> cards = new HashSet<>();

    @OneToMany(mappedBy = "appUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "metrics", "appUser" }, allowSetters = true)
    private Set<CardTemplate> cardTemplates = new HashSet<>();

    /**
     * Blocked users are related to the user who blocked them.
     */
    @Schema(description = "Blocked users are related to the user who blocked them.")
    @ManyToMany
    @JoinTable(
        name = "rel_app_user__blocked_user",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<AppUser> blockedUsers = new HashSet<>();

    @OneToMany(mappedBy = "aboutAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aboutAppUser", "forAppUser" }, allowSetters = true)
    private Set<FriendRecommendation> aboutFriendRecommendations = new HashSet<>();

    @OneToMany(mappedBy = "forAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aboutAppUser", "forAppUser" }, allowSetters = true)
    private Set<FriendRecommendation> forFriendRecommendations = new HashSet<>();

    @OneToMany(mappedBy = "initiatingAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "initiatingAppUser", "toAppUser" }, allowSetters = true)
    private Set<FriendRequest> intitiatingFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "toAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "initiatingAppUser", "toAppUser" }, allowSetters = true)
    private Set<FriendRequest> toFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "friendInitiating")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "friendInitiating", "friendAccepting" }, allowSetters = true)
    private Set<Friendship> friendshipInitiateds = new HashSet<>();

    @OneToMany(mappedBy = "friendAccepting")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "friendInitiating", "friendAccepting" }, allowSetters = true)
    private Set<Friendship> friendshipAccepteds = new HashSet<>();

    @ManyToMany(mappedBy = "blockedUsers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<AppUser> blockedByUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return this.spotifyID;
    }

    public AppUser spotifyID(String spotifyID) {
        this.setSpotifyID(spotifyID);
        return this;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return this.name;
    }

    public AppUser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return this.avatarURL;
    }

    public AppUser avatarURL(String avatarURL) {
        this.setAvatarURL(avatarURL);
        return this;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getBio() {
        return this.bio;
    }

    public AppUser bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSpotifyUsername() {
        return this.spotifyUsername;
    }

    public AppUser spotifyUsername(String spotifyUsername) {
        this.setSpotifyUsername(spotifyUsername);
        return this;
    }

    public void setSpotifyUsername(String spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public AppUser internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public UserPreferences getUserPreferences() {
        return this.userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public AppUser userPreferences(UserPreferences userPreferences) {
        this.setUserPreferences(userPreferences);
        return this;
    }

    public SpotifyToken getSpotifyToken() {
        return this.spotifyToken;
    }

    public void setSpotifyToken(SpotifyToken spotifyToken) {
        this.spotifyToken = spotifyToken;
    }

    public AppUser spotifyToken(SpotifyToken spotifyToken) {
        this.setSpotifyToken(spotifyToken);
        return this;
    }

    public Feed getFeed() {
        return this.feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public AppUser feed(Feed feed) {
        this.setFeed(feed);
        return this;
    }

    public Set<Playlist> getPlaylists() {
        return this.playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        if (this.playlists != null) {
            this.playlists.forEach(i -> i.setAppUser(null));
        }
        if (playlists != null) {
            playlists.forEach(i -> i.setAppUser(this));
        }
        this.playlists = playlists;
    }

    public AppUser playlists(Set<Playlist> playlists) {
        this.setPlaylists(playlists);
        return this;
    }

    public AppUser addPlaylist(Playlist playlist) {
        this.playlists.add(playlist);
        playlist.setAppUser(this);
        return this;
    }

    public AppUser removePlaylist(Playlist playlist) {
        this.playlists.remove(playlist);
        playlist.setAppUser(null);
        return this;
    }

    public Set<Stream> getStreams() {
        return this.streams;
    }

    public void setStreams(Set<Stream> streams) {
        if (this.streams != null) {
            this.streams.forEach(i -> i.setAppUser(null));
        }
        if (streams != null) {
            streams.forEach(i -> i.setAppUser(this));
        }
        this.streams = streams;
    }

    public AppUser streams(Set<Stream> streams) {
        this.setStreams(streams);
        return this;
    }

    public AppUser addStream(Stream stream) {
        this.streams.add(stream);
        stream.setAppUser(this);
        return this;
    }

    public AppUser removeStream(Stream stream) {
        this.streams.remove(stream);
        stream.setAppUser(null);
        return this;
    }

    public Set<Card> getCards() {
        return this.cards;
    }

    public void setCards(Set<Card> cards) {
        if (this.cards != null) {
            this.cards.forEach(i -> i.setAppUser(null));
        }
        if (cards != null) {
            cards.forEach(i -> i.setAppUser(this));
        }
        this.cards = cards;
    }

    public AppUser cards(Set<Card> cards) {
        this.setCards(cards);
        return this;
    }

    public AppUser addCard(Card card) {
        this.cards.add(card);
        card.setAppUser(this);
        return this;
    }

    public AppUser removeCard(Card card) {
        this.cards.remove(card);
        card.setAppUser(null);
        return this;
    }

    public Set<CardTemplate> getCardTemplates() {
        return this.cardTemplates;
    }

    public void setCardTemplates(Set<CardTemplate> cardTemplates) {
        if (this.cardTemplates != null) {
            this.cardTemplates.forEach(i -> i.setAppUser(null));
        }
        if (cardTemplates != null) {
            cardTemplates.forEach(i -> i.setAppUser(this));
        }
        this.cardTemplates = cardTemplates;
    }

    public AppUser cardTemplates(Set<CardTemplate> cardTemplates) {
        this.setCardTemplates(cardTemplates);
        return this;
    }

    public AppUser addCardTemplate(CardTemplate cardTemplate) {
        this.cardTemplates.add(cardTemplate);
        cardTemplate.setAppUser(this);
        return this;
    }

    public AppUser removeCardTemplate(CardTemplate cardTemplate) {
        this.cardTemplates.remove(cardTemplate);
        cardTemplate.setAppUser(null);
        return this;
    }

    public Set<AppUser> getBlockedUsers() {
        return this.blockedUsers;
    }

    public void setBlockedUsers(Set<AppUser> appUsers) {
        this.blockedUsers = appUsers;
    }

    public AppUser blockedUsers(Set<AppUser> appUsers) {
        this.setBlockedUsers(appUsers);
        return this;
    }

    public AppUser addBlockedUser(AppUser appUser) {
        this.blockedUsers.add(appUser);
        appUser.getBlockedByUsers().add(this);
        return this;
    }

    public AppUser removeBlockedUser(AppUser appUser) {
        this.blockedUsers.remove(appUser);
        appUser.getBlockedByUsers().remove(this);
        return this;
    }

    public Set<FriendRecommendation> getAboutFriendRecommendations() {
        return this.aboutFriendRecommendations;
    }

    public void setAboutFriendRecommendations(Set<FriendRecommendation> friendRecommendations) {
        if (this.aboutFriendRecommendations != null) {
            this.aboutFriendRecommendations.forEach(i -> i.setAboutAppUser(null));
        }
        if (friendRecommendations != null) {
            friendRecommendations.forEach(i -> i.setAboutAppUser(this));
        }
        this.aboutFriendRecommendations = friendRecommendations;
    }

    public AppUser aboutFriendRecommendations(Set<FriendRecommendation> friendRecommendations) {
        this.setAboutFriendRecommendations(friendRecommendations);
        return this;
    }

    public AppUser addAboutFriendRecommendation(FriendRecommendation friendRecommendation) {
        this.aboutFriendRecommendations.add(friendRecommendation);
        friendRecommendation.setAboutAppUser(this);
        return this;
    }

    public AppUser removeAboutFriendRecommendation(FriendRecommendation friendRecommendation) {
        this.aboutFriendRecommendations.remove(friendRecommendation);
        friendRecommendation.setAboutAppUser(null);
        return this;
    }

    public Set<FriendRecommendation> getForFriendRecommendations() {
        return this.forFriendRecommendations;
    }

    public void setForFriendRecommendations(Set<FriendRecommendation> friendRecommendations) {
        if (this.forFriendRecommendations != null) {
            this.forFriendRecommendations.forEach(i -> i.setForAppUser(null));
        }
        if (friendRecommendations != null) {
            friendRecommendations.forEach(i -> i.setForAppUser(this));
        }
        this.forFriendRecommendations = friendRecommendations;
    }

    public AppUser forFriendRecommendations(Set<FriendRecommendation> friendRecommendations) {
        this.setForFriendRecommendations(friendRecommendations);
        return this;
    }

    public AppUser addForFriendRecommendation(FriendRecommendation friendRecommendation) {
        this.forFriendRecommendations.add(friendRecommendation);
        friendRecommendation.setForAppUser(this);
        return this;
    }

    public AppUser removeForFriendRecommendation(FriendRecommendation friendRecommendation) {
        this.forFriendRecommendations.remove(friendRecommendation);
        friendRecommendation.setForAppUser(null);
        return this;
    }

    public Set<FriendRequest> getIntitiatingFriendRequests() {
        return this.intitiatingFriendRequests;
    }

    public void setIntitiatingFriendRequests(Set<FriendRequest> friendRequests) {
        if (this.intitiatingFriendRequests != null) {
            this.intitiatingFriendRequests.forEach(i -> i.setInitiatingAppUser(null));
        }
        if (friendRequests != null) {
            friendRequests.forEach(i -> i.setInitiatingAppUser(this));
        }
        this.intitiatingFriendRequests = friendRequests;
    }

    public AppUser intitiatingFriendRequests(Set<FriendRequest> friendRequests) {
        this.setIntitiatingFriendRequests(friendRequests);
        return this;
    }

    public AppUser addIntitiatingFriendRequest(FriendRequest friendRequest) {
        this.intitiatingFriendRequests.add(friendRequest);
        friendRequest.setInitiatingAppUser(this);
        return this;
    }

    public AppUser removeIntitiatingFriendRequest(FriendRequest friendRequest) {
        this.intitiatingFriendRequests.remove(friendRequest);
        friendRequest.setInitiatingAppUser(null);
        return this;
    }

    public Set<FriendRequest> getToFriendRequests() {
        return this.toFriendRequests;
    }

    public void setToFriendRequests(Set<FriendRequest> friendRequests) {
        if (this.toFriendRequests != null) {
            this.toFriendRequests.forEach(i -> i.setToAppUser(null));
        }
        if (friendRequests != null) {
            friendRequests.forEach(i -> i.setToAppUser(this));
        }
        this.toFriendRequests = friendRequests;
    }

    public AppUser toFriendRequests(Set<FriendRequest> friendRequests) {
        this.setToFriendRequests(friendRequests);
        return this;
    }

    public AppUser addToFriendRequest(FriendRequest friendRequest) {
        this.toFriendRequests.add(friendRequest);
        friendRequest.setToAppUser(this);
        return this;
    }

    public AppUser removeToFriendRequest(FriendRequest friendRequest) {
        this.toFriendRequests.remove(friendRequest);
        friendRequest.setToAppUser(null);
        return this;
    }

    public Set<Friendship> getFriendshipInitiateds() {
        return this.friendshipInitiateds;
    }

    public void setFriendshipInitiateds(Set<Friendship> friendships) {
        if (this.friendshipInitiateds != null) {
            this.friendshipInitiateds.forEach(i -> i.setFriendInitiating(null));
        }
        if (friendships != null) {
            friendships.forEach(i -> i.setFriendInitiating(this));
        }
        this.friendshipInitiateds = friendships;
    }

    public AppUser friendshipInitiateds(Set<Friendship> friendships) {
        this.setFriendshipInitiateds(friendships);
        return this;
    }

    public AppUser addFriendshipInitiated(Friendship friendship) {
        this.friendshipInitiateds.add(friendship);
        friendship.setFriendInitiating(this);
        return this;
    }

    public AppUser removeFriendshipInitiated(Friendship friendship) {
        this.friendshipInitiateds.remove(friendship);
        friendship.setFriendInitiating(null);
        return this;
    }

    public Set<Friendship> getFriendshipAccepteds() {
        return this.friendshipAccepteds;
    }

    public void setFriendshipAccepteds(Set<Friendship> friendships) {
        if (this.friendshipAccepteds != null) {
            this.friendshipAccepteds.forEach(i -> i.setFriendAccepting(null));
        }
        if (friendships != null) {
            friendships.forEach(i -> i.setFriendAccepting(this));
        }
        this.friendshipAccepteds = friendships;
    }

    public AppUser friendshipAccepteds(Set<Friendship> friendships) {
        this.setFriendshipAccepteds(friendships);
        return this;
    }

    public AppUser addFriendshipAccepted(Friendship friendship) {
        this.friendshipAccepteds.add(friendship);
        friendship.setFriendAccepting(this);
        return this;
    }

    public AppUser removeFriendshipAccepted(Friendship friendship) {
        this.friendshipAccepteds.remove(friendship);
        friendship.setFriendAccepting(null);
        return this;
    }

    public Set<AppUser> getBlockedByUsers() {
        return this.blockedByUsers;
    }

    public void setBlockedByUsers(Set<AppUser> appUsers) {
        if (this.blockedByUsers != null) {
            this.blockedByUsers.forEach(i -> i.removeBlockedUser(this));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.addBlockedUser(this));
        }
        this.blockedByUsers = appUsers;
    }

    public AppUser blockedByUsers(Set<AppUser> appUsers) {
        this.setBlockedByUsers(appUsers);
        return this;
    }

    public AppUser addBlockedByUser(AppUser appUser) {
        this.blockedByUsers.add(appUser);
        appUser.getBlockedUsers().add(this);
        return this;
    }

    public AppUser removeBlockedByUser(AppUser appUser) {
        this.blockedByUsers.remove(appUser);
        appUser.getBlockedUsers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", spotifyID='" + getSpotifyID() + "'" +
            ", name='" + getName() + "'" +
            ", avatarURL='" + getAvatarURL() + "'" +
            ", bio='" + getBio() + "'" +
            ", spotifyUsername='" + getSpotifyUsername() + "'" +
            "}";
    }
}
