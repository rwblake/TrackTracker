package team.bham.service.feed;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.FeedCardRepository;
import team.bham.repository.FeedRepository;
import team.bham.service.FriendService;
import team.bham.service.InsightsService;
import team.bham.service.spotify.Counter;
import team.bham.service.spotify.Entry;
import team.bham.service.spotify.StreamInsightsResponse;

@Service
public class FeedService {

    private final FeedCardRepository feedCardRepository;
    private final FeedRepository feedRepository;
    private final FeedCardService feedCardService;
    private final FriendService friendService;
    private final InsightsService insightsService;

    private final Random random = new Random();

    public FeedService(
        FeedCardRepository feedCardRepository,
        FeedRepository feedRepository,
        FeedCardService feedCardService,
        FriendService friendService,
        InsightsService insightsService
    ) {
        this.feedCardRepository = feedCardRepository;
        this.feedRepository = feedRepository;
        this.feedCardService = feedCardService;
        this.friendService = friendService;
        this.insightsService = insightsService;
    }

    public Set<FeedCard> getCards(Feed feed) {
        return feedCardRepository.findAllByFeed(feed);
    }

    /** Generate new cards for a given appUser's feed, and update their last_updated timestamp */
    public void updateUsersMusicProfile(AppUser appUser) {
        Feed feed = appUser.getFeed();

        checkStreamMilestones(appUser, feed); // Update stream milestones
        checkGenresMilestones(appUser, feed); // Update genre milestones

        generatePersonalCards(appUser, feed);
        importCards(appUser, feed); // Find random cards from other friends and add them to my feed

        feed.setLastUpdated(Instant.now());
        feedRepository.save(feed);
    }

