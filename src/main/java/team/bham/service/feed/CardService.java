package team.bham.service.feed;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.Card;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final FeedService feedService;
    private final Logger log = LoggerFactory.getLogger(CardService.class);

    public CardService(CardRepository cardRepository, FeedService feedService) {
        this.cardRepository = cardRepository;
        this.feedService = feedService;
    }

    /**Create any type of card by giving all values required for the database.
     * @param appUser the user to whom this card belongs.
     * @param cardType the type of this card (what metric is it displaying?)
     * @param timeFrame what period of time does this metric span over (today, a week, or all time?)
     * @param metricValue what is the value of this metric?
     * @deprecated For better support in the frontend, prefer the case-specific methods
     * (eg. createFriendRequestCard, createMilestone, etc.)
     *
     * @return The card once it has been saved in the database*/
    @Deprecated
    public Card createCard(AppUser appUser, CardType cardType, Duration timeFrame, Integer metricValue) {
        log.debug("Making a new card");
        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(cardType);
        card.setTimeFrame(timeFrame);
        card.setMetricValue(metricValue);
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database and return the response
        return cardRepository.save(card);
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
        feedService.addCardToFeed(card, feed);
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
        feedService.addCardToFeed(card, feed);
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
        feedService.addCardToFeed(card, feed);
        return Optional.of(card);
    }

    /**Create a milestone card for a given user (a milestone measures "of all time" achievements).
     * <br>The milestone automatically gets added to the user's feed.
     * @param appUser the user to whom this card belongs.
     * @param metric what the milestone is measuring.
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
        feedService.addCardToFeed(card, feed);
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
}
