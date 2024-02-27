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
import team.bham.domain.FeedCard;
import team.bham.repository.FeedCardRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.FeedCard}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FeedCardResource {

    private final Logger log = LoggerFactory.getLogger(FeedCardResource.class);

    private static final String ENTITY_NAME = "feedCard";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeedCardRepository feedCardRepository;

    public FeedCardResource(FeedCardRepository feedCardRepository) {
        this.feedCardRepository = feedCardRepository;
    }

    /**
     * {@code POST  /feed-cards} : Create a new feedCard.
     *
     * @param feedCard the feedCard to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feedCard, or with status {@code 400 (Bad Request)} if the feedCard has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/feed-cards")
    public ResponseEntity<FeedCard> createFeedCard(@Valid @RequestBody FeedCard feedCard) throws URISyntaxException {
        log.debug("REST request to save FeedCard : {}", feedCard);
        if (feedCard.getId() != null) {
            throw new BadRequestAlertException("A new feedCard cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FeedCard result = feedCardRepository.save(feedCard);
        return ResponseEntity
            .created(new URI("/api/feed-cards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /feed-cards/:id} : Updates an existing feedCard.
     *
     * @param id the id of the feedCard to save.
     * @param feedCard the feedCard to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedCard,
     * or with status {@code 400 (Bad Request)} if the feedCard is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feedCard couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/feed-cards/{id}")
    public ResponseEntity<FeedCard> updateFeedCard(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FeedCard feedCard
    ) throws URISyntaxException {
        log.debug("REST request to update FeedCard : {}, {}", id, feedCard);
        if (feedCard.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feedCard.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedCardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FeedCard result = feedCardRepository.save(feedCard);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, feedCard.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /feed-cards/:id} : Partial updates given fields of an existing feedCard, field will ignore if it is null
     *
     * @param id the id of the feedCard to save.
     * @param feedCard the feedCard to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedCard,
     * or with status {@code 400 (Bad Request)} if the feedCard is not valid,
     * or with status {@code 404 (Not Found)} if the feedCard is not found,
     * or with status {@code 500 (Internal Server Error)} if the feedCard couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/feed-cards/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FeedCard> partialUpdateFeedCard(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FeedCard feedCard
    ) throws URISyntaxException {
        log.debug("REST request to partial update FeedCard partially : {}, {}", id, feedCard);
        if (feedCard.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feedCard.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedCardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FeedCard> result = feedCardRepository
            .findById(feedCard.getId())
            .map(existingFeedCard -> {
                if (feedCard.getLiked() != null) {
                    existingFeedCard.setLiked(feedCard.getLiked());
                }

                return existingFeedCard;
            })
            .map(feedCardRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, feedCard.getId().toString())
        );
    }

    /**
     * {@code GET  /feed-cards} : get all the feedCards.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feedCards in body.
     */
    @GetMapping("/feed-cards")
    public List<FeedCard> getAllFeedCards() {
        log.debug("REST request to get all FeedCards");
        return feedCardRepository.findAll();
    }

    /**
     * {@code GET  /feed-cards/:id} : get the "id" feedCard.
     *
     * @param id the id of the feedCard to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feedCard, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/feed-cards/{id}")
    public ResponseEntity<FeedCard> getFeedCard(@PathVariable Long id) {
        log.debug("REST request to get FeedCard : {}", id);
        Optional<FeedCard> feedCard = feedCardRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(feedCard);
    }

    /**
     * {@code DELETE  /feed-cards/:id} : delete the "id" feedCard.
     *
     * @param id the id of the feedCard to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/feed-cards/{id}")
    public ResponseEntity<Void> deleteFeedCard(@PathVariable Long id) {
        log.debug("REST request to delete FeedCard : {}", id);
        feedCardRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
