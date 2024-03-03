package team.bham.service.account;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import team.bham.domain.*;
import team.bham.repository.*;
import team.bham.security.AuthoritiesConstants;
import team.bham.service.EmailAlreadyUsedException;
import team.bham.service.SpotifyIDAlreadyUsedException;
import team.bham.service.UsernameAlreadyUsedException;
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

    public AppUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        AppUserRepository appUserRepository,
        SpotifyTokenRepository spotifyTokenRepository
    ) {
        super(userRepository, passwordEncoder, authorityRepository, cacheManager);
        this.appUserRepository = appUserRepository;
        this.spotifyTokenRepository = spotifyTokenRepository;
    }

    /** Performs same actions as UserService, whilst also creating all other linked entities */
    public AppUser registerAppUser(
        ManagedAppUserVM appUserVM,
        String password,
        String spotifyUserID,
        AuthorizationCodeCredentials spotifyCredentials
    ) {
        // Check that login isn't used by anyone else.
        // If it is, check that the other account is activated, delete account if not.
        userRepository
            .findOneByLogin(appUserVM.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });

        // Check that email isn't used by anyone else
        userRepository
            .findOneByEmailIgnoreCase(appUserVM.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });

        // Check that Spotify id isn't used by anyone else
        appUserRepository
            .findOneBySpotifyID(spotifyUserID)
            .ifPresent(appUser -> {
                boolean removed = removeNonActivatedUser(appUser.getInternalUser());
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
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);

        SpotifyToken spotifyToken = new SpotifyToken(
            spotifyCredentials.getAccessToken(),
            spotifyCredentials.getTokenType(),
            spotifyCredentials.getScope(),
            Instant.now().plus(Duration.ofSeconds(spotifyCredentials.getExpiresIn())),
            spotifyCredentials.getRefreshToken()
        );

        AppUser newAppUser = new AppUser(spotifyUserID, spotifyToken);

        newAppUser.setBio(appUserVM.getBio());

        spotifyTokenRepository.save(spotifyToken);
        log.debug("Created Information for SpotifyToken: {}", spotifyToken);

        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        this.clearUserCaches(newUser);

        appUserRepository.save(newAppUser);
        log.debug("Created Information for AppUser: {}", newAppUser);

        return newAppUser;
    }
}
