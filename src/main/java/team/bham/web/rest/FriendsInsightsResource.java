package team.bham.web.rest;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.AppUser;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.service.FriendsInsightsService;
import team.bham.service.UserService;
import team.bham.service.spotify.FriendsInsightsLeaderboardsResponse;
import team.bham.service.spotify.FriendsInsightsPopularCategoriesResponse;

@RestController
@RequestMapping("/api/friends-insights")
@Transactional
public class FriendsInsightsResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(FriendsInsightsResource.class);

    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final FriendsInsightsService friendsInsightsService;

    public FriendsInsightsResource(
        UserService userService,
        AppUserRepository appUserRepository,
        FriendsInsightsService friendsInsightsService
    ) {
        this.userService = userService;
        this.appUserRepository = appUserRepository;
        this.friendsInsightsService = friendsInsightsService;
    }

    /**
     * {@code GET /popular-categories} : get all the streams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of streams in body.
     */
    @GetMapping("/popular-categories")
    public FriendsInsightsPopularCategoriesResponse getPopularCategories(@RequestParam("period") Optional<Integer> periodDays) {
        log.debug("REST request to get friends insights popular categories");

        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isEmpty()) throw new AccountResourceException("User could not be found");

        if (!this.appUserRepository.existsByInternalUser(userMaybe.get())) throw new AccountResourceException(
            "App User could not be found"
        );

        AppUser appUser = appUserRepository.getAppUserByInternalUser(userMaybe.get());
        if (appUser == null) throw new AccountResourceException("App User could not be found");

        return friendsInsightsService.getPopularCategories(appUser, periodDays);
    }

    @GetMapping("/leaderboards")
    public FriendsInsightsLeaderboardsResponse getLeaderboards(@RequestParam("period") Optional<Integer> periodDays) {
        log.debug("REST request to get friends insights leaderboards");

        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isEmpty()) throw new AccountResourceException("User could not be found");

        if (!this.appUserRepository.existsByInternalUser(userMaybe.get())) throw new AccountResourceException(
            "App User could not be found"
        );

        AppUser appUser = appUserRepository.getAppUserByInternalUser(userMaybe.get());
        if (appUser == null) throw new AccountResourceException("App User could not be found");

        return friendsInsightsService.getLeaderboards(appUser, periodDays);
    }
}
