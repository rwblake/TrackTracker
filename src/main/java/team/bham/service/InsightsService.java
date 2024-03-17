package team.bham.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import team.bham.domain.Playlist;
import team.bham.domain.Song;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.spotify.PlaylistRetriever;

@Service
@Transactional
public class InsightsService {}
