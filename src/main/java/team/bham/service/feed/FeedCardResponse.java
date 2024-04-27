package team.bham.service.feed;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;

public class FeedCardResponse implements Serializable {

    private Long id;
    private boolean liked;
    private Instant timeGenerated;
    private BelongsTo belongsTo;

    /** NOT STORED IN DATABASE:
     * The type of card that can be shown on the frontend.
     * There are 6 types of inferred type: "friend-request" | "new-friend" | "milestone" | "personal" | "friend-update | "new-playlist"
     * <br> For example: <br>
     * The card pertains to another user -> inferredType = "friend" -> The card will be displayed in the
     * frontend as a friend update.
     * */
    private String inferredType;

    /** NOT STORED IN DATABASE:
     * The message shown on the frontend for the card. This message is generated given the card's database values.
     * */
    private String generatedMessage;

    /** NOT STORED IN DATABASE:
     * The material symbol icon shown on the frontend for the card.
     * */
    private String inferredIcon;

    private URI href;

    private String imageUrl;

    public FeedCardResponse(
        team.bham.domain.FeedCard feedCard,
        String inferredType,
        String generatedMessage,
        String inferredIcon,
        URI href
    ) {
        this(feedCard, inferredType, generatedMessage, inferredIcon, href, null);
    }

    public FeedCardResponse(
        team.bham.domain.FeedCard feedCard,
        String inferredType,
        String generatedMessage,
        String inferredIcon,
        URI href,
        String imageUrl
    ) {
        id = feedCard.getId();
        liked = feedCard.getLiked();
        timeGenerated = feedCard.getCard().getTimeGenerated();

        this.inferredType = inferredType;
        this.generatedMessage = generatedMessage;
        this.inferredIcon = inferredIcon;
        this.href = href;
        this.imageUrl = imageUrl;

        belongsTo =
            new BelongsTo(
                feedCard.getCard().getAppUser().getId(),
                feedCard.getCard().getAppUser().getInternalUser().getFirstName(),
                feedCard.getCard().getAppUser().getInternalUser().getLastName()
            );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Instant getTimeGenerated() {
        return timeGenerated;
    }

    public void setTimeGenerated(Instant timeGenerated) {
        this.timeGenerated = timeGenerated;
    }

    public BelongsTo getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(BelongsTo belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getInferredType() {
        return inferredType;
    }

    public void setInferredType(String inferredType) {
        this.inferredType = inferredType;
    }

    public String getGeneratedMessage() {
        return generatedMessage;
    }

    public void setGeneratedMessage(String generatedMessage) {
        this.generatedMessage = generatedMessage;
    }

    public String getInferredIcon() {
        return inferredIcon;
    }

    public void setInferredIcon(String inferredIcon) {
        this.inferredIcon = inferredIcon;
    }

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

class BelongsTo implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;

    BelongsTo(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
