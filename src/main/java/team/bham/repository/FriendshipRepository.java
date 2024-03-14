package team.bham.repository;

import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.Friendship;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    public boolean existsByFriendAcceptingOrFriendInitiatingId(@NotNull AppUser friendAccepting, Long friendInitiating_id);

    public boolean existsByFriendInitiatingOrFriendAcceptingId(@NotNull AppUser friendInitiating, Long friendAccepting_id);
}
