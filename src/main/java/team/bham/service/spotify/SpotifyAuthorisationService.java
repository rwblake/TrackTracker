package team.bham.service.spotify;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/** Handles authorising a spotify account, getting tokens and refreshing tokens. */
@Service
public class SpotifyAuthorisationService {

    // Static - can be shared between authentication requests
    private static final String clientId = CredentialsParser.parseCredentials()[0];
    private static final String clientSecret = CredentialsParser.parseCredentials()[1];
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/");

    private static final String scope =
        "playlist-read-private, user-follow-read," +
        "user-read-playback-position, user-top-read, " +
        "user-read-recently-played, user-library-read";

    // Non-Static - specific to each authentication request
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(redirectUri)
        .build();

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
        AuthorizationCodeCredentials credentials = auth.initialiseCredentials(URI.create(newURI));
        if (credentials != null) {
            System.out.println("Successfully generated credentials: " + credentials);
        } else {
            System.out.println("Credentials not generated - need to prompt user to accept, or abort account creation.");
        }
    }

    /** Get the URI for the page which allows a user to connect their Spotify account to our app */
    public URI getAuthorisationCodeUri() throws RuntimeException {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
            .authorizationCodeUri()
            .state(generateAlphaNumericString(11)) // A random number challenge which the client must return
            .scope(scope) // The different types of permission requested
            .show_dialog(true) // Whether the dialog is shown for users who have already granted permission
            .build();

        return authorizationCodeUriRequest.execute();
    }

    /**
     * Initialises the credentials of the spotifyAPI object, given the returned URI.
     *
     * @param uri The returned uri after responding to Spotify
     * @return Whether the credentials could be initialised
     */
    public AuthorizationCodeCredentials initialiseCredentials(URI uri) {
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
                return null;
            }

            System.out.println("Initialising the credentials given query: " + map.get("code"));

            // Get credentials from returned url
            AuthorizationCodeCredentials authorisationCodeCredentials = spotifyApi.authorizationCode(map.get("code")).build().execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorisationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorisationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorisationCodeCredentials.getExpiresIn() + " seconds");

            return authorisationCodeCredentials;
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            e.printStackTrace();
            return null;
        }
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
