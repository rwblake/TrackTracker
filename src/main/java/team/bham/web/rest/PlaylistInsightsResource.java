package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import javax.validation.Valid;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import team.bham.domain.AppUser;
import team.bham.repository.PlaylistRepository;
import team.bham.service.PlaylistService;
import team.bham.service.UserService;
import team.bham.service.account.AppUserService;
import team.bham.service.feed.CardService;
import team.bham.service.feed.FeedCardService;
import team.bham.service.spotify.*;

@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistInsightsResource {

    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;
    private final UserService userService;
    private final AppUserService appUserService;
    private final FeedCardService feedCardService;

    public PlaylistInsightsResource(
        PlaylistService playlistService,
        SpotifyService spotifyService,
        UserService userService,
        AppUserService appUserService,
        FeedCardService feedCardService
    ) {
        this.playlistService = playlistService;
        this.spotifyService = spotifyService;
        this.userService = userService;
        this.appUserService = appUserService;
        this.feedCardService = feedCardService;
    }

    @PostMapping("/playlist-insights")
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody String url)
        throws IOException, ParseException, SpotifyWebApiException {
        String spotifyID = PlaylistRetriever.parsePlaylistIDfromURL(url);

        team.bham.domain.Playlist myPlaylist;
        myPlaylist = createPlaylistEntity(spotifyID);

        // Gets currently logged-in user, if there is one
        Optional<AppUser> user = userService.getUserWithAuthorities().flatMap(appUserService::getAppUser);

        PlaylistInsightsHTTPResponse reply = PlaylistInsightCalculator.getInsights(myPlaylist);

        if (user.isPresent()) {
            // save a card which tells the user they have just analysed a new playlist
            feedCardService.createNewPlaylistCard(user.get(), myPlaylist.getId());
        } else {
            // If we're not logged in, we shouldn't permanently store the playlist.
            playlistService.removePlaylist(myPlaylist);
        }

        Gson gson = new Gson();
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(reply));
    }

    private team.bham.domain.Playlist createPlaylistEntity(String spotifyID) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = spotifyService.getApiForCurrentUser();
        Playlist playlist = spotifyApi.getPlaylist(spotifyID).build().execute();

        return playlistService.createPlaylist(playlist, spotifyApi);
    }
}
