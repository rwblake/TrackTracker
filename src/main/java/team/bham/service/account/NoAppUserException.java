package team.bham.service.account;

public class NoAppUserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoAppUserException() {
        super("The logged-in user has no AppUser associated with it");
    }
}
