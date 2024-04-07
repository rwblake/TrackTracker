package team.bham.service.feed;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.FeedCard;
import team.bham.domain.Playlist;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;

@Service
public class FeedCardService {

    final PlaylistRepository playlistRepository;
    final AppUserRepository appUserRepository;

    public FeedCardService(PlaylistRepository playlistRepository, AppUserRepository appUserRepository) {
        this.playlistRepository = playlistRepository;
        this.appUserRepository = appUserRepository;
    }

    /** Converts backend cards into a format that the frontend can readily display */
    public List<FeedCardResponse> generateFrontendCards(Set<FeedCard> feedCards) {
        return feedCards
            .stream()
            .map(feedCard -> {
                switch (inferType(feedCard)) {
                    case "milestone":
                        return generateFrontendMilestoneCard(feedCard);
                    case "new-playlist":
                        return generateFrontendPlaylistCard(feedCard);
                    case "personal":
                        return generateFrontendPersonalCard(feedCard);
                    case "friend-request":
                        return generateFrontendFriendRequestCard(feedCard);
                    case "new-friend":
                        return generateFrontendNewFriendCard(feedCard);
                    case "friend-update":
                        return generateFrontendFriendUpdateCard(feedCard);
                    default:
                        throw new InvalidInferredCardTypeException();
                }
            })
            .collect(Collectors.toList());
    }

    /** Infers the type of the card given the metrics received from the database */
    private String inferType(FeedCard feedCard) {
        // if the card belongs to another user
        if (
            feedCard.getFeed().getAppUser().getId().longValue() != feedCard.getCard().getAppUser().getId().longValue()
        ) return "friend-update";

        switch (feedCard.getCard().getMetric()) {
            case FRIEND_REQUEST:
                return "friend-request";
            case NEW_FRIEND:
                return "new-friend";
            case NEW_PLAYLIST:
                return "new-playlist";
        }

        // there is now no timeframe, and card is not a friend update (or request / new friend)

        // if there is no timeframe -> a stat of all time
        if (feedCard.getCard().getTimeFrame() == null) return "milestone";

        // return personal for all else
        return "personal";
    }

