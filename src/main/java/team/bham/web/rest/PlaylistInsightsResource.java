package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import team.bham.service.PlaylistService;
import team.bham.service.PlaylistStatsService;
import team.bham.service.spotify.CredentialsParser;
import team.bham.service.spotify.PlaylistInsightsHTTPResponse;
import team.bham.service.spotify.PlaylistRetriever;

@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistInsightsResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistInsightsResource.class);

    private final PlaylistStatsService playlistStatsService;

    public PlaylistInsightsResource(PlaylistStatsService playlistStatsService) {
        this.playlistStatsService = playlistStatsService;
    }

    @PostMapping("/playlist-insights")
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody String url)
        throws URISyntaxException, IOException, ParseException, SpotifyWebApiException {
        // Do something
        log.debug("In method");
        String spotifyID = PlaylistRetriever.parsePlaylistIDfromURL(url);
        String[] credentials = CredentialsParser.parseCredentials();
        SpotifyApi spotifyApi = PlaylistRetriever.clientCredentials(credentials[0], credentials[1]);
        Playlist playlist = spotifyApi.getPlaylist(spotifyID).build().execute();
        List<Track> tracks = PlaylistRetriever.getTracks(spotifyApi, spotifyID);
        List<AudioFeatures> audioFeaturesList = PlaylistRetriever.getAudioFeatures(spotifyApi, tracks);

        team.bham.domain.PlaylistStats ret = playlistStatsService.createPlaylistStats(
            playlist,
            tracks.toArray(new Track[0]),
            audioFeaturesList.toArray(new AudioFeatures[0])
        );

        // PlaylistInsightsHTTPResponse reply = new PlaylistInsightsHTTPResponse();
        // Gson gson = new Gson();
        return ResponseEntity.status(HttpStatus.OK).body(url);
    }
}
