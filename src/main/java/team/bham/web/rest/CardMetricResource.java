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
import team.bham.domain.CardMetric;
import team.bham.repository.CardMetricRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.CardMetric}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CardMetricResource {

    private final Logger log = LoggerFactory.getLogger(CardMetricResource.class);

    private static final String ENTITY_NAME = "cardMetric";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CardMetricRepository cardMetricRepository;

    public CardMetricResource(CardMetricRepository cardMetricRepository) {
        this.cardMetricRepository = cardMetricRepository;
    }

    /**
     * {@code POST  /card-metrics} : Create a new cardMetric.
     *
     * @param cardMetric the cardMetric to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cardMetric, or with status {@code 400 (Bad Request)} if the cardMetric has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/card-metrics")
    public ResponseEntity<CardMetric> createCardMetric(@Valid @RequestBody CardMetric cardMetric) throws URISyntaxException {
        log.debug("REST request to save CardMetric : {}", cardMetric);
        if (cardMetric.getId() != null) {
            throw new BadRequestAlertException("A new cardMetric cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CardMetric result = cardMetricRepository.save(cardMetric);
        return ResponseEntity
            .created(new URI("/api/card-metrics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /card-metrics/:id} : Updates an existing cardMetric.
     *
     * @param id the id of the cardMetric to save.
     * @param cardMetric the cardMetric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardMetric,
     * or with status {@code 400 (Bad Request)} if the cardMetric is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cardMetric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/card-metrics/{id}")
    public ResponseEntity<CardMetric> updateCardMetric(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CardMetric cardMetric
    ) throws URISyntaxException {
        log.debug("REST request to update CardMetric : {}, {}", id, cardMetric);
        if (cardMetric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardMetric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardMetricRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CardMetric result = cardMetricRepository.save(cardMetric);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cardMetric.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /card-metrics/:id} : Partial updates given fields of an existing cardMetric, field will ignore if it is null
     *
     * @param id the id of the cardMetric to save.
     * @param cardMetric the cardMetric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardMetric,
     * or with status {@code 400 (Bad Request)} if the cardMetric is not valid,
     * or with status {@code 404 (Not Found)} if the cardMetric is not found,
     * or with status {@code 500 (Internal Server Error)} if the cardMetric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/card-metrics/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CardMetric> partialUpdateCardMetric(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CardMetric cardMetric
    ) throws URISyntaxException {
        log.debug("REST request to partial update CardMetric partially : {}, {}", id, cardMetric);
        if (cardMetric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardMetric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardMetricRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CardMetric> result = cardMetricRepository
            .findById(cardMetric.getId())
            .map(existingCardMetric -> {
                if (cardMetric.getMetric() != null) {
                    existingCardMetric.setMetric(cardMetric.getMetric());
                }

                return existingCardMetric;
            })
            .map(cardMetricRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cardMetric.getId().toString())
        );
    }

    /**
     * {@code GET  /card-metrics} : get all the cardMetrics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cardMetrics in body.
     */
    @GetMapping("/card-metrics")
    public List<CardMetric> getAllCardMetrics() {
        log.debug("REST request to get all CardMetrics");
        return cardMetricRepository.findAll();
    }

    /**
     * {@code GET  /card-metrics/:id} : get the "id" cardMetric.
     *
     * @param id the id of the cardMetric to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cardMetric, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/card-metrics/{id}")
    public ResponseEntity<CardMetric> getCardMetric(@PathVariable Long id) {
        log.debug("REST request to get CardMetric : {}", id);
        Optional<CardMetric> cardMetric = cardMetricRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cardMetric);
    }

    /**
     * {@code DELETE  /card-metrics/:id} : delete the "id" cardMetric.
     *
     * @param id the id of the cardMetric to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/card-metrics/{id}")
    public ResponseEntity<Void> deleteCardMetric(@PathVariable Long id) {
        log.debug("REST request to delete CardMetric : {}", id);
        cardMetricRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
