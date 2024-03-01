package team.bham.service.account;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/** Handles authorising a spotify account, getting tokens and refreshing tokens. */
public class SpotifyAuthorisationService {

    // Static - can be shared between authentication requests
    private static final String clientId = "1f7b9c2b8c7f449aa6540a55b41e9911";
    private static final String clientSecret = "1c54588fd24b4584a88bd62a33d17ec6";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/");

    // Non-Static - specific to each authentication request
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(redirectUri)
        .build();
    private AuthorizationCodeCredentials authorisationCodeCredentials = null;

    // To test this code whilst developing
    public static void main(String[] args) {
        // Create new authenticator connection
        SpotifyAuthorisationService auth = new SpotifyAuthorisationService();

        // Create a URI which links to Spotify's permissions page
        URI uri = auth.getAuthorisationCodeUri();
        System.out.println("URL: " + uri.toString());

        System.out.println("Please enter the returned URL after granting or declining the permissions:\n");
        Scanner input = new Scanner(System.in);
        String newURI = input.nextLine();

        // Initialise credentials
        boolean success = auth.initialiseCredentials(URI.create(newURI));
        if (success) {
            System.out.println("Successfully generated credentials: " + auth.authorisationCodeCredentials);
        } else {
            System.out.println("Credentials not generated - need to prompt user to accept, or abort account creation.");
        }
    }

    /** Perform synced request - method runs sequentially. */
    public URI getAuthorisationCodeUri() throws RuntimeException {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
            .authorizationCodeUri()
            .state(generateAlphaNumericString(11)) // scope: the different types of permission requested
            .scope(
                "playlist-read-private, user-follow-read," + // scope: the different types of permission requested
                "user-read-playback-position, user-top-read, " +
                "user-read-recently-played, user-library-read"
            )
            .show_dialog(true) // show_dialog: decides whether the dialog is shown for users who have already granted permission
            .build();

        return authorizationCodeUriRequest.execute();
    }

    /**Initialises the credentials of the spotifyAPI object, given the returned URI.
     * @param uri The returned uri after responding to Spotify
     * @return Whether the credentials could be initialised */
    public boolean initialiseCredentials(URI uri) {
        try {
            // Split the uri into the different queries
            String[] queries = uri.getQuery().split("&");
            Map<String, String> map = new java.util.HashMap<>(Map.of());
            for (String query : queries) {
                String[] parts = query.split("=");
                map.put(parts[0], parts[1]);
            }

            // Check that there is a code given
            if (map.get("code") == null) {
                System.out.println("User declined. Error: " + map.get("error"));
                return false;
            }

            System.out.println("Initialising the credentials given query: " + map.get("code"));

            // Get credentials from returned url
            authorisationCodeCredentials = spotifyApi.authorizationCode(map.get("code")).build().execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorisationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorisationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorisationCodeCredentials.getExpiresIn() + " seconds");

            return true;
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Generate a random alphanumeric string of length n */
    private static @NotNull String generateAlphaNumericString(int n) {
        // choose a Character randomly from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }

        System.out.println("generated string: " + sb);
        return sb.toString();
    }
    // TODO: getter for access token - useful or storing in database or accessing from other areas of codebase
    //    public String getAccessToken() {
    //        if (spotifyApi.getAccessToken())
    //        return spotifyApi.getAccessToken();
    //    }

}
