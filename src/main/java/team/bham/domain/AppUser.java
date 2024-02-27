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

    @JsonIgnoreProperties(value = { "sharingPreferences", "appUser" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private UserPreferences userPreferences;

    @JsonIgnoreProperties(value = { "appUser" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private SpotifyToken spotifyToken;

    @JsonIgnoreProperties(value = { "cards", "appUser" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Feed feed;

    @OneToMany(mappedBy = "appUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "friendInitiating", "friendAccepting", "appUser" }, allowSetters = true)
    private Set<Friendship> friends = new HashSet<>();

    @OneToMany(mappedBy = "toAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "initiatingAppUser", "toAppUser" }, allowSetters = true)
    private Set<FriendRequest> toFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "forAppUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aboutAppUser", "forAppUser" }, allowSetters = true)
    private Set<FriendRecommendation> forFriendRecommendations = new HashSet<>();

    /**
     * Blocked users are related to the user who blocked them.
     */
    @Schema(description = "Blocked users are related to the user who blocked them.")
    @OneToMany(mappedBy = "blockedByUser")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<AppUser> blockedUsers = new HashSet<>();

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

    @JsonIgnoreProperties(value = { "aboutAppUser", "forAppUser" }, allowSetters = true)
    @OneToOne(mappedBy = "aboutAppUser")
    private FriendRecommendation aboutFriendRecommendation;

    @JsonIgnoreProperties(value = { "initiatingAppUser", "toAppUser" }, allowSetters = true)
    @OneToOne(mappedBy = "initiatingAppUser")
    private FriendRequest intitiatingFriendRequest;

    @JsonIgnoreProperties(value = { "friendInitiating", "friendAccepting", "appUser" }, allowSetters = true)
    @OneToOne(mappedBy = "friendInitiating")
    private Friendship friendshipInitiated;

    @JsonIgnoreProperties(value = { "friendInitiating", "friendAccepting", "appUser" }, allowSetters = true)
    @OneToOne(mappedBy = "friendAccepting")
    private Friendship friendshipAccepted;

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
    private AppUser blockedByUser;

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

    public Set<Friendship> getFriends() {
        return this.friends;
    }

    public void setFriends(Set<Friendship> friendships) {
        if (this.friends != null) {
            this.friends.forEach(i -> i.setAppUser(null));
        }
        if (friendships != null) {
            friendships.forEach(i -> i.setAppUser(this));
        }
        this.friends = friendships;
    }

    public AppUser friends(Set<Friendship> friendships) {
        this.setFriends(friendships);
        return this;
    }

    public AppUser addFriend(Friendship friendship) {
        this.friends.add(friendship);
        friendship.setAppUser(this);
        return this;
    }

    public AppUser removeFriend(Friendship friendship) {
        this.friends.remove(friendship);
        friendship.setAppUser(null);
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

    public Set<AppUser> getBlockedUsers() {
        return this.blockedUsers;
    }

    public void setBlockedUsers(Set<AppUser> appUsers) {
        if (this.blockedUsers != null) {
            this.blockedUsers.forEach(i -> i.setBlockedByUser(null));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.setBlockedByUser(this));
        }
        this.blockedUsers = appUsers;
    }

    public AppUser blockedUsers(Set<AppUser> appUsers) {
        this.setBlockedUsers(appUsers);
        return this;
    }

    public AppUser addBlockedUser(AppUser appUser) {
        this.blockedUsers.add(appUser);
        appUser.setBlockedByUser(this);
        return this;
    }

    public AppUser removeBlockedUser(AppUser appUser) {
        this.blockedUsers.remove(appUser);
        appUser.setBlockedByUser(null);
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

    public FriendRecommendation getAboutFriendRecommendation() {
        return this.aboutFriendRecommendation;
    }

    public void setAboutFriendRecommendation(FriendRecommendation friendRecommendation) {
        if (this.aboutFriendRecommendation != null) {
            this.aboutFriendRecommendation.setAboutAppUser(null);
        }
        if (friendRecommendation != null) {
            friendRecommendation.setAboutAppUser(this);
        }
        this.aboutFriendRecommendation = friendRecommendation;
    }

    public AppUser aboutFriendRecommendation(FriendRecommendation friendRecommendation) {
        this.setAboutFriendRecommendation(friendRecommendation);
        return this;
    }

    public FriendRequest getIntitiatingFriendRequest() {
        return this.intitiatingFriendRequest;
    }

    public void setIntitiatingFriendRequest(FriendRequest friendRequest) {
        if (this.intitiatingFriendRequest != null) {
            this.intitiatingFriendRequest.setInitiatingAppUser(null);
        }
        if (friendRequest != null) {
            friendRequest.setInitiatingAppUser(this);
        }
        this.intitiatingFriendRequest = friendRequest;
    }

    public AppUser intitiatingFriendRequest(FriendRequest friendRequest) {
        this.setIntitiatingFriendRequest(friendRequest);
        return this;
    }

    public Friendship getFriendshipInitiated() {
        return this.friendshipInitiated;
    }

    public void setFriendshipInitiated(Friendship friendship) {
        if (this.friendshipInitiated != null) {
            this.friendshipInitiated.setFriendInitiating(null);
        }
        if (friendship != null) {
            friendship.setFriendInitiating(this);
        }
        this.friendshipInitiated = friendship;
    }

    public AppUser friendshipInitiated(Friendship friendship) {
        this.setFriendshipInitiated(friendship);
        return this;
    }

    public Friendship getFriendshipAccepted() {
        return this.friendshipAccepted;
    }

    public void setFriendshipAccepted(Friendship friendship) {
        if (this.friendshipAccepted != null) {
            this.friendshipAccepted.setFriendAccepting(null);
        }
        if (friendship != null) {
            friendship.setFriendAccepting(this);
        }
        this.friendshipAccepted = friendship;
    }

    public AppUser friendshipAccepted(Friendship friendship) {
        this.setFriendshipAccepted(friendship);
        return this;
    }

    public AppUser getBlockedByUser() {
        return this.blockedByUser;
    }

    public void setBlockedByUser(AppUser appUser) {
        this.blockedByUser = appUser;
    }

    public AppUser blockedByUser(AppUser appUser) {
        this.setBlockedByUser(appUser);
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