    private FeedCardResponse generateFrontendPlaylistCard(FeedCard feedCard) {
        final String type = "new-playlist";
        final String icon = "queue_music";
        String message = String.format("You analysed a new playlist: (ID %s)", feedCard.getCard().getMetricValue());
        URI href = null;

        // Get playlist from card's metric value
        System.out.println("Getting playlist from database");
        Optional<Playlist> optionalPlaylist = playlistRepository.findOneWithEagerRelationships(
            feedCard.getCard().getMetricValue().longValue()
        );
        if (optionalPlaylist.isPresent()) {
            System.out.println("playlist present");
            Playlist playlist = optionalPlaylist.get();
            message = String.format("You analysed a new playlist: %s", playlist.getName());
            href = URI.create("insights/playlist?playlistID=" + playlist.getSpotifyID());
        } else {
            System.out.println("playlist not present");
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private FeedCardResponse generateFrontendMilestoneCard(FeedCard feedCard) {
        final String type = "milestone";
        String icon;
        String message;
        URI href = URI.create("insights");

        switch (feedCard.getCard().getMetric()) {
            case LISTENING_DURATION:
                {
                    icon = "schedule";
                    message =
                        String.format("You have spent a total of %d minutes listening to Spotify!", feedCard.getCard().getMetricValue());
                    break;
                }
            case NO_OF_SONGS_LISTENED:
                {
                    icon = "numbers";
                    message = String.format("You have listened to a total of %d songs!", feedCard.getCard().getMetricValue());
                    break;
                }
            case NO_OF_FRIENDS:
                {
                    icon = "diversity_1";
                    message = String.format("You now have %d friends!", feedCard.getCard().getMetricValue());
                    href = URI.create("friends");
                    break;
                }
            case TOP_ARTIST:
                {
                    icon = "artist";
                    message = String.format("Your top artist of all time is (artistID: %d)!", feedCard.getCard().getMetricValue());
                    break;
                }
            case TOP_GENRE:
                {
                    icon = "music";
                    message = String.format("Your top genre of all time is (genreID: %d)!", feedCard.getCard().getMetricValue());
                    break;
                }
            case TOP_SONG:
                {
                    icon = "song";
                    message = String.format("Your top song of all time is (songID: %d)!", feedCard.getCard().getMetricValue());
                    break;
                }
            default:
                {
                    icon = "question_mark";
                    message = "Not yet implemented";
                    href = null;
                }
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private FeedCardResponse generateFrontendPersonalCard(FeedCard feedCard) {
        final String type = "personal";
        String icon;
        String message;
        URI href = URI.create("insights");

        String formattedDuration = formatDuration(feedCard.getCard().getTimeFrame());

        switch (feedCard.getCard().getMetric()) {
            case LISTENING_DURATION:
                {
                    icon = "schedule";
                    message =
                        String.format(
                            "%s you have spent %d minutes listening to Spotify!",
                            capitalise(formattedDuration),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            case NO_OF_SONGS_LISTENED:
                {
                    icon = "numbers";
                    message =
                        String.format(
                            "%s you have listened to %d songs!",
                            capitalise(formattedDuration),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            case TOP_ARTIST:
                {
                    icon = "artist";
                    message =
                        String.format("Your top artist %s is (artistID: %d)!", formattedDuration, feedCard.getCard().getMetricValue());
                    break;
                }
            default:
                {
                    icon = "question_mark";
                    message = "Not yet implemented.";
                    href = null;
                }
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private FeedCardResponse generateFrontendFriendUpdateCard(FeedCard feedCard) {
        final String type = "friend-update";
        String icon;
        String message;
        URI href = URI.create("insights/friends");

        String formattedDuration = formatDuration(feedCard.getCard().getTimeFrame());
        User owner = feedCard.getCard().getAppUser().getInternalUser();

        switch (feedCard.getCard().getMetric()) {
            case LISTENING_DURATION:
                {
                    icon = "schedule";
                    message =
                        String.format(
                            "%s %s has spent %d minutes listening to Spotify.",
                            capitalise(formattedDuration),
                            owner.getFirstName(),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            case NO_OF_SONGS_LISTENED:
                {
                    icon = "numbers";
                    message =
                        String.format(
                            "%s %s has listened to %d songs.",
                            capitalise(formattedDuration),
                            owner.getFirstName(),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            case TOP_ARTIST:
                {
                    icon = "artist";
                    message =
                        String.format(
                            "%s's top artist %s is (artistID: %d).",
                            owner.getFirstName(),
                            formattedDuration,
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            default:
                {
                    icon = "question_mark";
                    message = "Not yet implemented.";
                    href = null;
                }
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private FeedCardResponse generateFrontendNewFriendCard(FeedCard feedCard) {
        final String type = "new-friend";
        String icon = "person_add";
        String message = String.format("You are now friends with: (AppUserID %s)", feedCard.getCard().getMetricValue());
        URI href = URI.create("friends");

        // Get AppUser from card's metric value
        Optional<AppUser> optionalAppUser = appUserRepository.findOneById(feedCard.getCard().getMetricValue().longValue());

        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            User internalUser = appUser.getInternalUser();
            message = String.format("You are now friends with %s %s.", internalUser.getFirstName(), internalUser.getLastName());
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private FeedCardResponse generateFrontendFriendRequestCard(FeedCard feedCard) {
        final String type = "friend-request";
        String icon = "waving_hand";
        String message = String.format("Friend request from: (AppUserID %s)", feedCard.getCard().getMetricValue());
        URI href = URI.create("friends");

        // Get AppUser from card's metric value
        Optional<AppUser> optionalAppUser = appUserRepository.findOneById(feedCard.getCard().getMetricValue().longValue());

        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            User internalUser = appUser.getInternalUser();
            message = String.format("%s %s has requested to follow you.", internalUser.getFirstName(), internalUser.getLastName());
        }

        return new FeedCardResponse(feedCard, type, message, icon, href);
    }

    private String capitalise(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String formatDuration(Duration duration) {
        if (duration.toMinutes() == 1) {
            return "this minute";
        }

        if (duration.toHours() == 1) {
            return "this hour";
        }

        if (duration.toDays() == 1) {
            return "today";
        }

        if (duration.toDays() == 7) {
            return "this week";
        }

        long weeks = duration.toDays() / 7;
        return "in the last " + weeks + " week" + (weeks > 1 ? "s" : "");
    }
}
