package team.bham.service.spotify;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import javax.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import team.bham.domain.AppUser;
import team.bham.domain.SpotifyToken;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.service.UserService;
import team.bham.service.account.AppUserService;

/** Handles authorising a spotify account, getting tokens and refreshing tokens. */
@Service
public class SpotifyAuthorisationService {

    // Static - can be shared between authentication requests
    private static final String clientId = CredentialsParser.parseCredentials()[0];
    private static final String clientSecret = CredentialsParser.parseCredentials()[1];

    private static final String scope =
        "playlist-read-private, user-follow-read," +
        "user-read-playback-position, user-top-read, " +
        "user-read-recently-played, user-library-read, " +
        "user-read-email";

    // Non-Static - specific to each authentication request
    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final TokenRefresher tokenRefresher;
    private final HttpSession httpSession; // Allows access to each Http session

    public SpotifyAuthorisationService(
        UserService userService,
        AppUserRepository appUserRepository,
        TokenRefresher tokenRefresher,
        HttpSession httpSession
    ) {
        this.userService = userService;
        this.appUserRepository = appUserRepository;
        this.tokenRefresher = tokenRefresher;
        this.httpSession = httpSession;
    }

    /** Get the URI for the page which allows a user to connect their Spotify account to our app.
     *
     *  @return The url which links to the spotify page to permit/deny access */
    public URI getAuthorisationCodeUri(String origin) throws RuntimeException {
        // Get redirectUri
        URI redirectUri = SpotifyHttpManager.makeUri(origin + "/account/register/");
        // Generate state
        String state = generateAlphaNumericString(11);
        // Store state in HttpSession, to access later
        httpSession.setAttribute("spotifyState", state);

        System.out.println(redirectUri.toString());

        SpotifyApi spotifyApiForAuthentication = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApiForAuthentication
            .authorizationCodeUri()
            .state(state) // A random number challenge which the client must return
            .scope(scope) // The different types of permission requested
            .show_dialog(true) // Whether the dialog is shown for users who have already granted permission
            .build();

        return authorizationCodeUriRequest.execute();
    }

    /**
     * Initialises the credentials of the spotifyAPI object, given the returned URI.
     *
     * @param spotifyAuthCode The authentication code returned after the user grants access to Spotify account.
     * @param spotifyAuthState The state returned after the user grants access to Spotify account.
     * @return the credentials (null, if there was an error)
     */
    public AuthorizationCodeCredentials initialiseCredentials(String spotifyAuthCode, String spotifyAuthState, URI redirectUri) {
        try {
            // Retrieve state from session
            String storedState = (String) httpSession.getAttribute("spotifyState");
            System.out.println("Comparing Spotify states: '" + spotifyAuthState + "', '" + storedState + "'");

            // Check whether State is invalid
            if (storedState == null || !storedState.equals(spotifyAuthState)) {
                // State is invalid, throw an error
                //                throw new RuntimeException("Invalid Spotify state");
                System.out.println("Invalid Spotify state");
                return null;
            }

            // State is valid, proceed with authentication
            // Clear the state from session to prevent reuse
            httpSession.removeAttribute("spotifyState");

            SpotifyApi spotifyApiForAuthentication = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();

            // Get credentials from returned url
            AuthorizationCodeCredentials authorisationCodeCredentials = spotifyApiForAuthentication
                .authorizationCode(spotifyAuthCode)
                .build()
                .execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApiForAuthentication.setAccessToken(authorisationCodeCredentials.getAccessToken());
            spotifyApiForAuthentication.setRefreshToken(authorisationCodeCredentials.getRefreshToken());

            System.out.println("Generated code expires in: " + authorisationCodeCredentials.getExpiresIn() + " seconds");

            return authorisationCodeCredentials;
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new API object, given credentials (used during initial account authorisation).
     *
     * @param credentials The access credentials for a user
     * @return A new SpotifyAPI object (null, if there was an error)
     */
    public SpotifyApi getAPI(AuthorizationCodeCredentials credentials) {
        SpotifyApi spotifyApi = SpotifyApi.builder().setClientId(clientId).setClientSecret(clientSecret).build();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(credentials.getRefreshToken());

        return spotifyApi;
    }

    /**
     * Creates a new API object given an AppUser, refreshing their access/refresh tokens if necessary.
     *
     * @param appUser The appUser to be logged into the API
     * @return A new SpotifyAPI object (null, if there was an error)
     */
    public SpotifyApi getAPI(AppUser appUser) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyToken spotifyToken = appUser.getSpotifyToken();

        // Setup API object
        String accessToken = spotifyToken.getAccessToken();
        String refreshToken = spotifyToken.getRefreshToken();
        String[] credentials = CredentialsParser.parseCredentials();
        SpotifyApi spotifyApi = SpotifyApi
            .builder()
            .setClientId(credentials[0])
            .setClientSecret(credentials[1])
            .setAccessToken(accessToken)
            .setRefreshToken(refreshToken)
            .build();

        // If the token has expired, refresh it
        if (spotifyToken.getExpires().isBefore(Instant.now())) spotifyApi = tokenRefresher.refreshToken(spotifyApi, spotifyToken);

        return spotifyApi;
    }

    /** Create a SpotifyAPI object that is either authenticated with the current user, or for public access */
    public SpotifyApi getSpotifyApiForCurrentUser() throws IOException, ParseException, SpotifyWebApiException {
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        String[] credentials = CredentialsParser.parseCredentials();
        SpotifyApi spotifyApi = SpotifyApi.builder().setClientId(credentials[0]).setClientSecret(credentials[1]).build();
        String accessToken;

        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            // Return API object with Authorisation Code Flow
            accessToken = appUserRepository.getAppUserByInternalUser(userMaybe.get()).getSpotifyToken().getAccessToken();
        } else {
            // Not logged in
            // Return API object with Client Credentials Flow
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            accessToken = clientCredentials.getAccessToken();
        }

        spotifyApi.setAccessToken(accessToken);

        return spotifyApi;
    }

    /** Generate a random alphanumeric string of length n */
    private static String generateAlphaNumericString(int n) {
        // choose a Character randomly from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }

        //        System.out.println("generated string: " + sb);
        return sb.toString();
    }
    // TODO: getter for access token - useful or storing in database or accessing from other areas of codebase
    //    public String getAccessToken() {
    //        if (spotifyApi.getAccessToken())
    //        return spotifyApi.getAccessToken();
    //    }

}