    /** Generate random personal insight cards for a given appUser, and insert into their feed */
    private void generatePersonalCards(AppUser appUser, Feed feed) {
        /**
         * Generation Algorithm:
         *  - Each time this method is called, tries to generate a new card based on criteria:
         *      - card TYPE must be unique in the past 3 hours (regardless of PERIOD)
         *      - card TYPE, PERIOD combination must be unique in the past 12 hours
         *      - card METRIC VALUE must be different from last time card of same TYPE, PERIOD was generated
         *
         * Guarantees TYPE uniqueness in the past 3 hours
         * Guarantees TYPE, PERIOD uniqueness in the past 12 hours
         * Guarantees metric value different for card of given TYPE, PERIOD
         */

        // Create list of all possible card TYPE choices
        List<CardType> allCardTypeChoices = new ArrayList<>(
            Arrays.asList(
                CardType.LISTENING_DURATION,
                CardType.TOP_GENRE,
                CardType.TOP_ARTIST,
                CardType.TOP_SONG
                //            CardType.NO_OF_FRIENDS, // card metric not possible as no timestamp available for when friendship was accepted
                //            CardType.NO_OF_SONGS_LISTENED // very similar to milestone
                //            CardType.NO_OF_GENRES_LISTENED // very similar to milestone
            )
        );
        // Create list of all possible card PERIOD choices
        List<CardPeriod> allCardPeriodChoices = Arrays.stream(CardPeriod.values()).collect(Collectors.toList());

        // Get all <CardType, CardPeriod> combinations
        List<Pair<CardType, CardPeriod>> allCardTypeAndPeriodChoices = allCardTypeChoices
            .stream()
            .flatMap(cardType -> allCardPeriodChoices.stream().map(period -> new ImmutablePair<>(cardType, period)))
            .collect(Collectors.toCollection(ArrayList::new));

        // Prune card choices that have occurred recently based on below criteria
        allCardTypeAndPeriodChoices.removeIf(cardTypeAndPeriod -> {
            return ( // Prevent generation of any card of the same TYPE in past 3 hours
                isCardOfTypePresentAndRecent(appUser, cardTypeAndPeriod.getLeft(), 3) ||
                // Prevent generation of any card of the same TYPE and PERIOD in the past 12 hours
                isCardOfTypePresentAndRecent(appUser, cardTypeAndPeriod.getLeft(), cardTypeAndPeriod.getRight().getDuration(), 12)
            );
        });

        if (allCardTypeAndPeriodChoices.isEmpty()) return; // No card choices left

        // Randomly pick a TYPE, PERIOD combination based on remaining options
        int randIndex = random.nextInt(allCardTypeAndPeriodChoices.size());
        Pair<CardType, CardPeriod> cardTypeAndPeriodChoice = allCardTypeAndPeriodChoices.get(randIndex);

        // Calculate card metric based on cardTypeChoice and selectedPeriod
        CardType cardTypeChoice = cardTypeAndPeriodChoice.getLeft();
        CardPeriod cardPeriodChoice = cardTypeAndPeriodChoice.getRight();
        Integer metricValue = null;

        switch (cardTypeChoice) {
            case LISTENING_DURATION:
                {
                    Set<Stream> streams = appUser.getStreams();

                    // Filter streams by generated time period
                    Duration selectedPeriodDuration = cardPeriodChoice.getDuration();
                    if (selectedPeriodDuration != null) {
                        // Calculate the Instant to filter the streams by
                        Instant cutoffInstant = Instant.now().minus(selectedPeriodDuration);

                        streams =
                            streams.stream().filter(stream -> stream.getPlayedAt().isAfter(cutoffInstant)).collect(Collectors.toSet());
                    }

                    metricValue = streams.stream().mapToInt(stream -> stream.getSong().getDuration().toMinutesPart()).sum();
                    break;
                }
            case TOP_GENRE:
                {
                    List<Stream> streams = new ArrayList<>(appUser.getStreams());
                    StreamInsightsResponse insights = insightsService.getInsights(streams);

                    Genre insight = pickHighestFromCounterByPeriod(insights.getGenreCounter(), cardPeriodChoice);
                    if (insight == null) return; // counter was probably empty for the selected duration

                    metricValue = insight.getId().intValue();
                    break;
                }
            case TOP_ARTIST:
                {
                    List<Stream> streams = new ArrayList<>(appUser.getStreams());
                    StreamInsightsResponse insights = insightsService.getInsights(streams);

                    Artist insight = pickHighestFromCounterByPeriod(insights.getArtistCounter(), cardPeriodChoice);
                    if (insight == null) return; // counter was probably empty for the selected duration

                    metricValue = insight.getId().intValue();
                    break;
                }
            case TOP_SONG:
                {
                    List<Stream> streams = new ArrayList<>(appUser.getStreams());
                    StreamInsightsResponse insights = insightsService.getInsights(streams);

                    Song insight = pickHighestFromCounterByPeriod(insights.getSongCounter(), cardPeriodChoice);
                    if (insight == null) return; // counter was probably empty for the selected duration

                    metricValue = insight.getId().intValue();
                    break;
                }
            //            case NO_OF_FRIENDS: {
            //                break;
            //            }
            //            case NO_OF_SONGS_LISTENED: {
            //                break;
            //            }
            //            case NO_OF_GENRES_LISTENED: {
            //                break;
            //            }

        }

        // Calculation of metric was successful
        if (metricValue != null) {
            // Don't create card if metric value hasn't changed since last card of this TYPE and PERIOD
            Optional<Card> latestCardOfTypeAndPeriod = getLatestCardOfTypeAndPeriod(
                appUser,
                cardTypeChoice,
                cardPeriodChoice.getDuration()
            );
            if (latestCardOfTypeAndPeriod.isPresent() && latestCardOfTypeAndPeriod.get().getMetricValue().equals(metricValue)) return;

            // Automatically add to current user's feed
            Card generatedCard = feedCardService.createInsightCard(appUser, cardTypeChoice, metricValue, cardPeriodChoice.getDuration());
            feedCardService.addCardToFeed(generatedCard, feed);
        }
    }

