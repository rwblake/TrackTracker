package team.bham.web.rest;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.AppUser;
import team.bham.domain.User;
import team.bham.service.FriendsInsightsService;
import team.bham.service.UserService;
import team.bham.service.account.AppUserService;
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
    private final AppUserService appUserService;
    private final FriendsInsightsService friendsInsightsService;

    public FriendsInsightsResource(UserService userService, AppUserService appUserService, FriendsInsightsService friendsInsightsService) {
        this.userService = userService;
        this.appUserService = appUserService;
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

        User internalAppUser = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
        Optional<AppUser> appUser = appUserService.getAppUser(internalAppUser);

        if (appUser.isEmpty()) throw new AccountResourceException("App User could not be found");

        return friendsInsightsService.getPopularCategories(appUser.get(), periodDays);
    }
}
