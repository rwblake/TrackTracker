package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import team.bham.domain.Playlist;

public interface PlaylistRepositoryWithBagRelationships {
    Optional<Playlist> fetchBagRelationships(Optional<Playlist> playlist);

    List<Playlist> fetchBagRelationships(List<Playlist> playlists);

    Page<Playlist> fetchBagRelationships(Page<Playlist> playlists);
}
