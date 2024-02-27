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
import team.bham.domain.Feed;
import team.bham.repository.FeedRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Feed}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FeedResource {

    private final Logger log = LoggerFactory.getLogger(FeedResource.class);

    private static final String ENTITY_NAME = "feed";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeedRepository feedRepository;

    public FeedResource(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    /**
     * {@code POST  /feeds} : Create a new feed.
     *
     * @param feed the feed to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feed, or with status {@code 400 (Bad Request)} if the feed has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/feeds")
    public ResponseEntity<Feed> createFeed(@Valid @RequestBody Feed feed) throws URISyntaxException {
        log.debug("REST request to save Feed : {}", feed);
        if (feed.getId() != null) {
            throw new BadRequestAlertException("A new feed cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Feed result = feedRepository.save(feed);
        return ResponseEntity
            .created(new URI("/api/feeds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /feeds/:id} : Updates an existing feed.
     *
     * @param id the id of the feed to save.
     * @param feed the feed to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feed,
     * or with status {@code 400 (Bad Request)} if the feed is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feed couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/feeds/{id}")
    public ResponseEntity<Feed> updateFeed(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Feed feed)
        throws URISyntaxException {
        log.debug("REST request to update Feed : {}, {}", id, feed);
        if (feed.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feed.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Feed result = feedRepository.save(feed);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, feed.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /feeds/:id} : Partial updates given fields of an existing feed, field will ignore if it is null
     *
     * @param id the id of the feed to save.
     * @param feed the feed to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feed,
     * or with status {@code 400 (Bad Request)} if the feed is not valid,
     * or with status {@code 404 (Not Found)} if the feed is not found,
     * or with status {@code 500 (Internal Server Error)} if the feed couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/feeds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Feed> partialUpdateFeed(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Feed feed
    ) throws URISyntaxException {
        log.debug("REST request to partial update Feed partially : {}, {}", id, feed);
        if (feed.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feed.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Feed> result = feedRepository
            .findById(feed.getId())
            .map(existingFeed -> {
                if (feed.getLastUpdated() != null) {
                    existingFeed.setLastUpdated(feed.getLastUpdated());
                }

                return existingFeed;
            })
            .map(feedRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, feed.getId().toString())
        );
    }

    /**
     * {@code GET  /feeds} : get all the feeds.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feeds in body.
     */
    @GetMapping("/feeds")
    public List<Feed> getAllFeeds(@RequestParam(required = false) String filter) {
        if ("appuser-is-null".equals(filter)) {
            log.debug("REST request to get all Feeds where appUser is null");
            return StreamSupport
                .stream(feedRepository.findAll().spliterator(), false)
                .filter(feed -> feed.getAppUser() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all Feeds");
        return feedRepository.findAll();
    }

    /**
     * {@code GET  /feeds/:id} : get the "id" feed.
     *
     * @param id the id of the feed to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feed, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/feeds/{id}")
    public ResponseEntity<Feed> getFeed(@PathVariable Long id) {
        log.debug("REST request to get Feed : {}", id);
        Optional<Feed> feed = feedRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(feed);
    }

    /**
     * {@code DELETE  /feeds/:id} : delete the "id" feed.
     *
     * @param id the id of the feed to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/feeds/{id}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Long id) {
        log.debug("REST request to delete Feed : {}", id);
        feedRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
