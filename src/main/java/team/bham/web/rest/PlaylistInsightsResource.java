package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import team.bham.domain.PlaylistStats;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.PlaylistService;
import team.bham.service.PlaylistStatsService;
import team.bham.service.UserService;
import team.bham.service.spotify.*;

@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistInsightsResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistInsightsResource.class);

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final SpotifyAuthorisationService spotifyAuthorisationService;

    public PlaylistInsightsResource(
        PlaylistService playlistService,
        PlaylistRepository playlistRepository,
        SpotifyAuthorisationService spotifyAuthorisationService
    ) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
        this.spotifyAuthorisationService = spotifyAuthorisationService;
    }

    @PostMapping("/playlist-insights")
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody String url)
        throws IOException, ParseException, SpotifyWebApiException {
        String spotifyID = PlaylistRetriever.parsePlaylistIDfromURL(url);

        team.bham.domain.Playlist myPlaylist;
        myPlaylist = createPlaylistEntity(spotifyID);

        PlaylistInsightsHTTPResponse reply = PlaylistInsightCalculator.getInsights(myPlaylist);
        Gson gson = new Gson();
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(reply));
    }

    private team.bham.domain.Playlist createPlaylistEntity(String spotifyID) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = this.spotifyAuthorisationService.getSpotifyApiForCurrentUser();
        Playlist playlist = spotifyApi.getPlaylist(spotifyID).build().execute();

        return playlistService.createPlaylist(playlist, spotifyApi);
    }
}
