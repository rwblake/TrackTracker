package team.bham.service.feed;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import team.bham.domain.FeedCard;
import team.bham.domain.enumeration.CardType;

@Service
public class FeedCardService {

    /** Converts backend cards into a format that the frontend can readily display */
    public List<FeedCardResponse> generateFrontendCards(Set<FeedCard> feedCards) {
        return feedCards
            .stream()
            .map(feedCard -> {
                String inferredType = inferType(feedCard);
                String generatedMessage = generateFeedCardMessage(feedCard, inferredType);
                String inferredIcon = inferIcon(feedCard.getCard().getMetric(), inferredType);
                return new FeedCardResponse(feedCard, inferredType, generatedMessage, inferredIcon);
            })
            .collect(Collectors.toList());
    }

    /** Generates the message to be shown on the card */
    String generateFeedCardMessage(FeedCard feedCard, String inferredType) throws InvalidInferredCardTypeException {
        switch (inferredType) {
            case "milestone":
                {
                    switch (feedCard.getCard().getMetric()) {
                        case LISTENING_DURATION:
                            return String.format("You have spent %d minutes listening to Spotify!", feedCard.getCard().getMetricValue());
                        case NO_OF_SONGS_LISTENED:
                            return String.format("You have listened to %d songs!", feedCard.getCard().getMetricValue());
                        case NO_OF_FRIENDS:
                            return String.format("You now have %d friends!", feedCard.getCard().getMetricValue());
                        case TOP_ARTIST:
                            return String.format("Your top genre of all time is (artistID: %d)!", feedCard.getCard().getMetricValue());
                        default:
                            return "Not yet implemented";
                    }
                }
            case "new-playlist":
                {
                    return String.format("You analysed a new playlist: (ID %s)", feedCard.getId());
                }
            case "personal":
            case "friend-update":
            case "friend-request":
            case "new-friend":
                {
                    return "Not yet implemented: " + feedCard.getCard().getMetric().toString() + ", " + feedCard.getCard().getMetricValue();
                }
            default:
                throw new InvalidInferredCardTypeException();
        }
    }

    /** Infers which icon to show based on the CardType and inferred type */
    String inferIcon(CardType cardType, String inferredType) {
        if (cardType == CardType.NEW_PLAYLIST) return "queue_music";

        return "music_note";
    }

    /** Infers the type of the card given the metrics received from the database */
    String inferType(FeedCard feedCard) {
        // if the card belongs to another user
        if (
            feedCard.getFeed().getAppUser().getId().longValue() != feedCard.getCard().getAppUser().getId().longValue()
        ) return "friend-update";

        switch (feedCard.getCard().getMetric()) {
            case FRIEND_REQUEST:
                return "friend-request";
            case NEW_FRIEND:
                return "new-friend";
            case NEW_PLAYLIST:
                return "new-playlist";
        }

        // there is now no timeframe, and card is not a friend update (or request / new friend)

        // if there is no timeframe -> a stat of all time
        if (feedCard.getCard().getTimeFrame() == null) return "milestone";

        // return personal for all else
        return "personal";
    }
}
