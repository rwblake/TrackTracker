package team.bham.web.rest.vm;

import javax.validation.constraints.Size;
import team.bham.service.dto.AdminUserDTO;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 * This Class lists the fields an AppUser can have (extending the default User fields).
 * It needs to mirror register.model.ts in frontend
 */
public class ManagedAppUserVM extends AdminUserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    public static final int BIO_MAX_LENGTH = 200;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Size(max = 120)
    private String firstName;

    @Size(max = 120)
    private String lastName;

    private String spotifyAuthCode;

    private String spotifyAuthState;

    public ManagedAppUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpotifyAuthCode() {
        return spotifyAuthCode;
    }

    public void setSpotifyAuthCode(String spotifyAuthCode) {
        this.spotifyAuthCode = spotifyAuthCode;
    }

    public String getSpotifyAuthState() {
        return spotifyAuthState;
    }

    public void setSpotifyAuthState(String spotifyAuthState) {
        this.spotifyAuthState = spotifyAuthState;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedAppUserVM{" + super.toString() + "} ";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }
}
