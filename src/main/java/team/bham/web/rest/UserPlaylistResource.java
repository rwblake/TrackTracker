package team.bham.web.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import team.bham.domain.AppUser;
import team.bham.domain.PlaylistStats;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.PlaylistService;
import team.bham.service.PlaylistStatsService;
import team.bham.service.UserService;
import team.bham.service.spotify.*;

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

    @GetMapping("/current-user-playlists")
    public ResponseEntity<List<team.bham.domain.Playlist>> createPlaylist() {
        AppUser currentUser = null;
        List<team.bham.domain.Playlist> userPlaylists = new ArrayList<>();

        Optional<team.bham.domain.User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            currentUser = this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(userPlaylists);
        }

        for (team.bham.domain.Playlist playlist : playlistRepository.findAll()) {
            if (playlist.getAppUser() != null && playlist.getAppUser() == currentUser) {
                userPlaylists.add(playlist);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(userPlaylists);
    }
}
