package team.bham.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
import team.bham.repository.FeedCardRepository;

@Service
public class FeedService {

    private final FeedCardRepository feedCardRepository;

    public FeedService(FeedCardRepository feedCardRepository) {
        this.feedCardRepository = feedCardRepository;
    }

    public Set<FeedCard> getCards(Feed feed) {
        return feedCardRepository.findAllByFeed(feed);
    }
}
