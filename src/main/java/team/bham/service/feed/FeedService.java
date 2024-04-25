package team.bham.service.feed;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.FeedCardRepository;
import team.bham.repository.FeedRepository;
import team.bham.service.FriendService;
import team.bham.service.InsightsService;
import team.bham.service.spotify.Entry;
import team.bham.service.spotify.StreamInsightsResponse;

@Service
public class FeedService {

    private final FeedCardRepository feedCardRepository;
    private final FeedRepository feedRepository;
    private final FeedCardService feedCardService;
    private final FriendService friendService;
    private final InsightsService insightsService;

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
        // Generate TOP_SONG card...
        // Check if a TOP_SONG card already exists in the feed and its creation time
        if (isCardOfTypePresentAndRecent(appUser, CardType.TOP_SONG)) return; // Skip generation

        List<Stream> streams = new ArrayList<>(appUser.getStreams());
        StreamInsightsResponse insights = insightsService.getInsights(streams);

        // Pick a random number (to decide what to generate)
        Random random = new Random();
        int choice = random.nextInt(3);

        List<Entry<Song>> songs;
        Duration duration;

        switch (choice) {
            case 0:
                songs = insights.getSongCounter().getOfAllTime();
                duration = null;
                break;
            case 1:
                songs = insights.getSongCounter().getByMonth();
                duration = Duration.ofDays(30);
                break;
            default:
                songs = insights.getSongCounter().getByWeek();
                duration = Duration.ofDays(7);
        }

        if (songs.isEmpty()) return;

        Song topSong = songs.get(0).getMetric();
        Card card = feedCardService.createInsightCard(appUser, CardType.TOP_SONG, topSong.getId().intValue(), duration);

        // Automatically add to current user's feed
        feedCardService.addCardToFeed(card, feed);
    }

    /**
     * Checks if a card of the specified type already exists in the user's feed and if it was created within the last few hours.
     *
     * @param appUser The user for whom the feed is being checked.
     * @param cardType The type of card to check for.
     * @return {@code true} if a card of the specified type is present and recent, {@code false} otherwise.
     */
    private boolean isCardOfTypePresentAndRecent(AppUser appUser, CardType cardType) {
        // Get the user's feed
        Feed feed = appUser.getFeed();
        // Get all the cards in the feed
        List<Card> cards = feed.getCards().stream().map(FeedCard::getCard).collect(Collectors.toList());

        // Loop through the existing cards in the feed
        for (Card existingCard : cards) {
            // Check if the card type matches and if it was created within the last few hours
            if (
                existingCard.getMetric() == cardType && existingCard.getAppUser() == appUser && isRecent(existingCard.getTimeGenerated(), 3)
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
     * @return {@code true} if a card of the specified type is present and recent, {@code false} otherwise.
     */
    private boolean isCardOfTypePresentAndRecent(AppUser appUser, CardType cardType, Duration timeFrame) {
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
                (existingCard.getTimeFrame() == timeFrame) && // check time frame if given
                isRecent(existingCard.getTimeGenerated(), 3)
            ) {
                return true; // Card of this type is already present and recent
            }
        }

        return false;
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

            // Add a max of 2 random cards from this friend to the current user's feed
            Collections.shuffle(cards);
            for (int i = 0; i < cards.size() && i < 2; i++) {
                feedCardService.addCardToFeed(cards.get(i), feed);
            }
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
}
