package team.bham.service.feed;

import java.io.Serializable;
import java.time.Instant;

public class FeedCardResponse implements Serializable {

    Long id;
    boolean liked;
    Instant timeGenerated;
    BelongsTo belongsTo;

    /** NOT STORED IN DATABASE:
     * The type of card that can be shown on the frontend.
     * There are 5 types of inferred type: "friend-request" | "new-friend" | "milestone" | "personal" | "friend-update"
     * <br> For example: <br>
     * The card pertains to another user -> inferredType = "friend" -> The card will be displayed in the
     * frontend as a friend update.
     * */
    String inferredType;

    /** NOT STORED IN DATABASE:
     * The message shown on the frontend for the card. This message is generated given the card's database values.
     * */
    String generatedMessage;

    /** NOT STORED IN DATABASE:
     * The material symbol icon shown on the frontend for the card.
     * */
    String inferredIcon;

    String linksTo = "#";

    public FeedCardResponse(team.bham.domain.FeedCard feedCard, String inferredType, String generatedMessage, String inferredIcon) {
        id = feedCard.getId();
        liked = feedCard.getLiked();
        timeGenerated = feedCard.getCard().getTimeGenerated();

        this.inferredType = inferredType;
        this.generatedMessage = generatedMessage;
        this.inferredIcon = inferredIcon;

        belongsTo =
            new BelongsTo(
                feedCard.getCard().getAppUser().getId(),
                feedCard.getCard().getAppUser().getInternalUser().getFirstName(),
                feedCard.getCard().getAppUser().getInternalUser().getLastName()
            );
    }

    static class BelongsTo implements Serializable {

        Long id;
        String firstName;
        String lastName;

        BelongsTo(Long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
