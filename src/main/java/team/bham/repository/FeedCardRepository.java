package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.FeedCard;

/**
 * Spring Data JPA repository for the FeedCard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedCardRepository extends JpaRepository<FeedCard, Long> {}
