package team.bham.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import team.bham.domain.AppUser;
import team.bham.domain.Friendship;
import team.bham.domain.Playlist;
import team.bham.domain.PlaylistStats;
import team.bham.repository.FriendshipRepository;
import team.bham.repository.PlaylistStatsRepository;
import team.bham.repository.SongRepository;
import team.bham.service.spotify.PlaylistInsightCalculator;
import team.bham.service.spotify.SimpleSong;

@Service
public class FriendsService {

    private final FriendshipRepository friendshipRepository;

    public FriendsService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public Set<Friendship> getUsersFriends(AppUser appUser) {
        return friendshipRepository.findAllByFriendAcceptingOrFriendInitiating(appUser, appUser);
    }
}
