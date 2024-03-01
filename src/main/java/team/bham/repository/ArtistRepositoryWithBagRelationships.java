package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import team.bham.domain.Artist;

public interface ArtistRepositoryWithBagRelationships {
    Optional<Artist> fetchBagRelationships(Optional<Artist> artist);

    List<Artist> fetchBagRelationships(List<Artist> artists);

    Page<Artist> fetchBagRelationships(Page<Artist> artists);
}
