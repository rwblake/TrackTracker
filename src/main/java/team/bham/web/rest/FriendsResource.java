package team.bham.web.rest;

import static java.util.stream.Stream.concat;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.*;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.*;
import team.bham.service.FriendService;
import team.bham.service.account.AppUserService;
import team.bham.service.account.NoAppUserException;

@RestController
@RequestMapping("/api")
@Transactional
public class FriendsResource {

    private final Logger log = LoggerFactory.getLogger(FriendsResource.class);
    private final FriendRequestRepository friendRequestRepository;
    private final AppUserRepository appUserRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendService friendService;
    private final AppUserService appUserService;

    public FriendsResource(
        FriendRequestRepository friendRequestRepository,
        AppUserRepository appUserRepository,
        FriendshipRepository friendshipRepository,
        FriendService friendService,
        AppUserService appUserService
    ) {
        this.friendRequestRepository = friendRequestRepository;
        this.appUserRepository = appUserRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendService = friendService;
        this.appUserService = appUserService;
    }

    @GetMapping("/friends/appUsers")
    public List<AppUser> getAppUsers() throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
        List<Long> badIds = new ArrayList<>();

        // Only return users that aren't
        // -- the current user
        badIds.add(currentUser.getId());
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
    public List<Friend> getFriendships() throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
        return this.friendService.getFriends(currentUser);
    }

    @GetMapping("/friends/requests")
    public List<FriendRequest> getFriendRequests() throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
        // Get the friend requests from database
        return friendRequestRepository.findAllByToAppUser(currentUser);
    }

    @GetMapping("/friends/blocked")
    public List<AppUser> getBlocked() throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
        return new ArrayList<>(currentUser.getBlockedUsers());
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
    public List<FriendRecommendation> getRecommendations() throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();

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
    public List<FriendRequest> acceptFriendRequest(@Valid @RequestBody Long friendRequestId) throws NoAppUserException {
        try {
            friendService.acceptFriendRequest(friendRequestId);
        } catch (NoAppUserException e) {
            log.error("No AppUser was logged in");
        } catch (IllegalArgumentException e) {
            log.error("Cannot accept a friend request which was not directed toward the logged in AppUser");
        }
        return getFriendRequests();
    }

    @PostMapping("/friends/reject")
    public List<FriendRequest> rejectFriendRequest(@Valid @RequestBody Long friendRequestId) throws NoAppUserException {
        try {
            friendService.rejectFriendRequest(friendRequestId);
        } catch (NoAppUserException e) {
            log.error("No AppUser was logged in");
        } catch (IllegalArgumentException e) {
            log.error("Cannot reject a friend request which was not directed toward the logged in AppUser");
        }

        return getFriendRequests();
    }

    @PostMapping("/friends/pin")
    public void pinFriend(@Valid @RequestBody Long[] pinAppUserId) throws NoAppUserException {
        System.out.println("pinAppUserId: " + Arrays.toString(pinAppUserId));
        friendService.pinFriend(pinAppUserId);
    }

    @PostMapping("/friends/unpin")
    public void unpinFriend(@Valid @RequestBody Long[] unpinAppUserId) throws NoAppUserException {
        System.out.println("unpinAppUserId: " + Arrays.toString(unpinAppUserId));
        friendService.unpinFriend(unpinAppUserId);
    }

    @PostMapping("/friends/delete")
    public ResponseEntity<Void> deleteFriend(@Valid @RequestBody Long friendAppUserId) throws NoAppUserException {
        friendService.deleteFriend(friendAppUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friends/block")
    public ResponseEntity<Void> blockUser(@Valid @RequestBody Long appUserId) throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
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
    public ResponseEntity<Void> unblockUser(@Valid @RequestBody Long appUserId) throws NoAppUserException {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();
        AppUser otherUser = this.appUserRepository.getReferenceById(appUserId);

        // Remove from blocked list
        currentUser.removeBlockedUser(otherUser);
        otherUser.removeBlockedByUser(currentUser);
        this.appUserRepository.save(currentUser);
        this.appUserRepository.save(otherUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friends")
    public ResponseEntity<FriendRequest> createFriendRequest(@Valid @RequestBody Long otherAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        AppUser currentUser = appUserService.getCurrentAppUser();

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
        this.friendService.createFriendRequest(myFriendRequest);
        //this.friendRequestRepository.save(myFriendRequest);

        return ResponseEntity.created(new URI("/api/friends")).body(myFriendRequest);
    }
}
