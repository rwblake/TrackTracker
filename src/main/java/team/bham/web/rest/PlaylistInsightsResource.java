package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import team.bham.repository.PlaylistRepository;
import team.bham.service.PlaylistService;
import team.bham.service.PlaylistStatsService;
import team.bham.service.spotify.CredentialsParser;
import team.bham.service.spotify.PlaylistInsightCalculator;
import team.bham.service.spotify.PlaylistInsightsHTTPResponse;
import team.bham.service.spotify.PlaylistRetriever;

@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistInsightsResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistInsightsResource.class);

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;

    public PlaylistInsightsResource(PlaylistService playlistService, PlaylistRepository playlistRepository) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
    }

    @PostMapping("/playlist-insights")
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody String url)
        throws URISyntaxException, IOException, ParseException, SpotifyWebApiException {
        String spotifyID = PlaylistRetriever.parsePlaylistIDfromURL(url);

        team.bham.domain.Playlist myPlaylist;
        if (playlistRepository.existsBySpotifyID(spotifyID)) {
            myPlaylist = playlistRepository.findPlaylistBySpotifyID(spotifyID);
        } else {
            String[] credentials = CredentialsParser.parseCredentials();
            SpotifyApi spotifyApi = PlaylistRetriever.clientCredentials(credentials[0], credentials[1]);
            Playlist playlist = spotifyApi.getPlaylist(spotifyID).build().execute();
            List<Track> tracks = PlaylistRetriever.getTracks(spotifyApi, spotifyID);
            List<ArtistSimplified> artists = new ArrayList<>(tracks.size());
            for (Track track : tracks) {
                artists.addAll(List.of(track.getArtists()));
            }
            Artist[] myArtists = PlaylistRetriever.getArtists(spotifyApi, artists);

            List<AudioFeatures> audioFeaturesList = PlaylistRetriever.getAudioFeatures(spotifyApi, tracks);
            myPlaylist = playlistService.createPlaylist(playlist, tracks, audioFeaturesList, myArtists);
        }

        PlaylistInsightsHTTPResponse reply = PlaylistInsightCalculator.getInsights(myPlaylist);
        Gson gson = new Gson();
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(reply));
    }
}
