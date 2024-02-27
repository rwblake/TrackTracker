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
import team.bham.domain.SharingPreference;
import team.bham.repository.SharingPreferenceRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.SharingPreference}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SharingPreferenceResource {

    private final Logger log = LoggerFactory.getLogger(SharingPreferenceResource.class);

    private static final String ENTITY_NAME = "sharingPreference";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SharingPreferenceRepository sharingPreferenceRepository;

    public SharingPreferenceResource(SharingPreferenceRepository sharingPreferenceRepository) {
        this.sharingPreferenceRepository = sharingPreferenceRepository;
    }

    /**
     * {@code POST  /sharing-preferences} : Create a new sharingPreference.
     *
     * @param sharingPreference the sharingPreference to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sharingPreference, or with status {@code 400 (Bad Request)} if the sharingPreference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sharing-preferences")
    public ResponseEntity<SharingPreference> createSharingPreference(@Valid @RequestBody SharingPreference sharingPreference)
        throws URISyntaxException {
        log.debug("REST request to save SharingPreference : {}", sharingPreference);
        if (sharingPreference.getId() != null) {
            throw new BadRequestAlertException("A new sharingPreference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SharingPreference result = sharingPreferenceRepository.save(sharingPreference);
        return ResponseEntity
            .created(new URI("/api/sharing-preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sharing-preferences/:id} : Updates an existing sharingPreference.
     *
     * @param id the id of the sharingPreference to save.
     * @param sharingPreference the sharingPreference to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sharingPreference,
     * or with status {@code 400 (Bad Request)} if the sharingPreference is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sharingPreference couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sharing-preferences/{id}")
    public ResponseEntity<SharingPreference> updateSharingPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SharingPreference sharingPreference
    ) throws URISyntaxException {
        log.debug("REST request to update SharingPreference : {}, {}", id, sharingPreference);
        if (sharingPreference.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sharingPreference.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sharingPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SharingPreference result = sharingPreferenceRepository.save(sharingPreference);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sharingPreference.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sharing-preferences/:id} : Partial updates given fields of an existing sharingPreference, field will ignore if it is null
     *
     * @param id the id of the sharingPreference to save.
     * @param sharingPreference the sharingPreference to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sharingPreference,
     * or with status {@code 400 (Bad Request)} if the sharingPreference is not valid,
     * or with status {@code 404 (Not Found)} if the sharingPreference is not found,
     * or with status {@code 500 (Internal Server Error)} if the sharingPreference couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sharing-preferences/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SharingPreference> partialUpdateSharingPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SharingPreference sharingPreference
    ) throws URISyntaxException {
        log.debug("REST request to partial update SharingPreference partially : {}, {}", id, sharingPreference);
        if (sharingPreference.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sharingPreference.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sharingPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SharingPreference> result = sharingPreferenceRepository
            .findById(sharingPreference.getId())
            .map(existingSharingPreference -> {
                if (sharingPreference.getMetric() != null) {
                    existingSharingPreference.setMetric(sharingPreference.getMetric());
                }
                if (sharingPreference.getVisibility() != null) {
                    existingSharingPreference.setVisibility(sharingPreference.getVisibility());
                }

                return existingSharingPreference;
            })
            .map(sharingPreferenceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sharingPreference.getId().toString())
        );
    }

    /**
     * {@code GET  /sharing-preferences} : get all the sharingPreferences.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sharingPreferences in body.
     */
    @GetMapping("/sharing-preferences")
    public List<SharingPreference> getAllSharingPreferences() {
        log.debug("REST request to get all SharingPreferences");
        return sharingPreferenceRepository.findAll();
    }

    /**
     * {@code GET  /sharing-preferences/:id} : get the "id" sharingPreference.
     *
     * @param id the id of the sharingPreference to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sharingPreference, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sharing-preferences/{id}")
    public ResponseEntity<SharingPreference> getSharingPreference(@PathVariable Long id) {
        log.debug("REST request to get SharingPreference : {}", id);
        Optional<SharingPreference> sharingPreference = sharingPreferenceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sharingPreference);
    }

    /**
     * {@code DELETE  /sharing-preferences/:id} : delete the "id" sharingPreference.
     *
     * @param id the id of the sharingPreference to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sharing-preferences/{id}")
    public ResponseEntity<Void> deleteSharingPreference(@PathVariable Long id) {
        log.debug("REST request to delete SharingPreference : {}", id);
        sharingPreferenceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
