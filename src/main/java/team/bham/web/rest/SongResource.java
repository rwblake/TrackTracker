package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.Song;
import team.bham.repository.SongRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Song}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SongResource {

    private final Logger log = LoggerFactory.getLogger(SongResource.class);

    private static final String ENTITY_NAME = "song";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SongRepository songRepository;

    public SongResource(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * {@code POST  /songs} : Create a new song.
     *
     * @param song the song to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new song, or with status {@code 400 (Bad Request)} if the song has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/songs")
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to save Song : {}", song);
        if (song.getId() != null) {
            throw new BadRequestAlertException("A new song cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Song result = songRepository.save(song);
        return ResponseEntity
            .created(new URI("/api/songs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /songs/:id} : Updates an existing song.
     *
     * @param id the id of the song to save.
     * @param song the song to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated song,
     * or with status {@code 400 (Bad Request)} if the song is not valid,
     * or with status {@code 500 (Internal Server Error)} if the song couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/songs/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Song song)
        throws URISyntaxException {
        log.debug("REST request to update Song : {}, {}", id, song);
        if (song.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, song.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!songRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Song result = songRepository.save(song);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, song.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /songs/:id} : Partial updates given fields of an existing song, field will ignore if it is null
     *
     * @param id the id of the song to save.
     * @param song the song to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated song,
     * or with status {@code 400 (Bad Request)} if the song is not valid,
     * or with status {@code 404 (Not Found)} if the song is not found,
     * or with status {@code 500 (Internal Server Error)} if the song couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/songs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Song> partialUpdateSong(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Song song
    ) throws URISyntaxException {
        log.debug("REST request to partial update Song partially : {}, {}", id, song);
        if (song.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, song.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!songRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Song> result = songRepository
            .findById(song.getId())
            .map(existingSong -> {
                if (song.getSpotifyID() != null) {
                    existingSong.setSpotifyID(song.getSpotifyID());
                }
                if (song.getName() != null) {
                    existingSong.setName(song.getName());
                }
                if (song.getImageURL() != null) {
                    existingSong.setImageURL(song.getImageURL());
                }
                if (song.getReleaseDate() != null) {
                    existingSong.setReleaseDate(song.getReleaseDate());
                }
                if (song.getDuration() != null) {
                    existingSong.setDuration(song.getDuration());
                }
                if (song.getAcousticness() != null) {
                    existingSong.setAcousticness(song.getAcousticness());
                }
                if (song.getDanceability() != null) {
                    existingSong.setDanceability(song.getDanceability());
                }
                if (song.getEnergy() != null) {
                    existingSong.setEnergy(song.getEnergy());
                }
                if (song.getInstrumentalness() != null) {
                    existingSong.setInstrumentalness(song.getInstrumentalness());
                }
                if (song.getMusicalKey() != null) {
                    existingSong.setMusicalKey(song.getMusicalKey());
                }
                if (song.getLiveness() != null) {
                    existingSong.setLiveness(song.getLiveness());
                }
                if (song.getLoudness() != null) {
                    existingSong.setLoudness(song.getLoudness());
                }
                if (song.getMode() != null) {
                    existingSong.setMode(song.getMode());
                }
                if (song.getSpeechiness() != null) {
                    existingSong.setSpeechiness(song.getSpeechiness());
                }
                if (song.getTempo() != null) {
                    existingSong.setTempo(song.getTempo());
                }
                if (song.getTimeSignature() != null) {
                    existingSong.setTimeSignature(song.getTimeSignature());
                }
                if (song.getValence() != null) {
                    existingSong.setValence(song.getValence());
                }

                return existingSong;
            })
            .map(songRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, song.getId().toString())
        );
    }

    /**
     * {@code GET  /songs} : get all the songs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of songs in body.
     */
    @GetMapping("/songs")
    public List<Song> getAllSongs() {
        log.debug("REST request to get all Songs");
        return songRepository.findAll();
    }

    /**
     * {@code GET  /songs/:id} : get the "id" song.
     *
     * @param id the id of the song to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the song, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/songs/{id}")
    public ResponseEntity<Song> getSong(@PathVariable Long id) {
        log.debug("REST request to get Song : {}", id);
        Optional<Song> song = songRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(song);
    }

    /**
     * {@code DELETE  /songs/:id} : delete the "id" song.
     *
     * @param id the id of the song to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        log.debug("REST request to delete Song : {}", id);
        songRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
