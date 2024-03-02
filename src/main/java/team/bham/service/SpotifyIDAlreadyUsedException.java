package team.bham.service;

public class SpotifyIDAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SpotifyIDAlreadyUsedException() {
        super("This Spotify account is already in use!");
    }
}
