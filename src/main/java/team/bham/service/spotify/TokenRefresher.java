package team.bham.service.spotify;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.hc.core5.http.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import team.bham.domain.SpotifyToken;
import team.bham.repository.SpotifyTokenRepository;

@Service
@Transactional
public class TokenRefresher {

    private final SpotifyTokenRepository spotifyTokenRepository;

    public TokenRefresher(SpotifyTokenRepository spotifyTokenRepository) {
        this.spotifyTokenRepository = spotifyTokenRepository;
    }

    //    /** Get all tokens from database, refresh them, and replace, to keep them in date */
    //    @Scheduled(fixedRate = 50 * 60 * 1000) // Every 50 minutes
    //    public void refreshTokens() {
    //        AuthorizationCodeCredentials authorizationCodeCredentials;
    //
    //        // Get tokens from database
    //        List<SpotifyToken> spotifyTokens = this.spotifyTokenRepository.findAll();
    //
    //        String[] credentials = CredentialsParser.parseCredentials();
    //        for (SpotifyToken spotifyToken : spotifyTokens) {
    //            try {
    //                // Make api request
    //                authorizationCodeCredentials =
    //                    SpotifyApi
    //                        .builder()
    //                        .setClientId(credentials[0])
    //                        .setClientSecret(credentials[1])
    //                        .setRefreshToken(spotifyToken.getRefreshToken())
    //                        .build()
    //                        .authorizationCodeRefresh()
    //                        .build()
    //                        .execute();
    //                // Set token fields
    //                if (authorizationCodeCredentials.getRefreshToken() != null) {
    //                    // Refresh token isn't always given, so only set it if we're given a new one
    //                    spotifyToken.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    //                }
    //                spotifyToken.setAccessToken(authorizationCodeCredentials.getAccessToken());
    //                spotifyToken.setExpires(Instant.now().plus(Duration.ofSeconds(authorizationCodeCredentials.getExpiresIn())));
    //            } catch (IOException | SpotifyWebApiException | ParseException e) {
    //                // If any of the requests fail, we will still execute the other ones
    //                System.out.println("Failure to refresh spotify token, Error: " + e.getMessage());
    //            }
    //        }
    //        spotifyTokenRepository.saveAll(spotifyTokens);
    //    }

    /**Refreshes a SpotifyToken using a given SpotifyApi object
     * @param spotifyApi a fully authorised SpotifyApi object (has access and refresh tokens set up)
     * @param spotifyToken the SpotifyToken object (belonging to a AppUser), which needs to be updated*/
    public SpotifyApi refreshToken(SpotifyApi spotifyApi, SpotifyToken spotifyToken)
        throws IOException, ParseException, SpotifyWebApiException {
        // Refresh the token
        AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh().build().execute();

        // Refresh token isn't always given, so only set it if we're given a new one
        if (authorizationCodeCredentials.getRefreshToken() != null) spotifyToken.setRefreshToken(
            authorizationCodeCredentials.getRefreshToken()
        );

        spotifyToken.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyToken.setExpires(Instant.now().plus(Duration.ofSeconds(authorizationCodeCredentials.getExpiresIn())));

        // Finally save into database
        spotifyTokenRepository.save(spotifyToken);

        // Update spotifyApi object
        spotifyApi.setAccessToken(spotifyToken.getAccessToken());
        spotifyApi.setRefreshToken(spotifyToken.getRefreshToken());

        return spotifyApi;
    }
}
