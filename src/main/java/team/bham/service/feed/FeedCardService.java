package team.bham.service.feed;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    public FeedCardService(
        PlaylistRepository playlistRepository,
        AppUserRepository appUserRepository,
        CardRepository cardRepository,
        FeedRepository feedRepository,
        SongRepository songRepository,
        ArtistRepository artistRepository,
        GenreRepository genreRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.appUserRepository = appUserRepository;
        this.cardRepository = cardRepository;
        this.feedRepository = feedRepository;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
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

    /** Sorts the backend cards and converts each of them into a format that the frontend can readily display */
    public List<FeedCardResponse> generateFrontendCards(Set<FeedCard> feedCards) {
        return feedCards
            .stream()
            .sorted(Comparator.comparing(a -> a.getCard().getTimeGenerated()))
            .map(feedCard -> {
                // Decide which method to handle the FeedCard
                try {
                    // If the card belongs to another user -> ge
                    if (feedCard.getFeed().getAppUser().getId().longValue() != feedCard.getCard().getAppUser().getId().longValue()) {
                        return generateFrontendFriendUpdateCard(feedCard);
                    }

                    switch (feedCard.getCard().getMetric()) {
                        case FRIEND_REQUEST:
                            return generateFrontendFriendRequestCard(feedCard);
                        case NEW_FRIEND:
                            return generateFrontendNewFriendCard(feedCard);
                        case NEW_PLAYLIST:
                            return generateFrontendPlaylistCard(feedCard);
                        case NO_OF_FRIENDS:
                        case NO_OF_GENRES_LISTENED:
                        case NO_OF_SONGS_LISTENED:
                            {
                                // if there is no timeframe (and also a milestone card type) -> a stat of all time
                                if (feedCard.getCard().getTimeFrame() == null) {
                                    return generateFrontendMilestoneCard(feedCard);
                                }
                            }
                        default:
                            // there is now no timeframe, and card is not a friend update (or request / new friend)
                            return generateFrontendPersonalCard(feedCard);
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
     * @param metric what the milestone is measuring. Should only be one of: {@code CardType.NO_OF_FRIENDS, CardType.NO_OF_GENRES_LISTENED, CardType.NO_OF_SONGS_LISTENED}
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
        Optional<Card> existingCard = cardRepository.findOneByAppUserAndMetricAndMetricValue(
            appUser,
            CardType.PINNED_FRIEND,
            friendID.intValue()
        );

        // If a card already exists, return it
        if (existingCard.isPresent()) {
            log.debug("PinnedFriend card already exists for AppUser {} and friendID {}", appUser.getId(), friendID);
            return existingCard.get();
        }

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

    // Front-End card generation methods

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
         * Metric should only be one of: {@code CardType.NO_OF_FRIENDS, CardType.NO_OF_GENRES_LISTENED, CardType.NO_OF_SONGS_LISTENED}
         * -> See #generateFrontendCards method
         */
        switch (feedCard.getCard().getMetric()) {
            case NO_OF_FRIENDS:
                {
                    icon = "diversity_1";
                    message = String.format("You now have %d friends!", feedCard.getCard().getMetricValue());
                    href = URI.create("friends");
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

        /**
         * Only card metrics generated currently are {@code TOP_SONG}
         *
         * However, possible to be called on any of:
         *  {@code
         *  LISTENING_DURATION,
         *  TOP_GENRE,
         *  TOP_ARTIST,
         *  TOP_SONG,
         *  NO_OF_FRIENDS,
         *  NO_OF_SONGS_LISTENED,
         *  NO_OF_GENRES_LISTENED,

         *  PLAYLIST_HAPPIEST_SONG,
         *  PLAYLIST_MOST_ENERGETIC_SONG,
         *  PLAYLIST_SUMS_UP,
         *  GENRE,
         *  PINNED_FRIEND,
         *  }
         */
        switch (feedCard.getCard().getMetric()) {
            case LISTENING_DURATION:
                {
                    icon = "schedule";
                    message =
                        String.format(
                            "%s you have spent %s listening to Spotify!",
                            capitalise(formattedDuration),
                            formatListeningMinutes(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_GENRE:
                {
                    icon = "genres";
                    message =
                        String.format(
                            "Your top genre %s is %s!",
                            formattedDuration,
                            getGenreDisplayName(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_ARTIST:
                {
                    icon = "artist";
                    message =
                        String.format(
                            "Your top artist %s is %s!",
                            formattedDuration,
                            getArtistDisplayName(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_SONG:
                {
                    icon = "repeat";
                    message =
                        String.format(
                            "Your top song %s is %s!",
                            formattedDuration,
                            getSongDisplayName(feedCard.getCard().getMetricValue().longValue())
                        );
                    break;
                }
            case NO_OF_FRIENDS:
                {
                    icon = "user-group";
                    message = String.format("%s you've made %s friends!", feedCard.getCard().getMetricValue());
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
            case NO_OF_GENRES_LISTENED:
                {
                    icon = "numbers";
                    message =
                        String.format(
                            "%s you have listened to %d genres!",
                            capitalise(formattedDuration),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            // Unused but *technically* possible types to be called
            case PLAYLIST_HAPPIEST_SONG:
            case PLAYLIST_MOST_ENERGETIC_SONG:
            case PLAYLIST_SUMS_UP:
            case GENRE:
            case PINNED_FRIEND:
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

        /**
         * Following shouldn't be added as friends cards:
         * {@code
         *  FRIEND_REQUEST
         *  NEW_FRIEND
         *  NEW_PLAYLIST
         *  NO_OF_FRIENDS
         *  PINNED_FRIEND
         * }
         */
        switch (feedCard.getCard().getMetric()) {
            // All Criteria
            case LISTENING_DURATION:
                {
                    icon = "schedule";
                    message =
                        String.format(
                            "%s %s has spent %s listening to Spotify!",
                            capitalise(formattedDuration),
                            owner.getFirstName(),
                            formatListeningMinutes(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_GENRE:
                {
                    icon = "genres";
                    message =
                        String.format(
                            "%s's top genre %s is %s!",
                            owner.getFirstName(),
                            formattedDuration,
                            getGenreDisplayName(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_ARTIST:
                {
                    icon = "artist";
                    message =
                        String.format(
                            "%s's top artist %s is %s!",
                            owner.getFirstName(),
                            formattedDuration,
                            getArtistDisplayName(feedCard.getCard().getMetricValue())
                        );
                    break;
                }
            case TOP_SONG:
                {
                    icon = "repeat";
                    message =
                        String.format(
                            "%s's top song %s is %s!",
                            owner.getFirstName(),
                            formattedDuration,
                            getSongDisplayName(feedCard.getCard().getMetricValue().longValue())
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
            case NO_OF_GENRES_LISTENED:
                {
                    icon = "genres";
                    message =
                        String.format(
                            "%s %s has listened to a total of %d genres!",
                            capitalise(formattedDuration),
                            owner.getFirstName(),
                            feedCard.getCard().getMetricValue()
                        );
                    break;
                }
            // Unused but *technically* possible types to be called
            case PLAYLIST_HAPPIEST_SONG:
            case PLAYLIST_MOST_ENERGETIC_SONG:
            case PLAYLIST_SUMS_UP:
            case GENRE:
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

    // Utility Methods

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

        if (duration.toDays() == 30 || duration.toDays() == 31) {
            return "this month";
        }

        long weeks = duration.toDays() / 7;
        return "in the last " + weeks + " week" + (weeks > 1 ? "s" : "");
    }

    private String formatListeningMinutes(int minutes) {
        if (minutes < 60) {
            return String.format("%d minutes", minutes);
        }

        int hours = Math.floorDiv(minutes, 60);
        minutes -= hours * 60;

        if (hours < 24) {
            return String.format("%d hours and %d minutes", hours, minutes);
        }

        int days = Math.floorDiv(hours, 24);
        hours -= days * 24;

        return String.format("%d days and %d hours", days, hours);
    }

    private String getSongDisplayName(long songId) {
        Optional<Song> songById = songRepository.findById(songId);

        if (songById.isEmpty()) {
            return String.format("(songID: %d)", songId);
        }

        Song song = songById.get();
        Hibernate.initialize(song.getArtists());

        if (song.getArtists().isEmpty()) {
            String.format("%s", song.getName());
        }

        return String.format("%s by %s", song.getName(), song.getArtists().iterator().next().getName());
    }

    private String getArtistDisplayName(long artistId) {
        Optional<Artist> artistById = artistRepository.findById(artistId);

        if (artistById.isEmpty()) {
            return String.format("(artistId: %d)", artistId);
        }

        return artistById.get().getName();
    }

    private String getGenreDisplayName(long genreId) {
        Optional<Genre> genreById = genreRepository.findById(genreId);

        if (genreById.isEmpty()) {
            return String.format("(genreId: %d)", genreId);
        }

        return genreById.get().getName();
    }
}
