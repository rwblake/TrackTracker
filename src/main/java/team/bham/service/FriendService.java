package team.bham.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.bham.domain.*;
import team.bham.repository.FriendRequestRepository;
import team.bham.repository.FriendshipRepository;
import team.bham.repository.StreamRepository;
import team.bham.service.account.AppUserService;
import team.bham.service.account.NoAppUserException;
import team.bham.service.feed.CardService;

@Service
public class FriendService {

    private final Logger log = LoggerFactory.getLogger(FriendService.class);
    private final StreamRepository streamRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final CardService cardService;
    private final AppUserService appUserService;

    public FriendService(
        StreamRepository streamRepository,
        FriendshipRepository friendshipRepository,
        FriendRequestRepository friendRequestRepository,
        CardService cardService,
        AppUserService appUserService
    ) {
        this.streamRepository = streamRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.cardService = cardService;
        this.appUserService = appUserService;
    }

    /** Handles the backend logic for creating a friend request.
     * @param friendRequest the FriendRequest object to be saved into the database
     * @return the friendRequest that has now been saved in the database*/
    public FriendRequest createFriendRequest(FriendRequest friendRequest) {
        // Save the friend request in the database
        friendRequest = friendRequestRepository.save(friendRequest);
        log.debug("Saved new FriendRequest in database");

        // Add a new Friend Request card to the recipient's feed
        AppUser toAppUser = friendRequest.getToAppUser();
        AppUser fromAppUser = friendRequest.getInitiatingAppUser();
        log.debug("Making a new FriendRequest card");
        cardService.createFriendRequestCard(toAppUser, fromAppUser.getId());

        return friendRequest;
    }

    /** Handles the backend logic for accepting a friend request.
     * @param friendRequestId the id of the friend request to be accepted
     * @throws IllegalArgumentException the logged-in user is not the recipient of the friend request being accepted
     * @throws NoAppUserException there is no AppUser associated with the logged in account (or no account is logged in)
     */
    public void acceptFriendRequest(Long friendRequestId) throws IllegalArgumentException, NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();

        // Find the friend request
        FriendRequest friendRequest = friendRequestRepository.getReferenceById(friendRequestId);
        AppUser requestUser = friendRequest.getInitiatingAppUser();

        // Check they are the recipient of the friend request
        if (currentUser.getId().longValue() != friendRequest.getToAppUser().getId().longValue()) {
            throw new IllegalArgumentException("Can't accept friend request that isn't to the logged in user.");
        }

        // Create the friendship
        Friendship myFriendship = new Friendship();
        myFriendship.setCreatedAt(Instant.now());
        myFriendship.setFriendAccepting(currentUser);
        myFriendship.setFriendInitiating(requestUser);
        friendshipRepository.save(myFriendship);

        // Add new cards to each friend's feed, notifying them of the new friendship
        cardService.createNewFriendCard(currentUser, requestUser.getId());
        cardService.createNewFriendCard(requestUser, currentUser.getId());

        // Delete the friend request
        friendRequestRepository.delete(friendRequest);
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
