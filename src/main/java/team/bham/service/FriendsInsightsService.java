package team.bham.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.domain.enumeration.AlbumType;
import team.bham.repository.FriendshipRepository;
import team.bham.repository.SongRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.spotify.FriendsInsightsLeaderboardsResponse;
import team.bham.service.spotify.FriendsInsightsPopularCategoriesResponse;

@Service
public class FriendsInsightsService {

    private final FriendService friendService;

    public FriendsInsightsService(FriendService friendService) {
        this.friendService = friendService;
    }

    private <T> List<Map.Entry<T, Long>> getTopNFromLongFreqMap(Map<T, Long> freqMap, int n) {
        List<Map.Entry<T, Long>> sortedList = freqMap
            .entrySet()
            .stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .collect(Collectors.toList());

        return sortedList.subList(0, Math.min(sortedList.size(), n));
    }

    private <T> List<Map.Entry<T, Integer>> getTopNFromIntFreqMap(Map<T, Integer> freqMap, int n) {
        List<Map.Entry<T, Integer>> sortedList = freqMap
            .entrySet()
            .stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
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
        List<Friend> myFriends = friendService.getFriends(appUser);
        Set<AppUser> allFriends = myFriends.stream().map(Friend::getFriendAppUser).collect(Collectors.toSet());

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
            songFreqMap.put(song, songFreqMap.getOrDefault(song, 0) + 1);
        });

        // Calculate frequencies of artist streams
        songFreqMap.forEach((song, freq) -> {
            song
                .getArtists()
                .forEach(artist -> {
                    artistFreqMap.put(artist, artistFreqMap.getOrDefault(artist, 0) + freq);
                });
        });

        // Calculate frequencies of album streams
        songFreqMap.forEach((song, freq) -> {
            Album album = song.getAlbum();
            if (album.getAlbumType().equals(AlbumType.SINGLE)) return; // don't log singles as albums
            albumFreqMap.put(album, albumFreqMap.getOrDefault(album, 0) + freq);
        });

        // Sort and pick top 10 from sets
        List<Map.Entry<Song, Integer>> top10Songs = getTopNFromIntFreqMap(songFreqMap, 10);
        List<Map.Entry<Artist, Integer>> top10Artists = getTopNFromIntFreqMap(artistFreqMap, 10);
        List<Map.Entry<Album, Integer>> top10Albums = getTopNFromIntFreqMap(albumFreqMap, 10);

        return new FriendsInsightsPopularCategoriesResponse(top10Songs, top10Artists, top10Albums);
    }

    public FriendsInsightsLeaderboardsResponse getLeaderboards(AppUser appUser, Optional<Integer> timePeriodInDays) {
        // Get set of all users friends
        List<Friend> myFriends = friendService.getFriends(appUser);
        Set<AppUser> allFriends = myFriends.stream().map(Friend::getFriendAppUser).collect(Collectors.toSet());

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

        Map<AppUser, Long> streamedSecondsMap = new HashMap<>();
        Map<AppUser, Set<Artist>> streamedArtistsMap = new HashMap<>();

        for (Stream stream : allStreams) {
            AppUser streamAppUser = stream.getAppUser();
            Set<Artist> artists = stream.getSong().getArtists();
            Duration duration = stream.getSong().getDuration();

            streamedSecondsMap.put(streamAppUser, streamedSecondsMap.getOrDefault(streamAppUser, 0L) + duration.getSeconds());

            Set<Artist> allStreamedArtists = streamedArtistsMap.getOrDefault(streamAppUser, new HashSet<>());
            allStreamedArtists.addAll(artists);
            streamedArtistsMap.put(streamAppUser, allStreamedArtists);
        }

        Map<FriendsInsightsLeaderboardsResponse.LeaderboardUser, Long> streamedSecondsFreqMap = new HashMap<>();
        Map<FriendsInsightsLeaderboardsResponse.LeaderboardUser, Integer> streamedArtistsFreqMap = new HashMap<>();

        streamedSecondsMap.forEach((streamAppUser, secondsStreamed) -> {
            FriendsInsightsLeaderboardsResponse.LeaderboardUser user = new FriendsInsightsLeaderboardsResponse.LeaderboardUser(
                streamAppUser
            );
            streamedSecondsFreqMap.put(user, secondsStreamed);
        });

        streamedArtistsMap.forEach((streamAppUser, artistSet) -> {
            FriendsInsightsLeaderboardsResponse.LeaderboardUser user = new FriendsInsightsLeaderboardsResponse.LeaderboardUser(
                streamAppUser
            );
            streamedArtistsFreqMap.put(user, artistSet.size());
        });

        List<Map.Entry<FriendsInsightsLeaderboardsResponse.LeaderboardUser, Long>> streamedSecondsLeaderboard = getTopNFromLongFreqMap(
            streamedSecondsFreqMap,
            10
        );
        List<Map.Entry<FriendsInsightsLeaderboardsResponse.LeaderboardUser, Integer>> streamedArtistsLeaderboard = getTopNFromIntFreqMap(
            streamedArtistsFreqMap,
            10
        );

        return new FriendsInsightsLeaderboardsResponse(streamedSecondsLeaderboard, streamedArtistsLeaderboard);
    }
}
