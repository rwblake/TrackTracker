package team.bham.service.feed;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.*;

/** This service handles the abstraction of FeedCards. It is used to:
 * <ul>
 *     <li>Convert FeedCards to FeedCardResponses (for use in the frontend)</li>
 *     <li>Create and add cards to a user's feed</li>
 * </ul>
 * @apiNote For more fine-tune access manipulating Cards, use CardService
 * */
@Service
@Transactional
public class FeedCardService {

    final PlaylistRepository playlistRepository;
    final AppUserRepository appUserRepository;
    private final CardRepository cardRepository;

    private final Logger log = LoggerFactory.getLogger(FeedCardService.class);
    private final FeedRepository feedRepository;
    private final SongRepository songRepository;

    public FeedCardService(
        PlaylistRepository playlistRepository,
        AppUserRepository appUserRepository,
        CardRepository cardRepository,
        FeedRepository feedRepository,
        SongRepository songRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.appUserRepository = appUserRepository;
        this.cardRepository = cardRepository;
        this.feedRepository = feedRepository;
        this.songRepository = songRepository;
    }

    public void addCardToFeed(Card card, Feed feed) {
        // create new feedCard
        FeedCard feedCard = new FeedCard();
        feedCard.setCard(card);
        feedCard.setLiked(false);
        feedCard.setFeed(feed);
        // add feedCard to user's feed
        feed.addCard(feedCard);
        // save changes
        feedRepository.save(feed);
    }

    /** Converts backend cards into a format that the frontend can readily display */
    public List<FeedCardResponse> generateFrontendCards(Set<FeedCard> feedCards) {
        return feedCards
            .stream()
            .map(feedCard -> {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .collect(Collectors.toList());
    }

    /**Create a card notifying a given AppUser of a newly-formed friendship with a given friendID.
     * <br>The new friend card automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param friendID the AppUser id of the friend
     * @return The card once it has been saved in the database*/
    public Card createNewFriendCard(AppUser appUser, Long friendID) {
        log.debug("Making a new NewFriend card");
        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(CardType.NEW_FRIEND);
        card.setMetricValue(friendID.intValue());
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now());
        // save in database
        card = cardRepository.save(card);
        // add to the user's feed
        Feed feed = appUser.getFeed();
        addCardToFeed(card, feed);
        return card;
    }

    /**Delete any cards notifying a given AppUser of a new friend request from a given friendID.
     * <br>The friend request card automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param friendID the AppUser id of the friend who is requesting to follow you
     */
    public void deleteNewFriendCards(AppUser appUser, Long friendID) {
        log.debug("Deleting specified NewFriend cards");
        cardRepository.deleteAllByAppUserIdAndMetricAndMetricValue(appUser.getId(), CardType.NEW_FRIEND, friendID.intValue());
    }

    /**Create a card notifying a given AppUser of a new friend request from a given friendID.
     * <br>The friend request card automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param friendID the AppUser id of the friend who is requesting to follow you
     * @return The card once it has been saved in the database*/
    public Card createFriendRequestCard(AppUser appUser, Long friendID) {
        log.debug("Making a new FriendRequest card");
        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(CardType.FRIEND_REQUEST);
        card.setMetricValue(friendID.intValue());
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database
        card = cardRepository.save(card);
        // add to the user's feed
        Feed feed = appUser.getFeed();
        addCardToFeed(card, feed);
        return card;
    }

    /**Delete any cards notifying a given AppUser of a new friend request from a given friendID.
     * <br>The friend request card automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param friendID the AppUser id of the friend who is requesting to follow you
     */
    public void deleteFriendRequestCards(AppUser appUser, Long friendID) {
        log.debug("Deleting specified FriendRequest cards");
        cardRepository.deleteAllByAppUserIdAndMetricAndMetricValue(appUser.getId(), CardType.FRIEND_REQUEST, friendID.intValue());
    }

    /**Create a card for a new playlist the user has just analysed.
     * If there is already a card made for the user, return Empty.
     * <br>The new playlist card automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param playlistID the id of the newly analysed playlist
     *
     * @return The card once it has been saved in the database, or Empty if the card already exists as a Card for the user.*/
    public Optional<Card> createNewPlaylistCard(AppUser appUser, Long playlistID) {
        log.debug("Making a new NewPlaylist card");

        // Check that the card doesn't already exist in the database
        if (
            cardRepository.findOneByAppUserAndMetricAndMetricValue(appUser, CardType.NEW_PLAYLIST, playlistID.intValue()).isPresent()
        ) return Optional.empty();

        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(CardType.NEW_PLAYLIST);
        card.setMetricValue(playlistID.intValue());
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now());
        // save in database
        card = cardRepository.save(card);
        // add to the user's feed
        Feed feed = appUser.getFeed();
        addCardToFeed(card, feed);
        return Optional.of(card);
    }

