package team.bham.web.rest;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.AppUserRepository;
import team.bham.repository.CardRepository;
import team.bham.repository.FriendRequestRepository;
import team.bham.repository.FriendshipRepository;
import team.bham.service.UserService;

@RestController
@RequestMapping("/api")
@Transactional
public class FriendsResource {

    private final FriendRequestRepository friendRequestRepository;
    private final AppUserRepository appUserRepository;
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;
    private final CardRepository cardRepository;

    public FriendsResource(
        FriendRequestRepository friendRequestRepository,
        AppUserRepository appUserRepository,
        UserService userService,
        FriendshipRepository friendshipRepository,
        CardRepository cardRepository
    ) {
        this.friendRequestRepository = friendRequestRepository;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
        this.cardRepository = cardRepository;
    }

    /** Find the current user and check they have an associated AppUser entity */
    private AppUser getCurrentUser() throws Exception {
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            return this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            // Not logged in
            throw new Exception("Current User is not related to an AppUser.");
        }
    }

    @GetMapping("/friends")
    public List<Friend> getFriendships() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        // Get the friendships
        Set<Friendship> myFriendShips = friendshipRepository.findAllByFriendAcceptingOrFriendInitiating(currentUser, currentUser);
        // Convert into Friend objects, so that we only deal with the other person in the friendship,
        // rather than initiating and accepting AppUsers
        List<Friend> myFriends = new ArrayList<>(myFriendShips.size());
        Friend tmpFriend;
        for (Friendship myFriendship : myFriendShips) {
            if (myFriendship.getFriendInitiating().getId().longValue() == currentUser.getId().longValue()) {
                tmpFriend = new Friend(myFriendship.getCreatedAt(), true, myFriendship.getFriendAccepting(), myFriendship.getId());
            } else {
                tmpFriend = new Friend(myFriendship.getCreatedAt(), false, myFriendship.getFriendInitiating(), myFriendship.getId());
            }
            myFriends.add(tmpFriend);
        }
        return myFriends;
    }

    @GetMapping("/friends/requests")
    public List<FriendRequest> getFriendRequests() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        // Get the friend requests from database
        return friendRequestRepository.findAllByToAppUser(currentUser);
    }

    @PostMapping("/friends/accept")
    public List<FriendRequest> acceptFriendRequest(@Valid @RequestBody Long friendRequestId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();

        // Find the friend request
        FriendRequest friendRequest = this.friendRequestRepository.getReferenceById(friendRequestId);
        AppUser requestUser = friendRequest.getInitiatingAppUser();

        // Check they are the recipient of the friend request
        if (currentUser.getId() != friendRequest.getToAppUser().getId()) {
            throw new IllegalArgumentException("Can't accept friend request that isn't to the logged in user.");
        }

        // Create the friendship
        Friendship myFriendship = new Friendship();
        myFriendship.setCreatedAt(Instant.now());
        myFriendship.setFriendAccepting(currentUser);
        myFriendship.setFriendInitiating(requestUser);
        this.friendshipRepository.save(myFriendship);

        // Delete the friend request
        this.friendRequestRepository.delete(friendRequest);

        return getFriendRequests();
    }

    @PostMapping("/friends/reject")
    public List<FriendRequest> rejectFriendRequest(@Valid @RequestBody Long friendRequestId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();

        // Find the friend request
        FriendRequest friendRequest = this.friendRequestRepository.getReferenceById(friendRequestId);
        AppUser requestUser = friendRequest.getInitiatingAppUser();

        // Check they are the recipient of the friend request
        if (currentUser.getId() != friendRequest.getToAppUser().getId()) {
            throw new IllegalArgumentException("Can't accept friend request that isn't to the logged in user.");
        }

        // Delete the friend request
        this.friendRequestRepository.delete(friendRequest);

        return getFriendRequests();
    }

    @PostMapping("/friends/pin")
    public void pinFriend(@Valid @RequestBody Long pinAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        Card myCard = new Card();
        myCard.setAppUser(currentUser);
        myCard.setMetric(CardType.PINNED_FRIEND);
        myCard.setMetricValue(currentUser.getId().intValue()); // Incompatible types (be cautious)
        myCard.setTimeGenerated(Instant.now());
        // Not sure what setUsages and setTimeFrame do
        this.cardRepository.save(myCard);
    }

    @PostMapping("/friends/unpin")
    public void unpinFriend(@Valid @RequestBody Long pinAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        this.cardRepository.deleteByAppUserId(pinAppUserId);
    }

    @PostMapping("/friends")
    public ResponseEntity<FriendRequest> createFriendRequest(@Valid @RequestBody Long otherAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();

        // Find the user they want to be friends with
        AppUser requestUser;
        if (this.appUserRepository.existsById(otherAppUserId)) {
            requestUser = this.appUserRepository.getReferenceById(otherAppUserId);
        } else {
            // User doesn't exist
            throw new IllegalArgumentException("AppUser with id " + otherAppUserId + " does not exist.");
        }

        // Check friend request doesn't already exist
        if (this.friendRequestRepository.existsByInitiatingAppUserAndToAppUser(currentUser, requestUser)) {
            throw new IllegalArgumentException("Friend request already exists.");
        }

        // Check user hasn't already requested them
        if (this.friendRequestRepository.existsByInitiatingAppUserAndToAppUser(requestUser, currentUser)) {
            throw new IllegalArgumentException("Friend has already made a request to current user.");
        }

        // Check that they aren't already friends
        Long currentId = currentUser.getId();
        if (
            this.friendshipRepository.existsByFriendInitiatingAndFriendAccepting(currentUser, requestUser) ||
            this.friendshipRepository.existsByFriendInitiatingAndFriendAccepting(requestUser, currentUser)
        ) {
            throw new Exception("Users are already friends.");
        }

        // Check the other user hasn't blocked them
        if (requestUser.getBlockedUsers().stream().map(AppUser::getId).collect(Collectors.toList()).contains(currentUser.getId())) {
            throw new Exception("Attempt to request friendship whilst blocked has been denied.");
        }

        // Create the friend request
        FriendRequest myFriendRequest = new FriendRequest();
        myFriendRequest.setCreatedAt(Instant.now());
        myFriendRequest.setInitiatingAppUser(currentUser);
        myFriendRequest.setToAppUser(requestUser);
        this.friendRequestRepository.save(myFriendRequest);

        return ResponseEntity.created(new URI("/api/friends")).body(myFriendRequest);
    }
}
