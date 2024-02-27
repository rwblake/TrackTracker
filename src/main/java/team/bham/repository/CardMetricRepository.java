package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.CardMetric;

/**
 * Spring Data JPA repository for the CardMetric entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardMetricRepository extends JpaRepository<CardMetric, Long> {}
