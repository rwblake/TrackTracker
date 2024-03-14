package team.bham.web.rest;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.bham.domain.AppUser;
import team.bham.domain.FriendRequest;
import team.bham.domain.Friendship;
import team.bham.domain.User;
import team.bham.repository.AppUserRepository;
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

    public FriendsResource(
        FriendRequestRepository friendRequestRepository,
        AppUserRepository appUserRepository,
        UserService userService,
        FriendshipRepository friendshipRepository
    ) {
        this.friendRequestRepository = friendRequestRepository;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
    }

    @PostMapping("/friends")
    public ResponseEntity<FriendRequest> createFriendRequest(@Valid @RequestBody Long otherAppUserId) throws Exception {
        // Find the current user and check they have an associated AppUser entity
        Optional<User> userMaybe = this.userService.getUserWithAuthorities();
        AppUser currentUser;
        if (userMaybe.isPresent() && this.appUserRepository.existsByInternalUser(userMaybe.get())) {
            // Logged in
            currentUser = this.appUserRepository.getAppUserByInternalUser(userMaybe.get());
        } else {
            // Not logged in
            throw new Exception("Current User is not related to an AppUser.");
        }

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
            this.friendshipRepository.existsByFriendInitiatingOrFriendAcceptingId(currentUser, currentId) ||
            this.friendshipRepository.existsByFriendAcceptingOrFriendInitiatingId(currentUser, currentId)
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
