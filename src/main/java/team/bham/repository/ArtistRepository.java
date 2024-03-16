package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.Artist;

/**
 * Spring Data JPA repository for the Artist entity.
 *
 * When extending this class, extend ArtistRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ArtistRepository extends ArtistRepositoryWithBagRelationships, JpaRepository<Artist, Long> {
    default Optional<Artist> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Artist> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Artist> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    public Boolean existsBySpotifyID(String spotifyID);

    public Artist findArtistBySpotifyID(String spotifyID);
}
