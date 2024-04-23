package team.bham.web.rest;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.*;
import team.bham.repository.AppUserRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.InsightsService;
import team.bham.service.UserService;
import team.bham.service.account.NoAppUserException;
import team.bham.service.spotify.MyInsightCalculator;
import team.bham.service.spotify.SpotifyService;
import team.bham.service.spotify.StreamInsightsResponse;

@RestController
@RequestMapping("/api")
@Transactional
public class InsightsResource {

    private final StreamRepository streamRepository;
    private final AppUserRepository appUserRepository;
    private final UserService userService;
    private final SpotifyService spotifyService;
    private final InsightsService insightsService;

    public InsightsResource(
        StreamRepository streamRepository,
        AppUserRepository appUserRepository,
        UserService userService,
        SpotifyService spotifyService,
        InsightsService insightsService
    ) {
        this.streamRepository = streamRepository;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.spotifyService = spotifyService;
        this.insightsService = insightsService;
    }

    private AppUser getCurrentUser() throws NoAppUserException {
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            return this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            // Not logged in
            throw new NoAppUserException();
        }
    }

    @GetMapping("/insights")
    public StreamInsightsResponse getAllInsights() throws NoAppUserException {
        // find the current user
        AppUser currentUser = getCurrentUser();
        // get the insights in response format
        StreamInsightsResponse response = insightsService.getInsights(streamRepository.findAllByAppUserOrderByPlayedAt(currentUser));
        // return response
        return response;
    }
    //    @GetMapping("/insights")
    //    public Instant getTime(){
    //
    //    }

    //    @PostMapping("/insights-songs")
    //    public ResponseEntity<String> createStreams() throws Exception {
    //        List<Stream> streams;
    //        streams = getSongs(getTime());
    //
    //        MyInsightsHTTPResponse reply = MyInsightCalculator.getInsights(streams);
    //
    //        Gson gson = new Gson();
    //        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(reply));
    //
    //
    //    }

}
