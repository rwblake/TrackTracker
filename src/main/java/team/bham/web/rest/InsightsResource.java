package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.*;
import team.bham.repository.AppUserRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.UserService;
import team.bham.service.spotify.MyInsightCalculator;
import team.bham.service.spotify.MyInsightsHTTPResponse;
import team.bham.service.spotify.SpotifyAuthorisationService;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api")
@Transactional
public class InsightsResource {

    private final StreamRepository streamRepository;
    private final AppUserRepository appUserRepository;
    private final UserService userService;
    private final SpotifyAuthorisationService spotifyAuthorisationService;

    public InsightsResource(
        StreamRepository streamRepository,
        AppUserRepository appUserRepository,
        UserService userService,
        SpotifyAuthorisationService spotifyAuthorisationService
    ) {
        this.streamRepository = streamRepository;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.spotifyAuthorisationService = spotifyAuthorisationService;
    }

    private AppUser getCurrentUser() throws Exception {
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            return this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            // Not logged in
            throw new Exception("Current User is not related to an AppUser.");
        }
    }

    @GetMapping("/insights")
    public List<Stream> getSongs(Instant playedAt) throws Exception {
        AppUser currentUser = getCurrentUser();
        //return list of streams
        return streamRepository.findAllByPlayedAtAfterAndAppUserOrderByPlayedAt(playedAt, currentUser);
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
