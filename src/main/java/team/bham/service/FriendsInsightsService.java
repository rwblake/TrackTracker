package team.bham.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.domain.enumeration.AlbumType;
import team.bham.repository.SongRepository;
import team.bham.service.spotify.FriendsInsightsPopularCategoriesResponse;

@Service
public class FriendsInsightsService {

    private <T> List<Map.Entry<T, Integer>> getTopNFromFreqMap(Map<T, Integer> freqMap, int n) {
        List<Map.Entry<T, Integer>> sortedList = freqMap
            .entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .collect(Collectors.toList());

        return sortedList.subList(0, Math.min(sortedList.size(), n));
    }

    /**
     * Gets the most listened to tracks, artists, and albums for the specified time period for the AppUser & Friends
     *
     * @param appUser the AppUser to get the friends insights data from
     * @param timePeriodInDays the selected time period (7 days, 28 days, etc.) [OPTIONAL: will get all time data if no time period is specified]
     */
    public FriendsInsightsPopularCategoriesResponse getPopularCategories(AppUser appUser, Optional<Integer> timePeriodInDays) {
        // Get set of all users friends
        Set<AppUser> allFriends = appUser
            .getFriends()
            .stream()
            .map(friendship -> {
                AppUser initiating = friendship.getFriendInitiating();
                AppUser accepting = friendship.getFriendAccepting();

                return Objects.equals(appUser.getId(), initiating.getId()) ? accepting : initiating;
            })
            .collect(Collectors.toSet());

        // Add the requesting user to the set
        allFriends.add(appUser);

        // Get set of all streams for all users in above set
        Set<Stream> allStreams = allFriends.stream().flatMap(appUser1 -> appUser1.getStreams().stream()).collect(Collectors.toSet());

        // Filter streams if a time period has been specified
        if (timePeriodInDays.isPresent() && timePeriodInDays.get() > 0) {
            // Calculate the Instant to filter the streams by
            Instant cutoffInstant = Instant.now().minus(timePeriodInDays.get(), ChronoUnit.DAYS);

            allStreams = allStreams.stream().filter(stream -> stream.getPlayedAt().isAfter(cutoffInstant)).collect(Collectors.toSet());
        }

        Map<Song, Integer> songFreqMap = new HashMap<>();
        Map<Artist, Integer> artistFreqMap = new HashMap<>();
        Map<Album, Integer> albumFreqMap = new HashMap<>();

        // Calculate the frequencies of song streams
        allStreams.forEach(stream -> {
            Song song = stream.getSong();
            songFreqMap.putIfAbsent(song, songFreqMap.getOrDefault(song, 0) + 1);
        });

        // Calculate frequencies of artist streams
        songFreqMap.forEach((song, freq) -> {
            song
                .getArtists()
                .forEach(artist -> {
                    artistFreqMap.putIfAbsent(artist, artistFreqMap.getOrDefault(artist, 0) + freq);
                });
        });

        // Calculate frequencies of album streams
        songFreqMap.forEach((song, freq) -> {
            Album album = song.getAlbum();
            if (album.getAlbumType().equals(AlbumType.SINGLE)) return; // don't log singles as albums
            albumFreqMap.putIfAbsent(album, albumFreqMap.getOrDefault(album, 0) + freq);
        });

        // Sort and pick top 10 from sets
        List<Map.Entry<Song, Integer>> top10Songs = getTopNFromFreqMap(songFreqMap, 10);
        List<Map.Entry<Artist, Integer>> top10Artists = getTopNFromFreqMap(artistFreqMap, 10);
        List<Map.Entry<Album, Integer>> top10Albums = getTopNFromFreqMap(albumFreqMap, 10);

        return new FriendsInsightsPopularCategoriesResponse(top10Songs, top10Artists, top10Albums);
    }
}
