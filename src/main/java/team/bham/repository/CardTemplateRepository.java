package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.CardTemplate;

/**
 * Spring Data JPA repository for the CardTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardTemplateRepository extends JpaRepository<CardTemplate, Long> {}
