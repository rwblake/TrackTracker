package team.bham.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import team.bham.domain.Playlist;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PlaylistRepositoryWithBagRelationshipsImpl implements PlaylistRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Playlist> fetchBagRelationships(Optional<Playlist> playlist) {
        return playlist.map(this::fetchSongs);
    }

    @Override
    public Page<Playlist> fetchBagRelationships(Page<Playlist> playlists) {
        return new PageImpl<>(fetchBagRelationships(playlists.getContent()), playlists.getPageable(), playlists.getTotalElements());
    }

    @Override
    public List<Playlist> fetchBagRelationships(List<Playlist> playlists) {
        return Optional.of(playlists).map(this::fetchSongs).orElse(Collections.emptyList());
    }

    Playlist fetchSongs(Playlist result) {
        return entityManager
            .createQuery(
                "select playlist from Playlist playlist left join fetch playlist.songs where playlist is :playlist",
                Playlist.class
            )
            .setParameter("playlist", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Playlist> fetchSongs(List<Playlist> playlists) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, playlists.size()).forEach(index -> order.put(playlists.get(index).getId(), index));
        List<Playlist> result = entityManager
            .createQuery(
                "select distinct playlist from Playlist playlist left join fetch playlist.songs where playlist in :playlists",
                Playlist.class
            )
            .setParameter("playlists", playlists)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
