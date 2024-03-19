package team.bham.service.spotify;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.AppUser;
import team.bham.domain.Artist;
import team.bham.domain.Song;

public class FriendsInsightsLeaderboardsResponse implements Serializable {

    public static class LeaderboardUser implements Serializable {

        private final String username;
        private final String avatarUrl;

        public LeaderboardUser(AppUser appUser) {
            this.username = appUser.getInternalUser().getLogin();
            this.avatarUrl = appUser.getAvatarURL();
        }

        public String getUsername() {
            return username;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }
    }

    private final List<FriendsInsightsFrequencyPair<LeaderboardUser>> streamedSecondsLeaderboard;
    private final List<FriendsInsightsFrequencyPair<LeaderboardUser>> streamedArtistsLeaderboard;

    private <K> FriendsInsightsFrequencyPair<K> entryToFriendsInsightsFrequencyPairInt(Map.Entry<K, Integer> entry) {
        return new FriendsInsightsFrequencyPair<>(entry.getKey(), entry.getValue());
    }

    private <K> FriendsInsightsFrequencyPair<K> entryToFriendsInsightsFrequencyPairLong(Map.Entry<K, Long> entry) {
        return new FriendsInsightsFrequencyPair<>(entry.getKey(), entry.getValue());
    }

    private <K> List<FriendsInsightsFrequencyPair<K>> entriesToFriendsInsightsFrequencyPairsInt(List<Map.Entry<K, Integer>> entries) {
        return entries.stream().map(this::entryToFriendsInsightsFrequencyPairInt).collect(Collectors.toList());
    }

    private <K> List<FriendsInsightsFrequencyPair<K>> entriesToFriendsInsightsFrequencyPairsLong(List<Map.Entry<K, Long>> entries) {
        return entries.stream().map(this::entryToFriendsInsightsFrequencyPairLong).collect(Collectors.toList());
    }

    public FriendsInsightsLeaderboardsResponse(
        List<Map.Entry<LeaderboardUser, Long>> streamedSecondsLeaderboard,
        List<Map.Entry<LeaderboardUser, Integer>> streamedArtistsLeaderboard
    ) {
        this.streamedSecondsLeaderboard = entriesToFriendsInsightsFrequencyPairsLong(streamedSecondsLeaderboard);
        this.streamedArtistsLeaderboard = entriesToFriendsInsightsFrequencyPairsInt(streamedArtistsLeaderboard);
    }

    public List<FriendsInsightsFrequencyPair<LeaderboardUser>> getStreamedSecondsLeaderboard() {
        return streamedSecondsLeaderboard;
    }

    public List<FriendsInsightsFrequencyPair<LeaderboardUser>> getStreamedArtistsLeaderboard() {
        return streamedArtistsLeaderboard;
    }
}
