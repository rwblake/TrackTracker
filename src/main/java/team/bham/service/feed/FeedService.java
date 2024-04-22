package team.bham.service.feed;

import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.Card;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.FeedCardRepository;
import team.bham.repository.FeedRepository;

@Service
public class FeedService {

    private final FeedCardRepository feedCardRepository;
    private final FeedRepository feedRepository;

    public FeedService(FeedCardRepository feedCardRepository, FeedRepository feedRepository) {
        this.feedCardRepository = feedCardRepository;
        this.feedRepository = feedRepository;
    }

    public Set<FeedCard> getCards(Feed feed) {
        return feedCardRepository.findAllByFeed(feed);
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

    /** Refreshes the user's feed with new cards, and updates the timestamp for last Music Profile update */
    public void updateUsersMusicProfile(AppUser appUser) {
        Feed feed = appUser.getFeed();
        // checkStreamMilestones(appUser, feed);
        feed.setLastUpdated(Instant.now());
        feedRepository.save(feed);
    }

    public void checkStreamMilestones(AppUser appUser, Feed feed) {
        // A new streams milestone card is generated when the user reaches a multiple of this number
        final int milestoneFrequency = 5;
        int streamCount = appUser.getStreams().size();
        int milestoneAmount = (streamCount / milestoneFrequency) * milestoneFrequency;
        boolean exists = false;

        for (FeedCard card : feed.getCards()) {
            Card cardData = card.getCard();
            if (cardData.getMetric() == CardType.NO_OF_SONGS_LISTENED && cardData.getMetricValue() == milestoneAmount) {
                // The milestone has already been reached. Don't generate a new card.
                exists = true;
                break;
            }
        }

        if (!exists) {
            // cardService.createMilestone(appUser, CardType.NO_OF_SONGS_LISTENED, milestoneAmount);
        }
    }
}
