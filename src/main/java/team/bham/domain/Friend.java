package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;

public class Friend implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Instant createdAt;

    private Boolean initiatedByCurrentUser;

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
    private AppUser friendAppUser;

    public Song getMostRecentSong() {
        return mostRecentSong;
    }

    public void setMostRecentSong(Song mostRecentSong) {
        this.mostRecentSong = mostRecentSong;
    }

    @JsonIgnoreProperties(
        value = {
            "streams", "happiestPlaylistStats", "energeticPlaylistStats", "sumsUpPlaylistStats", "anomalousPlaylistStats", "playlists",
        },
        allowSetters = true
    )
    private Song mostRecentSong;

    public Friend(Instant createdAt, Boolean initiatedByCurrentUser, AppUser friendAppUser, Song mostRecentSong, Long id) {
        this.createdAt = createdAt;
        this.initiatedByCurrentUser = initiatedByCurrentUser;
        this.friendAppUser = friendAppUser;
        this.mostRecentSong = mostRecentSong;
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getInitiatedByCurrentUser() {
        return initiatedByCurrentUser;
    }

    public void setInitiatedByCurrentUser(Boolean initiatedByCurrentUser) {
        this.initiatedByCurrentUser = initiatedByCurrentUser;
    }

    public AppUser getFriendAppUser() {
        return friendAppUser;
    }

    public void setFriendAppUser(AppUser friendAppUser) {
        this.friendAppUser = friendAppUser;
    }
}
