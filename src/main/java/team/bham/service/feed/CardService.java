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

/** This service handles the manipulation of Cards. It is solely used to save and delete cards from the database.
 * @apiNote Only use this service for fine-tune manipulation, if the FeedCardService does not do what you want it to do.
 * */
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final Logger log = LoggerFactory.getLogger(CardService.class);

    public CardService(CardRepository cardRepository, FeedService feedService) {
        this.cardRepository = cardRepository;
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
}