    /**
     * Checks if a card of the specified type already exists in the user's feed and if it was created within the last few hours.
     *
     * @param appUser The user for whom the feed is being checked.
     * @param cardType The type of card to check for.
     * @param threshold The threshold in hours to consider a card as recent.
     * @return {@code true} if a card of the specified type is present and recent, {@code false} otherwise.
     */
    private boolean isCardOfTypePresentAndRecent(AppUser appUser, CardType cardType, int threshold) {
        // Get the user's feed
        Feed feed = appUser.getFeed();
        // Get all the cards in the feed
        List<Card> cards = feed.getCards().stream().map(FeedCard::getCard).collect(Collectors.toList());

        // Loop through the existing cards in the feed
        for (Card existingCard : cards) {
            // Check if the card type matches and if it was created within the last few hours
            if (
                existingCard.getMetric() == cardType &&
                existingCard.getAppUser() == appUser &&
                isRecent(existingCard.getTimeGenerated(), threshold)
            ) {
                return true; // Card of this type is already present and recent
            }
        }

        return false;
    }

    /**
     * Checks if a card of the specified type already exists in the user's feed and if it was created within the last few hours.
     *
     * @param appUser The user for whom the feed is being checked.
     * @param cardType The type of card to check for.
     * @param timeFrame The timeFrame of the card
     * @param threshold The threshold in hours to consider a card as recent.
     * @return {@code true} if a card of the specified type is present and recent, {@code false} otherwise.
     */
    private boolean isCardOfTypePresentAndRecent(AppUser appUser, CardType cardType, Duration timeFrame, int threshold) {
        // Get the user's feed
        Feed feed = appUser.getFeed();
        // Get all the cards in the feed
        List<Card> cards = feed.getCards().stream().map(FeedCard::getCard).collect(Collectors.toList());

        // Loop through the existing cards in the feed
        for (Card existingCard : cards) {
            // Check if the card type matches and if it was created within the last few hours
            if (
                existingCard.getMetric() == cardType &&
                existingCard.getAppUser() == appUser &&
                existingCard.getTimeFrame() == timeFrame && // check time frame if given
                isRecent(existingCard.getTimeGenerated(), threshold)
            ) {
                return true; // Card of this type is already present and recent
            }
        }

        return false;
    }

    private Optional<Card> getLatestCardOfTypeAndPeriod(AppUser appUser, CardType cardType, Duration timeFrame) {
        // Get the user's feed
        Feed feed = appUser.getFeed();

        return feed // Get all the cards in the feed
            .getCards()
            .stream()
            .map(FeedCard::getCard)
            // filter by matching user, card type and card period
            .filter(card -> card.getMetric() == cardType && card.getAppUser() == appUser && card.getTimeFrame() == timeFrame)
            // find max timeGenerated (latest card)
            .max(Comparator.comparing(Card::getTimeGenerated));
    }

    /**
     * Checks if the given creation time is recent, based on a specified time threshold.
     *
     * @param creationTime The creation time of the card.
     * @param threshold The threshold in hours to consider a card as recent.
     * @return {@code true} if the creation time is within the recent threshold, {@code false} otherwise.
     */
    private boolean isRecent(Instant creationTime, int threshold) {
        // Define the time threshold for considering a card as recent (e.g., 3 hours)
        Duration recentThreshold = Duration.ofHours(threshold);
        // Calculate the time difference between now and the card's creation time
        Duration timeDifference = Duration.between(creationTime, Instant.now());
        // Check if the time difference is within the recent threshold
        return timeDifference.compareTo(recentThreshold) <= 0;
    }

