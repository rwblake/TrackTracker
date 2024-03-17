package team.bham.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.repository.FriendshipRepository;
import team.bham.repository.StreamRepository;

@Service
public class FriendService {

    private final StreamRepository streamRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendService(StreamRepository streamRepository, FriendshipRepository friendshipRepository) {
        this.streamRepository = streamRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public List<Friend> getFriends(AppUser me) {
        Set<Friendship> myFriendShips = friendshipRepository.findAllByFriendAcceptingOrFriendInitiating(me, me);

        // Convert into Friend objects, so that we only deal with the other person in the friendship,
        // rather than initiating and accepting AppUsers

        List<Friend> myFriends = new ArrayList<>(myFriendShips.size());
        AppUser them;
        boolean initiatedByCurrentUser;
        Friend tmpFriend;
        List<Stream> streams;
        Song mostRecentSong = null;

        for (Friendship myFriendship : myFriendShips) {
            if (myFriendship.getFriendInitiating().getId().longValue() == me.getId().longValue()) {
                them = myFriendship.getFriendAccepting();
                initiatedByCurrentUser = true;
            } else {
                them = myFriendship.getFriendInitiating();
                initiatedByCurrentUser = false;
            }
            streams = this.streamRepository.findStreamByAppUserOrderByPlayedAtDesc(them);
            if (!streams.isEmpty()) {
                mostRecentSong = streams.get(0).getSong();
            }
            myFriends.add(new Friend(myFriendship.getCreatedAt(), initiatedByCurrentUser, them, mostRecentSong, myFriendship.getId()));
        }
        return myFriends;
    }
}
