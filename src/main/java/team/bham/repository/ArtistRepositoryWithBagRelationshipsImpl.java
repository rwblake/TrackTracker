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
import team.bham.domain.Artist;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ArtistRepositoryWithBagRelationshipsImpl implements ArtistRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Artist> fetchBagRelationships(Optional<Artist> artist) {
        return artist.map(this::fetchSongs).map(this::fetchAlbums).map(this::fetchGenres);
    }

    @Override
    public Page<Artist> fetchBagRelationships(Page<Artist> artists) {
        return new PageImpl<>(fetchBagRelationships(artists.getContent()), artists.getPageable(), artists.getTotalElements());
    }

    @Override
    public List<Artist> fetchBagRelationships(List<Artist> artists) {
        return Optional.of(artists).map(this::fetchSongs).map(this::fetchAlbums).map(this::fetchGenres).orElse(Collections.emptyList());
    }

    Artist fetchSongs(Artist result) {
        return entityManager
            .createQuery("select artist from Artist artist left join fetch artist.songs where artist is :artist", Artist.class)
            .setParameter("artist", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Artist> fetchSongs(List<Artist> artists) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, artists.size()).forEach(index -> order.put(artists.get(index).getId(), index));
        List<Artist> result = entityManager
            .createQuery("select distinct artist from Artist artist left join fetch artist.songs where artist in :artists", Artist.class)
            .setParameter("artists", artists)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Artist fetchAlbums(Artist result) {
        return entityManager
            .createQuery("select artist from Artist artist left join fetch artist.albums where artist is :artist", Artist.class)
            .setParameter("artist", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Artist> fetchAlbums(List<Artist> artists) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, artists.size()).forEach(index -> order.put(artists.get(index).getId(), index));
        List<Artist> result = entityManager
            .createQuery("select distinct artist from Artist artist left join fetch artist.albums where artist in :artists", Artist.class)
            .setParameter("artists", artists)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Artist fetchGenres(Artist result) {
        return entityManager
            .createQuery("select artist from Artist artist left join fetch artist.genres where artist is :artist", Artist.class)
            .setParameter("artist", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Artist> fetchGenres(List<Artist> artists) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, artists.size()).forEach(index -> order.put(artists.get(index).getId(), index));
        List<Artist> result = entityManager
            .createQuery("select distinct artist from Artist artist left join fetch artist.genres where artist in :artists", Artist.class)
            .setParameter("artists", artists)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
