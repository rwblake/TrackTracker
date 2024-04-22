package team.bham.service.account;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Image;
import team.bham.domain.*;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.*;
import team.bham.security.AuthoritiesConstants;
import team.bham.service.UserService;
import team.bham.service.spotify.SpotifyService;
import team.bham.web.rest.vm.ManagedAppUserVM;
import tech.jhipster.security.RandomUtil;

/** NEW - Service class for managing users. */
@Service
@Transactional
public class AppUserService extends team.bham.service.UserService {

    private final Logger log = LoggerFactory.getLogger(AppUserService.class);
    private final AppUserRepository appUserRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;

    public AppUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        AppUserRepository appUserRepository,
        SpotifyService spotifyService,
        UserService userService
    ) {
        super(userRepository, passwordEncoder, authorityRepository, cacheManager);
        this.appUserRepository = appUserRepository;
        this.spotifyService = spotifyService;
        this.userService = userService;
    }

    protected boolean removeNonActivatedAppUser(AppUser existingUser) {
        if (existingUser.getInternalUser().isActivated()) {
            return false;
        }
        appUserRepository.delete(existingUser);
        appUserRepository.flush();
        clearUserCaches(existingUser.getInternalUser());
        return true;
    }

    public Optional<AppUser> getAppUser(User internalUser) {
        return appUserRepository.findOneByInternalUser(internalUser);
    }

    /** Performs same actions as UserService, whilst also creating all other linked entities */
    public AppUser registerAppUser(
        ManagedAppUserVM appUserVM,
        String password,
        String spotifyID,
        String spotifyDisplayName,
        AuthorizationCodeCredentials spotifyCredentials
    ) throws IOException, ParseException, SpotifyWebApiException {
        // Check that login isn't used by anyone else.
        // If it is, check that the other account is activated, delete account if not.
        userRepository
            .findOneByLogin(appUserVM.getLogin().toLowerCase())
            .flatMap(appUserRepository::findOneByInternalUser)
            .ifPresent(appUser -> {
                boolean removed = removeNonActivatedAppUser(appUser);
                if (!removed) throw new UsernameAlreadyUsedException();
            });

        // Check that email isn't used by anyone else
        userRepository
            .findOneByEmailIgnoreCase(appUserVM.getEmail())
            .flatMap(appUserRepository::findOneByInternalUser)
            .ifPresent(appUser -> {
                boolean removed = removeNonActivatedAppUser(appUser);
                if (!removed) throw new EmailAlreadyUsedException();
            });

        // Check that Spotify id isn't used by anyone else
        appUserRepository
            .findOneBySpotifyID(spotifyID)
            .ifPresent(appUser -> {
                boolean removed = removeNonActivatedAppUser(appUser);
                if (!removed) throw new SpotifyIDAlreadyUsedException();
            });

        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(appUserVM.getLogin().toLowerCase());
        // new user initially gets a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(appUserVM.getFirstName());
        newUser.setLastName(appUserVM.getLastName());
        if (appUserVM.getEmail() != null) {
            newUser.setEmail(appUserVM.getEmail().toLowerCase());
        }
        newUser.setImageUrl(appUserVM.getImageUrl());
        newUser.setLangKey(appUserVM.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);

        // Create a SpotifyToken
        SpotifyToken spotifyToken = new SpotifyToken();
        spotifyToken.setAccessToken(spotifyCredentials.getAccessToken());
        spotifyToken.setTokenType(spotifyCredentials.getTokenType());
        spotifyToken.setUserScope(spotifyCredentials.getScope());
        spotifyToken.setExpires(Instant.now().plus(Duration.ofSeconds(spotifyCredentials.getExpiresIn())));
        spotifyToken.setRefreshToken(spotifyCredentials.getRefreshToken());

        // Get profile information from SpotifyAPI
        SpotifyApi spotifyApi = spotifyService.getApiForCurrentUser();
        spotifyApi.setAccessToken(spotifyToken.getAccessToken());
        se.michaelthelin.spotify.model_objects.specification.User spotifyUser = spotifyApi.getUsersProfile(spotifyID).build().execute();

        // Create an AppUser
        AppUser newAppUser = new AppUser();
        newAppUser.setSpotifyID(spotifyID);
        newAppUser.setSpotifyUsername(spotifyDisplayName);
        newAppUser.setName("UNUSED FIELD");
        if (spotifyUser.getImages() != null && spotifyUser.getImages().length != 0) {
            Image[] images = spotifyUser.getImages();
            String imageUrl = Arrays
                .stream(images)
                .sorted(Comparator.comparing(Image::getHeight).reversed())
                .collect(Collectors.toList())
                .get(0)
                .getUrl();
            newAppUser.setAvatarURL(imageUrl);
        }

        // Create a Feed
        Feed feed = new Feed();
        feed.setLastUpdated(Instant.now());

        // Create a UserPreferences
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setIsDarkMode(true);
        userPreferences.setVisibility(VisibilityPreference.GLOBAL);
        userPreferences.setIsHighContrast(false);

        // Link children to AppUser
        newAppUser.setInternalUser(newUser);
        newAppUser.setSpotifyToken(spotifyToken);
        newAppUser.setFeed(feed);
        newAppUser.setUserPreferences(userPreferences);

        // Link AppUser to each relation
        feed.setAppUser(newAppUser);
        spotifyToken.setAppUser(newAppUser);
        userPreferences.setAppUser(newAppUser);

        appUserRepository.save(newAppUser);
        log.debug("Created Information for AppUser: {}", newAppUser);

        return newAppUser;
    }

    /** Find the current user and check they have an associated AppUser entity */
    public AppUser getCurrentAppUser() throws NoAppUserException {
        Optional<User> userMaybe = userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            return appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            // Not logged in
            throw new NoAppUserException();
        }
    }
}
