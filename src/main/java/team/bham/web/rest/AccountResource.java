package team.bham.web.rest;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import team.bham.domain.AppUser;
import team.bham.domain.SpotifyToken;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.repository.UserRepository;
import team.bham.security.SecurityUtils;
import team.bham.service.DeclinedSpotifyAccessException;
import team.bham.service.MailService;
import team.bham.service.UserService;
import team.bham.service.account.AppUserService;
import team.bham.service.dto.AdminUserDTO;
import team.bham.service.dto.PasswordChangeDTO;
import team.bham.service.spotify.SpotifyAuthorisationService;
import team.bham.web.rest.errors.*;
import team.bham.web.rest.vm.KeyAndPasswordVM;
import team.bham.web.rest.vm.ManagedAppUserVM;
import team.bham.web.rest.vm.ManagedUserVM;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final AppUserService appUserService;

    private final UserService userService;

    private final MailService mailService;

    private final SpotifyAuthorisationService spotifyAuthorisationService;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        AppUserService appUserService,
        MailService mailService,
        SpotifyAuthorisationService spotifyAuthorisationService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.appUserService = appUserService;
        this.mailService = mailService;
        this.spotifyAuthorisationService = spotifyAuthorisationService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedAppUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     * @throws DeclinedSpotifyAccessException {@code 400 (Bad Request)} if the user declined access to Spotify account.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedAppUserVM managedAppUserVM, HttpServletRequest request)
        throws IOException, ParseException, SpotifyWebApiException {
        // Get origin of request
        URI requestUri = URI.create(request.getRequestURL().toString());
        String origin = requestUri.getScheme() + "://" + requestUri.getAuthority();

        // Regenerate redirectUri for SpotifyAPI
        URI redirectUri = SpotifyHttpManager.makeUri(origin + "/spotify-callback/");

        if (isPasswordLengthInvalid(managedAppUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }

        // attempt to generate credentials
        AuthorizationCodeCredentials credentials = spotifyAuthorisationService.initialiseCredentials(
            managedAppUserVM.getSpotifyAuthCode(),
            managedAppUserVM.getSpotifyAuthState(),
            redirectUri
        );
        if (credentials == null) {
            throw new Error(
                "Could not initialise Credentials given: " +
                Arrays.toString(new String[] { managedAppUserVM.getSpotifyAuthCode(), managedAppUserVM.getSpotifyAuthState() })
            );
        }

        SpotifyApi spotifyApi = spotifyAuthorisationService.getAPI(credentials);

        System.out.println("Created Spotify API object with Access Token: " + spotifyApi.getAccessToken());

        // once credentials generated, get SpotifyUserID using credentials
        String spotifyUserID = spotifyApi.getCurrentUsersProfile().build().execute().getId();

        // Save user in database
        AppUser appUser = appUserService.registerAppUser(managedAppUserVM, managedAppUserVM.getPassword(), spotifyUserID, credentials);
        // Send email to verify account TODO: Uncomment this once authorisation via email works
        //        mailService.sendActivationEmail(appUser.getInternalUser());
    }

    /**
     * {@code GET /spotify/authentication_uri} : get the Spotify account authentication URI
     */
    @GetMapping("/spotify/authentication_uri")
    @ResponseStatus(HttpStatus.OK)
    public URI getAuthenticationURI(HttpServletRequest request) {
        URI requestUri = URI.create(request.getRequestURL().toString());
        String origin = requestUri.getScheme() + "://" + requestUri.getAuthority();
        return spotifyAuthorisationService.getAuthorisationCodeUri(origin);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    @GetMapping("/account-user")
    public AppUser getAccountUser() {
        User internalAppUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
        AppUser appUser = appUserService.getAppUser(internalAppUser).get();
        System.out.println("BackEnd" + appUser);
        System.out.println(appUser.getBio());
        return appUser;
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (user.isEmpty()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
