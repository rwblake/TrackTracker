package team.bham.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.User;

/**
 * Spring Data JPA repository for the AppUser entity.
 * Indicates which methods can be called on the database Entities.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findOneBySpotifyID(String spotifyID);

    Optional<AppUser> findOneByInternalUser(User internalUser);

    public boolean existsByInternalUser(User user);

    public AppUser getAppUserByInternalUser(User user);
}
