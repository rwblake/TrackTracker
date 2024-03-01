package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.Stream;

/**
 * Spring Data JPA repository for the Stream entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {}
