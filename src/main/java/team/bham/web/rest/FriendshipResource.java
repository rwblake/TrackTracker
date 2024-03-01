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
import team.bham.domain.Friendship;
import team.bham.repository.FriendshipRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Friendship}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FriendshipResource {

    private final Logger log = LoggerFactory.getLogger(FriendshipResource.class);

    private static final String ENTITY_NAME = "friendship";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FriendshipRepository friendshipRepository;

    public FriendshipResource(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * {@code POST  /friendships} : Create a new friendship.
     *
     * @param friendship the friendship to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new friendship, or with status {@code 400 (Bad Request)} if the friendship has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/friendships")
    public ResponseEntity<Friendship> createFriendship(@Valid @RequestBody Friendship friendship) throws URISyntaxException {
        log.debug("REST request to save Friendship : {}", friendship);
        if (friendship.getId() != null) {
            throw new BadRequestAlertException("A new friendship cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Friendship result = friendshipRepository.save(friendship);
        return ResponseEntity
            .created(new URI("/api/friendships/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /friendships/:id} : Updates an existing friendship.
     *
     * @param id the id of the friendship to save.
     * @param friendship the friendship to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendship,
     * or with status {@code 400 (Bad Request)} if the friendship is not valid,
     * or with status {@code 500 (Internal Server Error)} if the friendship couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/friendships/{id}")
    public ResponseEntity<Friendship> updateFriendship(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Friendship friendship
    ) throws URISyntaxException {
        log.debug("REST request to update Friendship : {}, {}", id, friendship);
        if (friendship.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendship.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendshipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Friendship result = friendshipRepository.save(friendship);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendship.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /friendships/:id} : Partial updates given fields of an existing friendship, field will ignore if it is null
     *
     * @param id the id of the friendship to save.
     * @param friendship the friendship to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendship,
     * or with status {@code 400 (Bad Request)} if the friendship is not valid,
     * or with status {@code 404 (Not Found)} if the friendship is not found,
     * or with status {@code 500 (Internal Server Error)} if the friendship couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/friendships/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Friendship> partialUpdateFriendship(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Friendship friendship
    ) throws URISyntaxException {
        log.debug("REST request to partial update Friendship partially : {}, {}", id, friendship);
        if (friendship.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendship.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendshipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Friendship> result = friendshipRepository
            .findById(friendship.getId())
            .map(existingFriendship -> {
                if (friendship.getCreatedAt() != null) {
                    existingFriendship.setCreatedAt(friendship.getCreatedAt());
                }

                return existingFriendship;
            })
            .map(friendshipRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendship.getId().toString())
        );
    }

    /**
     * {@code GET  /friendships} : get all the friendships.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of friendships in body.
     */
    @GetMapping("/friendships")
    public List<Friendship> getAllFriendships() {
        log.debug("REST request to get all Friendships");
        return friendshipRepository.findAll();
    }

    /**
     * {@code GET  /friendships/:id} : get the "id" friendship.
     *
     * @param id the id of the friendship to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the friendship, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/friendships/{id}")
    public ResponseEntity<Friendship> getFriendship(@PathVariable Long id) {
        log.debug("REST request to get Friendship : {}", id);
        Optional<Friendship> friendship = friendshipRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(friendship);
    }

    /**
     * {@code DELETE  /friendships/:id} : delete the "id" friendship.
     *
     * @param id the id of the friendship to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/friendships/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long id) {
        log.debug("REST request to delete Friendship : {}", id);
        friendshipRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
