package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.SpotifyToken;

/**
 * Spring Data JPA repository for the SpotifyToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, Long> {}
