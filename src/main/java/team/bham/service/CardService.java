package team.bham.service;

import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.Card;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**Create a card given the values:
     * @param appUser the user to whom this card belongs.
     * @param cardType the type of this card (what metric is it displaying?)
     * @param timeFrame what period of time does this metric span over (today, a week, or all time?)
     * @param metricValue what is the value of this metric?
     *
     * @return The card which has been saved in the database*/
    public Card createCard(AppUser appUser, CardType cardType, Duration timeFrame, Integer metricValue) {
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

    /**Create a card given the values:
     * @param appUser the user to whom this card belongs.
     * @param cardType the type of this card (what metric is it displaying?)
     * @param metricValue what is the value of this metric?
     *
     * @return The card which has been saved in the database*/
    public Card createCard(AppUser appUser, CardType cardType, Integer metricValue) {
        // create a new card
        Card card = new Card();
        // assign the given values
        card.setMetric(cardType);
        card.setMetricValue(metricValue);
        card.setAppUser(appUser);
        // set the time that the card was generated to now
        card.setTimeGenerated(Instant.now()); // card generated now
        // save in database and return the response
        return cardRepository.save(card);
    }
}
