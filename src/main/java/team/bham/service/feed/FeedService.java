package team.bham.service.feed;

import java.util.Set;
import org.springframework.stereotype.Service;
import team.bham.domain.Card;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
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
}
