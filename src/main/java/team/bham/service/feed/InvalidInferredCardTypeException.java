package team.bham.service.feed;

public class InvalidInferredCardTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidInferredCardTypeException() {
        super(
            "Invalid inferred card type. " +
            "An inferred card type can only be one of: " +
            "'friend-request', 'new-friend', 'milestone', 'personal', or 'friend-update'"
        );
    }
}
