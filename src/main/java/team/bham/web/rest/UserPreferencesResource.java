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
import team.bham.domain.UserPreferences;
import team.bham.repository.UserPreferencesRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.UserPreferences}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserPreferencesResource {

    private final Logger log = LoggerFactory.getLogger(UserPreferencesResource.class);

    private static final String ENTITY_NAME = "userPreferences";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferencesResource(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    /**
     * {@code POST  /user-preferences} : Create a new userPreferences.
     *
     * @param userPreferences the userPreferences to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPreferences, or with status {@code 400 (Bad Request)} if the userPreferences has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-preferences")
    public ResponseEntity<UserPreferences> createUserPreferences(@Valid @RequestBody UserPreferences userPreferences)
        throws URISyntaxException {
        log.debug("REST request to save UserPreferences : {}", userPreferences);
        if (userPreferences.getId() != null) {
            throw new BadRequestAlertException("A new userPreferences cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserPreferences result = userPreferencesRepository.save(userPreferences);
        return ResponseEntity
            .created(new URI("/api/user-preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-preferences/:id} : Updates an existing userPreferences.
     *
     * @param id the id of the userPreferences to save.
     * @param userPreferences the userPreferences to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPreferences,
     * or with status {@code 400 (Bad Request)} if the userPreferences is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPreferences couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-preferences/{id}")
    public ResponseEntity<UserPreferences> updateUserPreferences(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserPreferences userPreferences
    ) throws URISyntaxException {
        log.debug("REST request to update UserPreferences : {}, {}", id, userPreferences);
        if (userPreferences.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPreferences.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPreferencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserPreferences result = userPreferencesRepository.save(userPreferences);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPreferences.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-preferences/:id} : Partial updates given fields of an existing userPreferences, field will ignore if it is null
     *
     * @param id the id of the userPreferences to save.
     * @param userPreferences the userPreferences to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPreferences,
     * or with status {@code 400 (Bad Request)} if the userPreferences is not valid,
     * or with status {@code 404 (Not Found)} if the userPreferences is not found,
     * or with status {@code 500 (Internal Server Error)} if the userPreferences couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-preferences/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserPreferences> partialUpdateUserPreferences(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserPreferences userPreferences
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserPreferences partially : {}, {}", id, userPreferences);
        if (userPreferences.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPreferences.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPreferencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserPreferences> result = userPreferencesRepository
            .findById(userPreferences.getId())
            .map(existingUserPreferences -> {
                if (userPreferences.getVisibility() != null) {
                    existingUserPreferences.setVisibility(userPreferences.getVisibility());
                }
                if (userPreferences.getIsDarkMode() != null) {
                    existingUserPreferences.setIsDarkMode(userPreferences.getIsDarkMode());
                }
                if (userPreferences.getIsHighContrast() != null) {
                    existingUserPreferences.setIsHighContrast(userPreferences.getIsHighContrast());
                }
                if (userPreferences.getPlaylistPrivacy() != null) {
                    existingUserPreferences.setPlaylistPrivacy(userPreferences.getPlaylistPrivacy());
                }

                return existingUserPreferences;
            })
            .map(userPreferencesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPreferences.getId().toString())
        );
    }

    /**
     * {@code GET  /user-preferences} : get all the userPreferences.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userPreferences in body.
     */
    @GetMapping("/user-preferences")
    public List<UserPreferences> getAllUserPreferences(@RequestParam(required = false) String filter) {
        if ("appuser-is-null".equals(filter)) {
            log.debug("REST request to get all UserPreferencess where appUser is null");
            return StreamSupport
                .stream(userPreferencesRepository.findAll().spliterator(), false)
                .filter(userPreferences -> userPreferences.getAppUser() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all UserPreferences");
        return userPreferencesRepository.findAll();
    }

    /**
     * {@code GET  /user-preferences/:id} : get the "id" userPreferences.
     *
     * @param id the id of the userPreferences to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userPreferences, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-preferences/{id}")
    public ResponseEntity<UserPreferences> getUserPreferences(@PathVariable Long id) {
        log.debug("REST request to get UserPreferences : {}", id);
        Optional<UserPreferences> userPreferences = userPreferencesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userPreferences);
    }

    /**
     * {@code DELETE  /user-preferences/:id} : delete the "id" userPreferences.
     *
     * @param id the id of the userPreferences to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-preferences/{id}")
    public ResponseEntity<Void> deleteUserPreferences(@PathVariable Long id) {
        log.debug("REST request to delete UserPreferences : {}", id);
        userPreferencesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
