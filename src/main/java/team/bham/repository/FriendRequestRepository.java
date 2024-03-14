package team.bham.repository;

import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.FriendRequest;

/**
 * Spring Data JPA repository for the FriendRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    public boolean existsByInitiatingAppUserAndToAppUser(@NotNull AppUser initiatingAppUser, AppUser toAppUser);
}
