package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.PlaylistStats;
import team.bham.repository.PlaylistStatsRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.PlaylistStats}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistStatsResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistStatsResource.class);

    private static final String ENTITY_NAME = "playlistStats";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlaylistStatsRepository playlistStatsRepository;

    public PlaylistStatsResource(PlaylistStatsRepository playlistStatsRepository) {
        this.playlistStatsRepository = playlistStatsRepository;
    }

    /**
     * {@code POST  /playlist-stats} : Create a new playlistStats.
     *
     * @param playlistStats the playlistStats to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playlistStats, or with status {@code 400 (Bad Request)} if the playlistStats has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/playlist-stats")
    public ResponseEntity<PlaylistStats> createPlaylistStats(@Valid @RequestBody PlaylistStats playlistStats) throws URISyntaxException {
        log.debug("REST request to save PlaylistStats : {}", playlistStats);
        if (playlistStats.getId() != null) {
            throw new BadRequestAlertException("A new playlistStats cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlaylistStats result = playlistStatsRepository.save(playlistStats);
        return ResponseEntity
            .created(new URI("/api/playlist-stats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /playlist-stats/:id} : Updates an existing playlistStats.
     *
     * @param id the id of the playlistStats to save.
     * @param playlistStats the playlistStats to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playlistStats,
     * or with status {@code 400 (Bad Request)} if the playlistStats is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playlistStats couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/playlist-stats/{id}")
    public ResponseEntity<PlaylistStats> updatePlaylistStats(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlaylistStats playlistStats
    ) throws URISyntaxException {
        log.debug("REST request to update PlaylistStats : {}, {}", id, playlistStats);
        if (playlistStats.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playlistStats.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playlistStatsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PlaylistStats result = playlistStatsRepository.save(playlistStats);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playlistStats.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /playlist-stats/:id} : Partial updates given fields of an existing playlistStats, field will ignore if it is null
     *
     * @param id the id of the playlistStats to save.
     * @param playlistStats the playlistStats to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playlistStats,
     * or with status {@code 400 (Bad Request)} if the playlistStats is not valid,
     * or with status {@code 404 (Not Found)} if the playlistStats is not found,
     * or with status {@code 500 (Internal Server Error)} if the playlistStats couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/playlist-stats/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlaylistStats> partialUpdatePlaylistStats(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlaylistStats playlistStats
    ) throws URISyntaxException {
        log.debug("REST request to partial update PlaylistStats partially : {}, {}", id, playlistStats);
        if (playlistStats.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playlistStats.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playlistStatsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlaylistStats> result = playlistStatsRepository
            .findById(playlistStats.getId())
            .map(existingPlaylistStats -> {
                if (playlistStats.getPlaylistLength() != null) {
                    existingPlaylistStats.setPlaylistLength(playlistStats.getPlaylistLength());
                }
                if (playlistStats.getLastUpdated() != null) {
                    existingPlaylistStats.setLastUpdated(playlistStats.getLastUpdated());
                }

                return existingPlaylistStats;
            })
            .map(playlistStatsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playlistStats.getId().toString())
        );
    }

    /**
     * {@code GET  /playlist-stats} : get all the playlistStats.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playlistStats in body.
     */
    @GetMapping("/playlist-stats")
    public List<PlaylistStats> getAllPlaylistStats(@RequestParam(required = false) String filter) {
        if ("playlist-is-null".equals(filter)) {
            log.debug("REST request to get all PlaylistStatss where playlist is null");
            return StreamSupport
                .stream(playlistStatsRepository.findAll().spliterator(), false)
                .filter(playlistStats -> playlistStats.getPlaylist() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all PlaylistStats");
        return playlistStatsRepository.findAll();
    }

    /**
     * {@code GET  /playlist-stats/:id} : get the "id" playlistStats.
     *
     * @param id the id of the playlistStats to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playlistStats, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/playlist-stats/{id}")
    public ResponseEntity<PlaylistStats> getPlaylistStats(@PathVariable Long id) {
        log.debug("REST request to get PlaylistStats : {}", id);
        Optional<PlaylistStats> playlistStats = playlistStatsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(playlistStats);
    }

    /**
     * {@code DELETE  /playlist-stats/:id} : delete the "id" playlistStats.
     *
     * @param id the id of the playlistStats to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/playlist-stats/{id}")
    public ResponseEntity<Void> deletePlaylistStats(@PathVariable Long id) {
        log.debug("REST request to delete PlaylistStats : {}", id);
        playlistStatsRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
