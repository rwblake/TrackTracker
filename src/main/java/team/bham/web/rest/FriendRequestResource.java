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
import team.bham.domain.FriendRequest;
import team.bham.repository.FriendRequestRepository;
import team.bham.service.FriendService;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.FriendRequest}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FriendRequestResource {

    private final Logger log = LoggerFactory.getLogger(FriendRequestResource.class);

    private static final String ENTITY_NAME = "friendRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FriendRequestRepository friendRequestRepository;
    private final FriendService friendService;

    public FriendRequestResource(FriendRequestRepository friendRequestRepository, FriendService friendService) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendService = friendService;
    }

    /**
     * {@code POST  /friend-requests} : Create a new friendRequest.
     *
     * @param friendRequest the friendRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new friendRequest, or with status {@code 400 (Bad Request)} if the friendRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/friend-requests")
    public ResponseEntity<FriendRequest> createFriendRequest(@Valid @RequestBody FriendRequest friendRequest) throws URISyntaxException {
        log.debug("REST request to save FriendRequest : {}", friendRequest);
        if (friendRequest.getId() != null) throw new BadRequestAlertException(
            "A new friendRequest cannot already have an ID",
            ENTITY_NAME,
            "idexists"
        );

        FriendRequest result = friendService.createFriendRequest(friendRequest);

        return ResponseEntity
            .created(new URI("/api/friend-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /friend-requests/:id} : Updates an existing friendRequest.
     *
     * @param id the id of the friendRequest to save.
     * @param friendRequest the friendRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendRequest,
     * or with status {@code 400 (Bad Request)} if the friendRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the friendRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/friend-requests/{id}")
    public ResponseEntity<FriendRequest> updateFriendRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FriendRequest friendRequest
    ) throws URISyntaxException {
        log.debug("REST request to update FriendRequest : {}, {}", id, friendRequest);
        if (friendRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FriendRequest result = friendRequestRepository.save(friendRequest);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendRequest.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /friend-requests/:id} : Partial updates given fields of an existing friendRequest, field will ignore if it is null
     *
     * @param id the id of the friendRequest to save.
     * @param friendRequest the friendRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated friendRequest,
     * or with status {@code 400 (Bad Request)} if the friendRequest is not valid,
     * or with status {@code 404 (Not Found)} if the friendRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the friendRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/friend-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FriendRequest> partialUpdateFriendRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FriendRequest friendRequest
    ) throws URISyntaxException {
        log.debug("REST request to partial update FriendRequest partially : {}, {}", id, friendRequest);
        if (friendRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, friendRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!friendRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FriendRequest> result = friendRequestRepository
            .findById(friendRequest.getId())
            .map(existingFriendRequest -> {
                if (friendRequest.getCreatedAt() != null) {
                    existingFriendRequest.setCreatedAt(friendRequest.getCreatedAt());
                }

                return existingFriendRequest;
            })
            .map(friendRequestRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, friendRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /friend-requests} : get all the friendRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of friendRequests in body.
     */
    @GetMapping("/friend-requests")
    public List<FriendRequest> getAllFriendRequests() {
        log.debug("REST request to get all FriendRequests");
        return friendRequestRepository.findAll();
    }

    /**
     * {@code GET  /friend-requests/:id} : get the "id" friendRequest.
     *
     * @param id the id of the friendRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the friendRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/friend-requests/{id}")
    public ResponseEntity<FriendRequest> getFriendRequest(@PathVariable Long id) {
        log.debug("REST request to get FriendRequest : {}", id);
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(friendRequest);
    }

    /**
     * {@code DELETE  /friend-requests/:id} : delete the "id" friendRequest.
     *
     * @param id the id of the friendRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/friend-requests/{id}")
    public ResponseEntity<Void> deleteFriendRequest(@PathVariable Long id) {
        log.debug("REST request to delete FriendRequest : {}", id);
        friendRequestRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
