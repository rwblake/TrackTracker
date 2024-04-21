package team.bham.service.spotify;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import team.bham.domain.AppUser;
import team.bham.domain.Song;
import team.bham.domain.Stream;
import team.bham.repository.AppUserRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.SongService;
import team.bham.service.StreamService;
import team.bham.service.feed.FeedService;

@Service
@Transactional
public class StreamRefresher {

    private final int refreshIntervalMinutes = 5;

    private final AppUserRepository appUserRepository;
    private final StreamRepository streamRepository;
    private final StreamService streamService;
    private final SongService songService;
    private final FeedService feedService;
    private final SpotifyService spotifyService;
    private final Logger log = LoggerFactory.getLogger(StreamRefresher.class);

    public StreamRefresher(
        AppUserRepository appUserRepository,
        StreamRepository streamRepository,
        StreamService streamService,
        SongService songService,
        FeedService feedService,
        SpotifyService spotifyService
    ) {
        this.appUserRepository = appUserRepository;
        this.streamRepository = streamRepository;
        this.streamService = streamService;
        this.songService = songService;
        this.feedService = feedService;
        this.spotifyService = spotifyService;
    }

    @Scheduled(fixedRate = refreshIntervalMinutes * 60 * 1000)
    public void refreshAllUsersStreams() {
        try {
            // Get all AppUsers
            List<AppUser> appUsers = this.appUserRepository.findAll();
            // Refresh streams for each AppUser
            for (AppUser appUser : appUsers) {
                refreshStreamsForUser(appUser);
                log.debug("Updating " + appUser.getId() + "'s last updated timestamp");
                // Mark that the user's music profile has just been updated
                feedService.updateUsersMusicProfile(appUser);
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }

    /** Refreshes the streams for a given AppUser */
    public void refreshStreamsForUser(AppUser appUser) {
        try {
            // Find last stream (and when it was) to avoid duplication
            Instant previousScheduledCall = Instant.now().minusSeconds(60 * refreshIntervalMinutes);

            // Find streams between last function call and the one before that
            List<Stream> previousStreams =
                this.streamRepository.findAllByPlayedAtAfterAndAppUserOrderByPlayedAt(
                        previousScheduledCall.minusSeconds(60 * refreshIntervalMinutes),
                        appUser
                    );
            Instant lastStream = previousScheduledCall;
            if (!previousStreams.isEmpty()) {
                lastStream = previousStreams.get(previousStreams.size() - 1).getPlayedAt(); // Time of most recent recorded stream
            }
            lastStream = lastStream.plusSeconds(1); // Stops issues with duplicate tracks around this time

            // Setup API object
            SpotifyApi spotifyApi = spotifyService.getApi(appUser);

            // Get streams
            PlayHistory[] playHistory = spotifyApi
                .getCurrentUsersRecentlyPlayedTracks()
                .after(Date.from(lastStream))
                .limit(50)
                .build()
                .execute()
                .getItems();

            if (playHistory.length == 0) {
                return;
            }

            // Create songs
            List<Track> trackList = Arrays.stream(playHistory).map(PlayHistory::getTrack).collect(Collectors.toList());
            List<Song> mySongs = this.songService.createSongs(trackList, spotifyApi);

            if (playHistory.length != mySongs.size()) {
                return;
            }

            // Create streams
            for (int i = 0; i < mySongs.size(); i++) {
                this.streamService.createStream(playHistory[i], mySongs.get(i), appUser);
            }
        } catch (IOException | SpotifyWebApiException | ParseException | IllegalStateException e) {
            // If any of the requests fail, we will still execute the other ones
            log.error("Failed to get streams for an appUser", e);
        }
    }
}