    /** Import other users' personal insight cards (friend updates) from today */
    private void importCards(AppUser appUser, Feed feed) {
        // Get friends
        List<AppUser> friends = friendService.getFriends(appUser).stream().map(Friend::getFriendAppUser).collect(Collectors.toList());

        Instant yesterday = Instant.now().minus(Duration.ofDays(1));

        // Pick some potential friends cards
        List<Card> friendCards = new ArrayList<>();
        for (AppUser friend : friends) {
            // Access cards which pertain to this user
            List<Card> cards = new ArrayList<>(friend.getCards());

            // Remove the irrelevant ones
            cards.removeIf(card -> {
                CardType metric = card.getMetric();
                boolean alreadyAdded = appUser.getFeed().getCards().stream().anyMatch(card1 -> card1.getCard().equals(card));
                // Removal criteria
                return (
                    alreadyAdded ||
                    card.getTimeGenerated().isBefore(yesterday) ||
                    card.getTimeFrame() == null ||
                    metric == CardType.FRIEND_REQUEST ||
                    metric == CardType.NEW_FRIEND ||
                    metric == CardType.NEW_PLAYLIST ||
                    metric == CardType.NO_OF_FRIENDS ||
                    metric == CardType.PINNED_FRIEND
                );
            });

            if (cards.isEmpty()) continue;

            // Pick a max of 2 random cards from this friend to the pool of possible friend cards
            Collections.shuffle(cards);
            for (int i = 0; i < cards.size() && i < 2; i++) {
                friendCards.add(cards.get(i));
            }
        }

        if (friendCards.isEmpty()) return;

        // Card quantity generation algorithm: https://www.desmos.com/calculator/e67u5nhwid
        // Pick between 0.5*log_1.5(N) and log_1.5(N) cards
        int noOfFriends = friends.size(); // == N
        double maxCardsExact = Math.log(noOfFriends) / Math.log(1.5); // == log_1.5(N)
        int maxCards = Math.max(1, (int) Math.floor(maxCardsExact)); // == max(1, floor( log_1.5(N) ))
        int minCards = Math.max(1, (int) Math.floor(0.5 * maxCardsExact)); // == max(1, floor( 0.5 * log_1.5(N) ))

        int cardAmount = minCards;
        if (maxCards > minCards) {
            cardAmount += random.nextInt((maxCards - minCards) + 1);
        }

        Collections.shuffle(friendCards);
        for (int i = 0; i < friendCards.size() && i < cardAmount; i++) {
            feedCardService.addCardToFeed(friendCards.get(i), feed);
        }
    }

    /** Generate new cards for any streaming milestones reached */
    private void checkStreamMilestones(AppUser appUser, Feed feed) {
        // A new streams milestone card is generated when the user reaches a multiple of this number
        final int milestoneFrequency = 25;

        // Count all the different streams the user has listened to
        int streamCount = appUser.getStreams().size();

        int maxStreamsMilestone = (streamCount / milestoneFrequency) * milestoneFrequency;

        // Get a list of all the user's cards which measure NO_OF_SONGS_LISTENED
        List<FeedCard> milestoneCards = feed
            .getCards()
            // Filter out any irrelevant cards
            .stream()
            .filter(feedCard -> feedCard.getCard().getMetric() == CardType.NO_OF_SONGS_LISTENED)
            // Sort by metric value
            .sorted(Comparator.comparingInt(a -> a.getCard().getMetricValue()))
            .collect(Collectors.toList());

        // Get the highest currently recorded milestone streams value
        Integer highestStreamsRecorded;

        if (milestoneCards.isEmpty()) {
            highestStreamsRecorded = 0;
        } else {
            highestStreamsRecorded = milestoneCards.get(milestoneCards.size() - 1).getCard().getMetricValue();
        }

        highestStreamsRecorded += milestoneFrequency; // Needed to prevent an already-made milestone from being made

        // Calculate the steps to add milestones for, between the min and max values
        List<Integer> valuesToAdd = IntStream
            .rangeClosed(highestStreamsRecorded, maxStreamsMilestone)
            .filter(num -> num % milestoneFrequency == 0)
            .boxed()
            .collect(Collectors.toList());

        // Check how many values need to be added
        // (to prevent massive amounts of cards being added in batch)
        if (valuesToAdd.size() > 2) {
            // only add the last value
            Integer value = valuesToAdd.get(valuesToAdd.size() - 1);
            feedCardService.createMilestone(appUser, CardType.NO_OF_SONGS_LISTENED, value);
            return;
        }

        // For each of these steps create a milestone
        for (Integer value : valuesToAdd) {
            feedCardService.createMilestone(appUser, CardType.NO_OF_SONGS_LISTENED, value);
        }
    }

