package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.SharingPreference;

/**
 * Spring Data JPA repository for the SharingPreference entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SharingPreferenceRepository extends JpaRepository<SharingPreference, Long> {}
