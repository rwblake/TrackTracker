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
import team.bham.domain.FriendRecommendation;
import team.bham.repository.FriendRecommendationRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.FriendRecommendation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FriendRecommendationResource {

    private final Logger log = LoggerFactory.getLogger(FriendRecommendationResource.class);

    private static final String ENTITY_NAME = "friendRecommendation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FriendRecommendationRepository friendRecommendationRepository;

    public FriendRecommendationResource(FriendRecommendationRepository friendRecommendationRepository) {
        this.friendRecommendationRepository = friendRecommendationRepository;
    }

    /**
     * {@code POST  /friend-recommendations} : Create a new friendRecommendation.
     *
     * @param friendRecommendation the friendRecommendation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new friendRecommendation, or with status {@code 400 (Bad Request)} if the friendRecommendation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/friend-recommendations")
    public ResponseEntity<FriendRecommendation> createFriendRecommendation(@Valid @RequestBody FriendRecommendation friendRecommendation)
        throws URISyntaxException {
        log.debug("REST request to save FriendRecommendation : {}", friendRecommendation);
        if (friendRecommendation.getId() != null) {
            throw new BadRequestAlertException("A new friendRecommendation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FriendRecommendation result = friendRecommendationRepository.save(friendRecommendation);
        return ResponseEntity
            .created(new URI("/api/friend-recommendations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /friend-recommendations/:id} : Updates an existing friendRecommendation.
     *
     * @param id the id of the friendRecommendation to save.
     * @param friendRecommendation the friendRecommendation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendRecommendation,
     * or with status {@code 400 (Bad Request)} if the friendRecommendation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the friendRecommendation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/friend-recommendations/{id}")
    public ResponseEntity<FriendRecommendation> updateFriendRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FriendRecommendation friendRecommendation
    ) throws URISyntaxException {
        log.debug("REST request to update FriendRecommendation : {}, {}", id, friendRecommendation);
        if (friendRecommendation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendRecommendation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FriendRecommendation result = friendRecommendationRepository.save(friendRecommendation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendRecommendation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /friend-recommendations/:id} : Partial updates given fields of an existing friendRecommendation, field will ignore if it is null
     *
     * @param id the id of the friendRecommendation to save.
     * @param friendRecommendation the friendRecommendation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendRecommendation,
     * or with status {@code 400 (Bad Request)} if the friendRecommendation is not valid,
     * or with status {@code 404 (Not Found)} if the friendRecommendation is not found,
     * or with status {@code 500 (Internal Server Error)} if the friendRecommendation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/friend-recommendations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FriendRecommendation> partialUpdateFriendRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FriendRecommendation friendRecommendation
    ) throws URISyntaxException {
        log.debug("REST request to partial update FriendRecommendation partially : {}, {}", id, friendRecommendation);
        if (friendRecommendation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendRecommendation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FriendRecommendation> result = friendRecommendationRepository
            .findById(friendRecommendation.getId())
            .map(existingFriendRecommendation -> {
                if (friendRecommendation.getSimilarity() != null) {
                    existingFriendRecommendation.setSimilarity(friendRecommendation.getSimilarity());
                }
                if (friendRecommendation.getCreatedAt() != null) {
                    existingFriendRecommendation.setCreatedAt(friendRecommendation.getCreatedAt());
                }

                return existingFriendRecommendation;
            })
            .map(friendRecommendationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendRecommendation.getId().toString())
        );
    }

    /**
     * {@code GET  /friend-recommendations} : get all the friendRecommendations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of friendRecommendations in body.
     */
    @GetMapping("/friend-recommendations")
    public List<FriendRecommendation> getAllFriendRecommendations() {
        log.debug("REST request to get all FriendRecommendations");
        return friendRecommendationRepository.findAll();
    }

    /**
     * {@code GET  /friend-recommendations/:id} : get the "id" friendRecommendation.
     *
     * @param id the id of the friendRecommendation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the friendRecommendation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/friend-recommendations/{id}")
    public ResponseEntity<FriendRecommendation> getFriendRecommendation(@PathVariable Long id) {
        log.debug("REST request to get FriendRecommendation : {}", id);
        Optional<FriendRecommendation> friendRecommendation = friendRecommendationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(friendRecommendation);
    }

    /**
     * {@code DELETE  /friend-recommendations/:id} : delete the "id" friendRecommendation.
     *
     * @param id the id of the friendRecommendation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/friend-recommendations/{id}")
    public ResponseEntity<Void> deleteFriendRecommendation(@PathVariable Long id) {
        log.debug("REST request to delete FriendRecommendation : {}", id);
        friendRecommendationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
