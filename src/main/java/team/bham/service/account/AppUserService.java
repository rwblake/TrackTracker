package team.bham.service.account;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
import team.bham.domain.*;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.*;
import team.bham.security.AuthoritiesConstants;
import team.bham.service.EmailAlreadyUsedException;
import team.bham.service.SpotifyIDAlreadyUsedException;
import team.bham.service.UsernameAlreadyUsedException;
import team.bham.service.spotify.SpotifyAuthorisationService;
import team.bham.web.rest.vm.ManagedAppUserVM;
import tech.jhipster.security.RandomUtil;

/** NEW - Service class for managing users. */
@Service
@Transactional
public class AppUserService extends team.bham.service.UserService {

    private final Logger log = LoggerFactory.getLogger(AppUserService.class);

    // Required repositories (entities) go here:
    private final AppUserRepository appUserRepository;
    private final SpotifyTokenRepository spotifyTokenRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final FeedRepository feedRepository;
    private final SpotifyAuthorisationService spotifyAuthorisationService;

    public AppUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        AppUserRepository appUserRepository,
        SpotifyTokenRepository spotifyTokenRepository,
        UserPreferencesRepository userPreferencesRepository,
        FeedRepository feedRepository,
        SpotifyAuthorisationService spotifyAuthorisationService
    ) {
        super(userRepository, passwordEncoder, authorityRepository, cacheManager);
        this.appUserRepository = appUserRepository;
        this.spotifyTokenRepository = spotifyTokenRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.feedRepository = feedRepository;
        this.spotifyAuthorisationService = spotifyAuthorisationService;
    }

    protected boolean removeNonActivatedAppUser(AppUser existingUser) {
        if (existingUser.getInternalUser().isActivated()) {
            return false;
        }
        appUserRepository.delete(existingUser);
        appUserRepository.flush();
        this.clearUserCaches(existingUser.getInternalUser());
        return true;
    }

    public Optional<AppUser> getAppUser(User internalUser) {
        return this.appUserRepository.findOneByInternalUser(internalUser);
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
            .ifPresent(user -> {
                appUserRepository
                    .findOneByInternalUser(user)
                    .ifPresent(appUser -> {
                        boolean removed = removeNonActivatedAppUser(appUser);
                        if (!removed) {
                            throw new UsernameAlreadyUsedException();
                        }
                    });
            });

        // Check that email isn't used by anyone else
        userRepository
            .findOneByEmailIgnoreCase(appUserVM.getEmail())
            .ifPresent(user -> {
                appUserRepository
                    .findOneByInternalUser(user)
                    .ifPresent(appUser -> {
                        boolean removed = removeNonActivatedAppUser(appUser);
                        if (!removed) {
                            throw new EmailAlreadyUsedException();
                        }
                    });
            });

        // Check that Spotify id isn't used by anyone else
        appUserRepository
            .findOneBySpotifyID(spotifyID)
            .ifPresent(appUser -> {
                boolean removed = removeNonActivatedAppUser(appUser);
                if (!removed) {
                    throw new SpotifyIDAlreadyUsedException();
                }
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
        newUser.setActivated(true); // TODO: change this to deactivated so that you need to receive an email
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
        SpotifyApi spotifyApi = this.spotifyAuthorisationService.getSpotifyApiForCurrentUser();
        spotifyApi.setAccessToken(spotifyToken.getAccessToken());
        se.michaelthelin.spotify.model_objects.specification.User spotifyUser = spotifyApi.getUsersProfile(spotifyID).build().execute();

        // Create an AppUser
        AppUser newAppUser = new AppUser();
        newAppUser.setSpotifyID(spotifyID);
        newAppUser.setBio(appUserVM.getBio());
        newAppUser.setSpotifyUsername(spotifyDisplayName);
        newAppUser.setName("UNUSED FIELD");
        if (spotifyUser.getImages() != null && spotifyUser.getImages().length != 0) {
            newAppUser.setAvatarURL(spotifyUser.getImages()[0].getUrl());
        }

        // Create a Feed
        Feed feed = new Feed();
        feed.setLastUpdated(Instant.now());

        // Create a UserPreferences
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setIsDarkMode(true);
        userPreferences.setVisibility(VisibilityPreference.FRIENDS);
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
}
