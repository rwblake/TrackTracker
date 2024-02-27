package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.PlaylistStats;

/**
 * Spring Data JPA repository for the PlaylistStats entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlaylistStatsRepository extends JpaRepository<PlaylistStats, Long> {}
