package team.bham.service.spotify;

public class DeclinedSpotifyAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeclinedSpotifyAccessException() {
        super("Credentials not generated - need to prompt user to accept, or abort account creation");
    }
}