    /** Generate new cards for any genre milestones reached */
    private void checkGenresMilestones(AppUser appUser, Feed feed) {
        // A new NO_OF_SONGS_LISTENED milestone card is generated
        // when the user reaches a multiple of this number
        final int milestoneFrequency = 100; // this needs to be big since artists have many genres

        // Count all the different genres the user has listened to
        int genresCount = (int) appUser
            .getStreams()
            .stream()
            .flatMap(stream -> stream.getSong().getArtists().stream())
            .flatMap(artist -> artist.getGenres().stream())
            .count(); // Don't convert this to mapToLong as it will count duplicates

        int maxGenresMilestone = (genresCount / milestoneFrequency) * milestoneFrequency;

        // Get a list of all the user's cards which measure NO_OF_SONGS_LISTENED
        List<FeedCard> milestoneCards = feed
            .getCards()
            // Filter out any irrelevant cards
            .stream()
            .filter(feedCard -> feedCard.getCard().getMetric() == CardType.NO_OF_GENRES_LISTENED)
            // Sort by metric value
            .sorted(Comparator.comparingInt(a -> a.getCard().getMetricValue()))
            .collect(Collectors.toList());

        // Get the highest currently recorded milestone value
        Integer highestGenresRecorded;
        if (milestoneCards.isEmpty()) {
            highestGenresRecorded = 0;
        } else {
            highestGenresRecorded = milestoneCards.get(milestoneCards.size() - 1).getCard().getMetricValue();
        }

        highestGenresRecorded += milestoneFrequency; // Needed to prevent an already-made milestone from being made

        // Calculate the steps to add milestones for, between the min and max values
        List<Integer> valuesToAdd = IntStream
            .rangeClosed(highestGenresRecorded, maxGenresMilestone)
            .filter(num -> num % milestoneFrequency == 0)
            .boxed()
            .collect(Collectors.toList());

        // Check how many values need to be added
        // (to prevent massive amounts of cards being added in batch)
        if (valuesToAdd.size() > 2) {
            // only add the last value
            Integer value = valuesToAdd.get(valuesToAdd.size() - 1);
            feedCardService.createMilestone(appUser, CardType.NO_OF_GENRES_LISTENED, value);
            return;
        }

        // For each of these steps create a milestone
        for (Integer value : valuesToAdd) {
            feedCardService.createMilestone(appUser, CardType.NO_OF_GENRES_LISTENED, value);
        }
    }

    // Utility methods

    enum CardPeriod {
        WEEK,
        MONTH,
        ALL_TIME;

        public Duration getDuration() {
            switch (this) {
                case WEEK:
                    return Duration.ofDays(7);
                case MONTH:
                    return Duration.ofDays(30);
                case ALL_TIME:
                default:
                    return null;
            }
        }
    }

    private <T> T pickHighestFromCounterByPeriod(Counter<T> counter, CardPeriod period) {
        List<Entry<T>> collection;

        switch (period) {
            case MONTH:
                collection = counter.getByMonth();
                break;
            case WEEK:
                collection = counter.getByWeek();
                break;
            case ALL_TIME:
            default:
                collection = counter.getOfAllTime();
                break;
        }

        if (collection.isEmpty()) return null;

        return collection.get(collection.size() - 1).getMetric();
    }
}
