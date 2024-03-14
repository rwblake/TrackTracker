package team.bham.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.Friendship;
import team.bham.domain.User;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    public boolean existsByFriendAcceptingOrFriendInitiatingId(@NotNull AppUser friendAccepting, Long friendInitiating_id);

    public boolean existsByFriendInitiatingOrFriendAcceptingId(@NotNull AppUser friendInitiating, Long friendAccepting_id);

    Set<Friendship> findAllByAppUser(AppUser appUser);
}