    /**Create a milestone card for a given user (a milestone measures "of all time" achievements).
     * <br>The milestone automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param metric what the milestone is measuring. Should only be one of: {@code CardType.NO_OF_FRIENDS, CardType.NO_OF_GENRES_LISTENED, CardType.NO_OF_SONGS_LISTENED, CardType.LISTENING_DURATION}
     * @param value the value of the milestone.
     *
     * @return The card once it has been saved in the database*/
    public Card createMilestone(AppUser appUser, CardType metric, Integer value) {
        log.debug("Making a new Milestone card");
        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(metric);
        card.setMetricValue(value);
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database and return the response
        card = cardRepository.save(card);

        // add to the user's feed
        Feed feed = appUser.getFeed();
        addCardToFeed(card, feed);
        return card;
    }

    /**Create an insight card for a given user. NOTE: For cards without a timespan (milestone cards),
     * use createMilestone instead.
     * @param appUser the user to whom this card belongs.
     * @param metric what the milestone is measuring.
     * @param value the value of the milestone.
     * @param timeFrame what span of time this value measures from (an hour, day, week, or month).
     *
     * @return The card once it has been saved in the database*/
    public Card createInsightCard(AppUser appUser, CardType metric, Integer value, Duration timeFrame) {
        log.debug("Making a new Insight card");

        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(metric);
        card.setMetricValue(value);
        card.setAppUser(appUser);
        card.setTimeFrame(timeFrame);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database and return the response
        return cardRepository.save(card);
    }

    /**Pin a friend for a user. NOTE: This card will not show on any user's feed.
     * It is instead used for the homepage pinned friends feature.
     * @param appUser the user to whom this card belongs.
     * @param friendID the friend that is pinned.
     *
     * @return The card once it has been saved in the database*/
    public Card createPinnedFriendCard(AppUser appUser, Long friendID) {
        log.debug("Making a new PinnedFriend card");

        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(CardType.PINNED_FRIEND);
        card.setMetricValue(friendID.intValue());
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database and return the response
        return cardRepository.save(card);
    }

    /**Unpin a friend for a user.
     * @param appUser the user to whom the card to be deleted belongs.
     * @param friendID the friend that is pinned.
     */
    public void deletePinnedFriendCard(AppUser appUser, Long friendID) {
        log.debug("Deleting specified PinnedFriend card");
        cardRepository.deleteAllByAppUserIdAndMetricAndMetricValue(appUser.getId(), CardType.PINNED_FRIEND, friendID.intValue());
    }

    /** Infers the type of the card given the metrics received from the database
     * @return {@code "friend-update"} if the card belongs to another user<br />
     *         {@code "friend-request"} for new friend requests<br />
     *         {@code "new-friend"} for new friends<br />
     *         {@code "new-playlist"} for newly analysed playlists<br />
     *         {@code "milestone"} if the card has no timeframe & is one of {@code CardType.NO_OF_FRIENDS, CardType.NO_OF_GENRES_LISTENED, CardType.NO_OF_SONGS_LISTENED, CardType.LISTENING_DURATION}<br />
     *         {@code "personal"} for all other cards<br />
     * */
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

        // if there is no timeframe (and also a milestone card type) -> a stat of all time
        if (
            feedCard.getCard().getTimeFrame() == null &&
            List
                .of(
                    new CardType[] {
                        CardType.NO_OF_FRIENDS,
                        CardType.NO_OF_GENRES_LISTENED,
                        CardType.NO_OF_SONGS_LISTENED,
                        CardType.LISTENING_DURATION,
                    }
                )
                .contains(feedCard.getCard().getMetric())
        ) {
            return "milestone";
        }

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

        /**
         * Metric should only be one of: {@code CardType.NO_OF_FRIENDS, CardType.NO_OF_GENRES_LISTENED, CardType.NO_OF_SONGS_LISTENED, CardType.LISTENING_DURATION}
         * -> See #createMilestone and #inferType methods
         */
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
                    icon = "music_note";
                    message = String.format("You have listened to a total of %d songs!", feedCard.getCard().getMetricValue());
                    break;
                }
            case NO_OF_GENRES_LISTENED:
                {
                    icon = "genres";
                    message = String.format("You have listened to a total of %d genres!", feedCard.getCard().getMetricValue());
                    break;
                }
            case NO_OF_FRIENDS:
                {
                    icon = "diversity_1";
                    message = String.format("You now have %d friends!", feedCard.getCard().getMetricValue());
                    href = URI.create("friends");
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
            case TOP_SONG:
                {
                    Optional<Song> song = songRepository.findById(feedCard.getCard().getMetricValue().longValue());
                    if (song.isPresent()) {
                        Hibernate.initialize(song.get().getArtists());
                        if (!song.get().getArtists().isEmpty()) {
                            message =
                                String.format(
                                    "Your top song %s is %s by %s!",
                                    formattedDuration,
                                    song.get().getName(),
                                    song.get().getArtists().iterator().next().getName()
                                );
                        } else {
                            message = String.format("Your top song %s is %s!", formattedDuration, song.get().getName());
                        }
                    } else {
                        message =
                            String.format("Your top song %s is (songID: %d)!", formattedDuration, feedCard.getCard().getMetricValue());
                    }
                    icon = "repeat";
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
        if (duration == null) {
            return "of all time";
        }

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
