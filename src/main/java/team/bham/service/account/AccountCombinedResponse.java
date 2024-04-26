package team.bham.service.account;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.service.feed.FeedCardResponse;

public class AccountCombinedResponse implements Serializable {

    private Long id;
    private String spotifyID;
    private String avatarURL;
    private String bio;
    private String spotifyUsername;
    private InternalUserResponse internalUser;
    private Long userPreferencesID;
    private FeedResponse feed;
    private List<FriendshipResponse> friends;

    public AccountCombinedResponse(
        AppUser appUser,
        Set<team.bham.domain.Friendship> friends,
        List<FeedCardResponse> feedCards,
        List<Integer> pinnedFriendIds
    ) {
        id = appUser.getId();
        spotifyID = appUser.getSpotifyID();
        avatarURL = appUser.getAvatarURL();
        bio = appUser.getBio();
        spotifyUsername = appUser.getSpotifyUsername();
        internalUser = new InternalUserResponse(appUser.getInternalUser());
        userPreferencesID = appUser.getUserPreferences().getId();
        feed = new FeedResponse(appUser.getFeed());
        feed.setCards(feedCards);

        this.friends =
            friends
                .stream()
                .map(friend -> {
                    FriendshipResponse fr = new FriendshipResponse(friend, appUser.getId());
                    boolean isPinned = pinnedFriendIds.stream().anyMatch(id -> id == fr.getFriendID());
                    fr.setPinned(isPinned);
                    return fr;
                })
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSpotifyUsername() {
        return spotifyUsername;
    }

    public void setSpotifyUsername(String spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }

    public InternalUserResponse getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUserResponse internalUser) {
        this.internalUser = internalUser;
    }

    public Long getUserPreferencesID() {
        return userPreferencesID;
    }

    public void setUserPreferencesID(Long userPreferencesID) {
        this.userPreferencesID = userPreferencesID;
    }

    public FeedResponse getFeed() {
        return feed;
    }

    public void setFeed(FeedResponse feed) {
        this.feed = feed;
    }

    public List<FriendshipResponse> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendshipResponse> friends) {
        this.friends = friends;
    }
}

class FeedResponse implements Serializable {

    private Long id;
    private Instant lastUpdated;
    private List<FeedCardResponse> cards;

    public FeedResponse(team.bham.domain.Feed feed) {
        id = feed.getId();
        lastUpdated = feed.getLastUpdated();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<FeedCardResponse> getCards() {
        return cards;
    }

    public void setCards(List<FeedCardResponse> cards) {
        this.cards = cards;
    }
}

class InternalUserResponse implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;

    InternalUserResponse(User internalUser) {
        this.id = internalUser.getId();
        this.firstName = internalUser.getFirstName();
        this.lastName = internalUser.getLastName();
        this.login = internalUser.getLogin();
        this.email = internalUser.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class FriendshipResponse implements Serializable {

    private Instant createdAt;
    private long friendID;
    private String firstName;
    private String lastName;
    private String avatarURL;
    private boolean pinned;
    private String spotifyID;

    public FriendshipResponse(team.bham.domain.Friendship friendship, Long myId) {
        createdAt = friendship.getCreatedAt();
        AppUser otherPerson;
        if (friendship.getFriendAccepting().getId().longValue() != myId.longValue()) {
            otherPerson = friendship.getFriendAccepting();
        } else {
            otherPerson = friendship.getFriendInitiating();
        }
        friendID = otherPerson.getId();
        firstName = otherPerson.getInternalUser().getFirstName();
        lastName = otherPerson.getInternalUser().getLastName();
        avatarURL = otherPerson.getAvatarURL();
        spotifyID = otherPerson.getSpotifyID();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public long getFriendID() {
        return friendID;
    }

    public void setFriendID(long friendID) {
        this.friendID = friendID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }
}
