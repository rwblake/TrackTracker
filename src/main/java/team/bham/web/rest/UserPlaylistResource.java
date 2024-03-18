package team.bham.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.bham.domain.AppUser;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.UserService;

@RestController
@RequestMapping("/api")
@Transactional
public class UserPlaylistResource {

    private final Logger log = LoggerFactory.getLogger(UserPlaylistResource.class);

    private final PlaylistRepository playlistRepository;
    private final AppUserRepository appUserRepository;
    private final UserService userService;

    public UserPlaylistResource(PlaylistRepository playlistRepository, UserService userService, AppUserRepository appUserRepository) {
        this.playlistRepository = playlistRepository;
        this.userService = userService;
        this.appUserRepository = appUserRepository;
    }

    /** API endpoint for retrieving the currently logged-in user's playlists*/
    @GetMapping("/current-user-playlists")
    public ResponseEntity<List<team.bham.domain.Playlist>> createPlaylist() {
        AppUser currentUser;
        List<team.bham.domain.Playlist> userPlaylists = new ArrayList<>();

        Optional<team.bham.domain.User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            currentUser = this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userPlaylists);
        }

        userPlaylists = this.playlistRepository.findPlaylistsByAppUser(currentUser);

        // Sort based on recency
        userPlaylists.sort((p1, p2) ->
            (int) p2.getPlaylistStats().getLastUpdated().getEpochSecond() - (int) p1.getPlaylistStats().getLastUpdated().getEpochSecond()
        );

        return ResponseEntity.status(HttpStatus.OK).body(userPlaylists);
    }
}
