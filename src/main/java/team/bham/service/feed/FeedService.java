package team.bham.service.feed;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.FeedCardRepository;
import team.bham.repository.FeedRepository;

@Service
public class FeedService {

    private final FeedCardRepository feedCardRepository;
    private final FeedRepository feedRepository;
    private final FeedCardService feedCardService;

    public FeedService(FeedCardRepository feedCardRepository, FeedRepository feedRepository, FeedCardService feedCardService) {
        this.feedCardRepository = feedCardRepository;
        this.feedRepository = feedRepository;
        this.feedCardService = feedCardService;
    }

    public Set<FeedCard> getCards(Feed feed) {
        return feedCardRepository.findAllByFeed(feed);
    }

    /** Refreshes the user's feed with new cards, and updates the timestamp for last Music Profile update */
    public void updateUsersMusicProfile(AppUser appUser) {
        Feed feed = appUser.getFeed();

        checkStreamMilestones(appUser, feed); // Update stream milestones
        checkGenresMilestones(appUser, feed); // Update genre milestones

        feed.setLastUpdated(Instant.now());
        feedRepository.save(feed);
    }

    private void checkStreamMilestones(AppUser appUser, Feed feed) {
        // A new streams milestone card is generated when the user reaches a multiple of this number
        final int milestoneFrequency = 10;
        int streamCount = appUser.getStreams().size();

        int maxStreamsMilestone = (streamCount / milestoneFrequency) * milestoneFrequency;
        //        boolean exists = false;

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

        // For each of these steps create a milestone
        for (Integer value : valuesToAdd) {
            feedCardService.createMilestone(appUser, CardType.NO_OF_SONGS_LISTENED, value);
        }
        //
        //        for (FeedCard card : milestoneCards) {
        //            Card cardData = card.getCard();
        //            if (cardData.getMetricValue() == maxStreamsMilestone) {
        //                // The milestone has already been reached. Don't generate a new card.
        //                exists = true;
        //                break;
        //            }
        //        }
        //
        //        if (!exists) {
        //
        //        }
    }

    private void checkGenresMilestones(AppUser appUser, Feed feed) {
        // A new streams milestone card is generated when the user reaches a multiple of this number
        final int milestoneFrequency = 10;
        // Count all the different genres the user has listened to
        int genresCount = (int) appUser
            .getStreams()
            .stream()
            .flatMap(stream -> stream.getSong().getArtists().stream())
            .flatMap(artist -> artist.getGenres().stream())
            .count();

        int maxGenresMilestone = (genresCount / milestoneFrequency) * milestoneFrequency;
        //        boolean exists = false;

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

        // For each of these steps create a milestone
        for (Integer value : valuesToAdd) {
            feedCardService.createMilestone(appUser, CardType.NO_OF_GENRES_LISTENED, value);
        }
        //
        //        for (FeedCard card : milestoneCards) {
        //            Card cardData = card.getCard();
        //            if (cardData.getMetricValue() == maxGenresMilestone) {
        //                // The milestone has already been reached. Don't generate a new card.
        //                exists = true;
        //                break;
        //            }
        //        }
        //
        //        if (!exists) {
        //
        //        }
    }
}
