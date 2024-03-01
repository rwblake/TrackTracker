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
import team.bham.domain.Stream;
import team.bham.repository.StreamRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Stream}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StreamResource {

    private final Logger log = LoggerFactory.getLogger(StreamResource.class);

    private static final String ENTITY_NAME = "stream";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StreamRepository streamRepository;

    public StreamResource(StreamRepository streamRepository) {
        this.streamRepository = streamRepository;
    }

    /**
     * {@code POST  /streams} : Create a new stream.
     *
     * @param stream the stream to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stream, or with status {@code 400 (Bad Request)} if the stream has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/streams")
    public ResponseEntity<Stream> createStream(@Valid @RequestBody Stream stream) throws URISyntaxException {
        log.debug("REST request to save Stream : {}", stream);
        if (stream.getId() != null) {
            throw new BadRequestAlertException("A new stream cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Stream result = streamRepository.save(stream);
        return ResponseEntity
            .created(new URI("/api/streams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /streams/:id} : Updates an existing stream.
     *
     * @param id the id of the stream to save.
     * @param stream the stream to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stream,
     * or with status {@code 400 (Bad Request)} if the stream is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stream couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/streams/{id}")
    public ResponseEntity<Stream> updateStream(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Stream stream
    ) throws URISyntaxException {
        log.debug("REST request to update Stream : {}, {}", id, stream);
        if (stream.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stream.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!streamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Stream result = streamRepository.save(stream);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stream.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /streams/:id} : Partial updates given fields of an existing stream, field will ignore if it is null
     *
     * @param id the id of the stream to save.
     * @param stream the stream to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stream,
     * or with status {@code 400 (Bad Request)} if the stream is not valid,
     * or with status {@code 404 (Not Found)} if the stream is not found,
     * or with status {@code 500 (Internal Server Error)} if the stream couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/streams/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Stream> partialUpdateStream(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Stream stream
    ) throws URISyntaxException {
        log.debug("REST request to partial update Stream partially : {}, {}", id, stream);
        if (stream.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stream.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!streamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stream> result = streamRepository
            .findById(stream.getId())
            .map(existingStream -> {
                if (stream.getPlayedAt() != null) {
                    existingStream.setPlayedAt(stream.getPlayedAt());
                }

                return existingStream;
            })
            .map(streamRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stream.getId().toString())
        );
    }

    /**
     * {@code GET  /streams} : get all the streams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of streams in body.
     */
    @GetMapping("/streams")
    public List<Stream> getAllStreams() {
        log.debug("REST request to get all Streams");
        return streamRepository.findAll();
    }

    /**
     * {@code GET  /streams/:id} : get the "id" stream.
     *
     * @param id the id of the stream to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stream, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/streams/{id}")
    public ResponseEntity<Stream> getStream(@PathVariable Long id) {
        log.debug("REST request to get Stream : {}", id);
        Optional<Stream> stream = streamRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stream);
    }

    /**
     * {@code DELETE  /streams/:id} : delete the "id" stream.
     *
     * @param id the id of the stream to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/streams/{id}")
    public ResponseEntity<Void> deleteStream(@PathVariable Long id) {
        log.debug("REST request to delete Stream : {}", id);
        streamRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
