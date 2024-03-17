package team.bham.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.Stream;

/**
 * Spring Data JPA repository for the Stream entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    public List<Stream> findAllByPlayedAtAfterAndAppUserOrderByPlayedAt(Instant playedAt, AppUser appUser);

    public List<Stream> findStreamByAppUserOrderByPlayedAtDesc(AppUser appUser);
}
