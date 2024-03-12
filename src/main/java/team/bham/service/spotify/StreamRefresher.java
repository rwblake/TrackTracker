package team.bham.service.spotify;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hc.core5.http.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import team.bham.domain.AppUser;
import team.bham.domain.Genre;
import team.bham.domain.Stream;
import team.bham.repository.AppUserRepository;
import team.bham.repository.ArtistRepository;
import team.bham.repository.GenreRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.StreamService;

@Service
@Transactional
public class StreamRefresher {

    public final int refreshIntervalMinutes = 25;

    public final AppUserRepository appUserRepository;
    public final StreamRepository streamRepository;
    public final StreamService streamService;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    public StreamRefresher(
        AppUserRepository appUserRepository,
        StreamRepository streamRepository,
        StreamService streamService,
        GenreRepository genreRepository,
        ArtistRepository artistRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.streamRepository = streamRepository;
        this.streamService = streamService;
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
    }

    @Scheduled(fixedRate = 25 * 60 * 1000)
    public void refreshAllUsersStreams() {
        // Get all AppUsers
        List<AppUser> appUsers = this.appUserRepository.findAll();
        // Refresh streams for each AppUser
        for (AppUser appUser : appUsers) {
            refreshStreamsForUser(appUser);
        }
    }

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
            String accessToken = appUser.getSpotifyToken().getAccessToken();
            String[] credentials = CredentialsParser.parseCredentials();
            SpotifyApi spotifyApi = SpotifyApi
                .builder()
                .setClientId(credentials[0])
                .setClientSecret(credentials[1])
                .setAccessToken(accessToken)
                .build();

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

            // Get audio features and artists
            List<Track> trackList = Arrays.stream(playHistory).map(PlayHistory::getTrack).collect(Collectors.toList());
            List<AudioFeatures> audioFeaturesList = PlaylistRetriever.getAudioFeatures(spotifyApi, trackList);
            List<ArtistSimplified> artists = new ArrayList<>(trackList.size());
            for (Track track : trackList) {
                artists.addAll(List.of(track.getArtists()));
            }
            Artist[] myArtists = PlaylistRetriever.getArtists(spotifyApi, artists);

            // Create streams
            for (int i = 0; i < playHistory.length; i++) {
                this.streamService.createStream(playHistory[i], audioFeaturesList.get(i), appUser);
            }

            // Get artist attributes that couldn't previously be set
            Genre myGenre;
            team.bham.domain.Artist myArtist;
            for (se.michaelthelin.spotify.model_objects.specification.Artist artist : myArtists) {
                // Set artist genres (by setting genre artists which cascade)
                for (String name : artist.getGenres()) {
                    if (genreRepository.existsByName(name)) {
                        myGenre = genreRepository.findGenreByName(name);
                    } else {
                        myGenre = new Genre();
                        myGenre.setName(name);
                    }
                    myGenre.addArtist(artistRepository.findArtistBySpotifyID(artist.getId()));
                    genreRepository.save(myGenre);
                }
                // Set artist image
                myArtist = artistRepository.findArtistBySpotifyID(artist.getId());
                if (artist.getImages() != null && artist.getImages().length > 0) {
                    myArtist.setImageURL(artist.getImages()[0].getUrl());
                }

                artistRepository.save(myArtist);
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // If any of the requests fail, we will still execute the other ones
            System.out.println("Failed to get streams for an appUser, Error: " + e.getMessage());
        }
    }
}
