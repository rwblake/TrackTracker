package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.FriendRecommendation;

/**
 * Spring Data JPA repository for the FriendRecommendation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FriendRecommendationRepository extends JpaRepository<FriendRecommendation, Long> {}
