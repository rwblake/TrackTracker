package team.bham.service.account;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;

public class AccountCombinedResponse implements Serializable {

    Long id;
    String spotifyID;
    String avatarURL;
    String bio;
    String spotifyUsername;
    InternalUserResponse internalUser;
    Long userPreferencesID;
    FeedResponse feed;
    List<FriendshipResponse> friends;

    public AccountCombinedResponse(AppUser appUser, Set<team.bham.domain.Friendship> friends, Set<team.bham.domain.FeedCard> feedCards) {
        id = appUser.getId();
        spotifyID = appUser.getSpotifyID();
        avatarURL = appUser.getAvatarURL();
        bio = appUser.getBio();
        spotifyUsername = appUser.getSpotifyUsername();
        internalUser = new InternalUserResponse(appUser.getInternalUser());
        userPreferencesID = appUser.getUserPreferences().getId();
        feed = new FeedResponse(appUser.getFeed()); // TODO
        feed.cards = feedCards.stream().map(feedCard -> new FeedCardResponse(feedCard, appUser.getId())).collect(Collectors.toList());
        this.friends = friends.stream().map(friend -> new FriendshipResponse(friend, appUser.getId())).collect(Collectors.toList());
    }

    static class FeedResponse implements Serializable {

        Long id;
        Instant lastUpdated;
        List<FeedCardResponse> cards;

        public FeedResponse(team.bham.domain.Feed feed) {
            id = feed.getId();
            lastUpdated = feed.getLastUpdated();
        }
    }

    static class FeedCardResponse implements Serializable {

        Long id;
        boolean liked;
        CardType metric;
        Duration timeFrame;
        Integer metricValue;
        Instant timeGenerated;
        BelongsTo belongsTo;

        /** NOT STORED IN DATABASE:
         * Used to give the front end more information about how to display the card.
         * There are these types of inferred type: "friend-request" | "new-friend" | "milestone" | "personal" | "friend-update"
         * <br> For example: <br>
         * The card pertains to another user -> inferredType = "friend" -> The card will be displayed in the
         * frontend as a friend update.
         * */
        String inferredType;

        public FeedCardResponse(team.bham.domain.FeedCard feedCard, Long feedOwnerID) {
            id = feedCard.getId();
            liked = feedCard.getLiked();
            metric = feedCard.getCard().getMetric();
            timeFrame = feedCard.getCard().getTimeFrame();
            metricValue = feedCard.getCard().getMetricValue();
            timeGenerated = feedCard.getCard().getTimeGenerated();
            belongsTo =
                new BelongsTo(
                    feedCard.getCard().getAppUser().getId(),
                    feedCard.getCard().getAppUser().getInternalUser().getFirstName(),
                    feedCard.getCard().getAppUser().getInternalUser().getLastName()
                );

            // infer the type of the card
            inferredType = inferType(feedOwnerID);
        }

        private String inferType(Long feedOwnerID) {
            // if the card belongs to another user
            if (feedOwnerID.longValue() != belongsTo.id.longValue()) return "friend-update";

            switch (metric) {
                case FRIEND_REQUEST:
                    return "friend-request";
                case NEW_FRIEND:
                    return "new-friend";
            }

            // there is now no timeframe, and card is not a friend update (or request / new friend)

            // if there is no timeframe -> a stat of all time
            if (timeFrame == null) return "milestone";

            // return personal for all else
            return "personal";
        }
    }

    static class BelongsTo implements Serializable {

        Long id;
        String firstName;
        String lastName;

        BelongsTo(Long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    static class InternalUserResponse implements Serializable {

        Long id;
        String firstName;
        String lastName;
        String login;
        String email;

        InternalUserResponse(User internalUser) {
            this.id = internalUser.getId();
            this.firstName = internalUser.getFirstName();
            this.lastName = internalUser.getLastName();
            this.login = internalUser.getLogin();
            this.email = internalUser.getEmail();
        }
    }

    static class FriendshipResponse implements Serializable {

        Instant createdAt;
        long friendID;
        String firstName;
        String lastName;
        String avatarURL;

        public FriendshipResponse(team.bham.domain.Friendship friendship, Long me) {
            createdAt = friendship.getCreatedAt();
            AppUser otherPerson;
            if (friendship.getFriendAccepting().getId().longValue() != me.longValue()) {
                otherPerson = friendship.getFriendAccepting();
            } else {
                otherPerson = friendship.getFriendInitiating();
            }
            friendID = otherPerson.getId();
            firstName = otherPerson.getInternalUser().getFirstName();
            lastName = otherPerson.getInternalUser().getLastName();
            avatarURL = otherPerson.getAvatarURL();
        }
    }
}
