package team.bham.web.rest;

import static java.util.stream.Stream.concat;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.*;
import team.bham.domain.enumeration.CardType;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.*;
import team.bham.service.FriendService;
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
    private final FriendService friendService;

    public FriendsResource(
        FriendRequestRepository friendRequestRepository,
        AppUserRepository appUserRepository,
        UserService userService,
        FriendshipRepository friendshipRepository,
        CardRepository cardRepository,
        FriendService friendService
    ) {
        this.friendRequestRepository = friendRequestRepository;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
        this.cardRepository = cardRepository;
        this.friendService = friendService;
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

    @GetMapping("/friends/appUsers")
    public List<AppUser> getAppUsers() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        List<Long> badIds = new ArrayList<>();

        // Only return users that aren't
        // -- blocked
        badIds.addAll(currentUser.getBlockedUsers().stream().map(AppUser::getId).collect(Collectors.toList()));
        badIds.addAll(currentUser.getBlockedByUsers().stream().map(AppUser::getId).collect(Collectors.toList()));
        // -- part of ongoing friend requests
        badIds.addAll(
            currentUser
                .getToFriendRequests()
                .stream()
                .map(FriendRequest::getInitiatingAppUser)
                .map(AppUser::getId)
                .collect(Collectors.toList())
        );
        badIds.addAll(
            currentUser
                .getIntitiatingFriendRequests()
                .stream()
                .map(FriendRequest::getToAppUser)
                .map(AppUser::getId)
                .collect(Collectors.toList())
        );
        // -- part of friendships
        badIds.addAll(
            currentUser
                .getFriendshipInitiateds()
                .stream()
                .map(Friendship::getFriendAccepting)
                .map(AppUser::getId)
                .collect(Collectors.toList())
        );
        badIds.addAll(
            currentUser
                .getFriendshipAccepteds()
                .stream()
                .map(Friendship::getFriendInitiating)
                .map(AppUser::getId)
                .collect(Collectors.toList())
        );

        // Only return users with global visibility preferences
        if (badIds.isEmpty()) {
            return this.appUserRepository.getAppUsersByUserPreferencesVisibility(VisibilityPreference.GLOBAL);
        } else {
            return this.appUserRepository.getAppUsersByUserPreferencesVisibilityAndIdNotIn(VisibilityPreference.GLOBAL, badIds);
        }
    }

    @GetMapping("/friends")
    public List<Friend> getFriendships() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        return this.friendService.getFriends(currentUser);
    }

    @GetMapping("/friends/requests")
    public List<FriendRequest> getFriendRequests() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        // Get the friend requests from database
        return friendRequestRepository.findAllByToAppUser(currentUser);
    }

    @GetMapping("/friends/blocked")
    public List<AppUser> getBlocked() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        return currentUser.getBlockedUsers().stream().collect(Collectors.toList());
    }

    private HashMap<String, Integer> genreFrequencies(AppUser appUser) {
        HashMap<String, Integer> ret = new HashMap<>();
        for (Stream stream : appUser.getStreams()) {
            for (Artist artist : stream.getSong().getArtists()) {
                for (Genre genre : artist.getGenres()) {
                    if (ret.containsKey(genre.getName())) {
                        ret.replace(genre.getName(), ret.get(genre.getName()) + 1);
                    } else {
                        ret.put(genre.getName(), 1);
                    }
                }
            }
        }
        return ret;
    }

    private Float similarity(AppUser a, AppUser b) {
        HashMap<String, Integer> fa = genreFrequencies(a);
        HashMap<String, Integer> fb = genreFrequencies(b);
        float similar = 0;
        float total = 0;
        Set<String> keySet = concat(fa.keySet().stream(), fb.keySet().stream()).collect(Collectors.toSet());
        for (String key : keySet) {
            if (fa.containsKey(key)) {
                total += fa.get(key);
                if (fb.containsKey(key)) {
                    total += fb.get(key);
                    similar += fb.get(key);
                    similar += fa.get(key);
                }
            } else {
                total += fb.get(key);
            }
        }
        return similar / total;
    }

    @GetMapping("/friends/recommendations")
    public List<FriendRecommendation> getRecommendations() throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();

        // Get available app users (not already friends/ blocked)
        List<AppUser> appUsers = getAppUsers();
        List<FriendRecommendation> recommendations = new ArrayList<>(appUsers.size());
        FriendRecommendation recommendation;
        for (AppUser appUser : appUsers) {
            recommendation = new FriendRecommendation();
            recommendation.setAboutAppUser(appUser);
            recommendation.setForAppUser(currentUser);
            recommendation.setSimilarity(similarity(currentUser, appUser));
            recommendations.add(recommendation);
        }
        return recommendations;
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

    @PostMapping("/friends/block")
    public ResponseEntity<Void> blockUser(@Valid @RequestBody Long appUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        AppUser otherUser = this.appUserRepository.getReferenceById(appUserId);

        // Add to blocked list
        currentUser.addBlockedUser(otherUser);
        otherUser.addBlockedByUser(currentUser);
        this.appUserRepository.save(currentUser);
        this.appUserRepository.save(otherUser);

        // Delete friend
        deleteFriend(appUserId);

        // Delete requests
        this.friendRequestRepository.deleteAllByInitiatingAppUserAndToAppUser(currentUser, otherUser);
        this.friendRequestRepository.deleteAllByInitiatingAppUserAndToAppUser(otherUser, currentUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friends/unblock")
    public ResponseEntity<Void> unblockUser(@Valid @RequestBody Long appUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        AppUser otherUser = this.appUserRepository.getReferenceById(appUserId);

        // Remove from blocked list
        currentUser.removeBlockedUser(otherUser);
        otherUser.removeBlockedByUser(currentUser);
        this.appUserRepository.save(currentUser);
        this.appUserRepository.save(otherUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friends/delete")
    public ResponseEntity<Void> deleteFriend(@Valid @RequestBody Long friendAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();

        // Delete friendship
        this.friendshipRepository.deleteAllByFriendAcceptingIdAndFriendInitiatingId(currentUser.getId(), friendAppUserId);
        this.friendshipRepository.deleteAllByFriendAcceptingIdAndFriendInitiatingId(friendAppUserId, currentUser.getId());

        // Delete any pinned friends
        unpinFriend(friendAppUserId);
        unpinFriend(currentUser.getId());
        return ResponseEntity.noContent().build();
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
        myCard.setMetricValue(pinAppUserId.intValue()); // Incompatible types (be cautious)
        myCard.setTimeGenerated(Instant.now());
        // Not sure what setUsages and setTimeFrame do
        this.cardRepository.save(myCard);
    }

    @PostMapping("/friends/unpin")
    public void unpinFriend(@Valid @RequestBody Long pinAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = getCurrentUser();
        this.cardRepository.deleteAllByAppUserIdAndMetricAndMetricValue(
                currentUser.getId(),
                CardType.PINNED_FRIEND,
                pinAppUserId.intValue()
            );
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
