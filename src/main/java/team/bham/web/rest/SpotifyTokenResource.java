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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.SpotifyToken;
import team.bham.repository.SpotifyTokenRepository;
import team.bham.security.AuthoritiesConstants;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.SpotifyToken}.
 */
@RestController
@RequestMapping("/api/admin")
@Transactional
public class SpotifyTokenResource {

    private final Logger log = LoggerFactory.getLogger(SpotifyTokenResource.class);

    private static final String ENTITY_NAME = "spotifyToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpotifyTokenRepository spotifyTokenRepository;

    public SpotifyTokenResource(SpotifyTokenRepository spotifyTokenRepository) {
        this.spotifyTokenRepository = spotifyTokenRepository;
    }

    /**
     * {@code POST  /spotify-tokens} : Create a new spotifyToken.
     *
     * @param spotifyToken the spotifyToken to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spotifyToken, or with status {@code 400 (Bad Request)} if the spotifyToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/spotify-tokens")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SpotifyToken> createSpotifyToken(@Valid @RequestBody SpotifyToken spotifyToken) throws URISyntaxException {
        log.debug("REST request to save SpotifyToken : {}", spotifyToken);
        if (spotifyToken.getId() != null) {
            throw new BadRequestAlertException("A new spotifyToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SpotifyToken result = spotifyTokenRepository.save(spotifyToken);
        return ResponseEntity
            .created(new URI("/api/spotify-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /spotify-tokens/:id} : Updates an existing spotifyToken.
     *
     * @param id the id of the spotifyToken to save.
     * @param spotifyToken the spotifyToken to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spotifyToken,
     * or with status {@code 400 (Bad Request)} if the spotifyToken is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spotifyToken couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/spotify-tokens/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SpotifyToken> updateSpotifyToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpotifyToken spotifyToken
    ) throws URISyntaxException {
        log.debug("REST request to update SpotifyToken : {}, {}", id, spotifyToken);
        if (spotifyToken.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spotifyToken.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spotifyTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SpotifyToken result = spotifyTokenRepository.save(spotifyToken);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spotifyToken.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /spotify-tokens/:id} : Partial updates given fields of an existing spotifyToken, field will ignore if it is null
     *
     * @param id the id of the spotifyToken to save.
     * @param spotifyToken the spotifyToken to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spotifyToken,
     * or with status {@code 400 (Bad Request)} if the spotifyToken is not valid,
     * or with status {@code 404 (Not Found)} if the spotifyToken is not found,
     * or with status {@code 500 (Internal Server Error)} if the spotifyToken couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/spotify-tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SpotifyToken> partialUpdateSpotifyToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpotifyToken spotifyToken
    ) throws URISyntaxException {
        log.debug("REST request to partial update SpotifyToken partially : {}, {}", id, spotifyToken);
        if (spotifyToken.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spotifyToken.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spotifyTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SpotifyToken> result = spotifyTokenRepository
            .findById(spotifyToken.getId())
            .map(existingSpotifyToken -> {
                if (spotifyToken.getAccessToken() != null) {
                    existingSpotifyToken.setAccessToken(spotifyToken.getAccessToken());
                }
                if (spotifyToken.getTokenType() != null) {
                    existingSpotifyToken.setTokenType(spotifyToken.getTokenType());
                }
                if (spotifyToken.getUserScope() != null) {
                    existingSpotifyToken.setUserScope(spotifyToken.getUserScope());
                }
                if (spotifyToken.getExpires() != null) {
                    existingSpotifyToken.setExpires(spotifyToken.getExpires());
                }
                if (spotifyToken.getRefreshToken() != null) {
                    existingSpotifyToken.setRefreshToken(spotifyToken.getRefreshToken());
                }

                return existingSpotifyToken;
            })
            .map(spotifyTokenRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spotifyToken.getId().toString())
        );
    }

    /**
     * {@code GET  /spotify-tokens} : get all the spotifyTokens.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spotifyTokens in body.
     */
    @GetMapping("/spotify-tokens")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<SpotifyToken> getAllSpotifyTokens(@RequestParam(required = false) String filter) {
        if ("appuser-is-null".equals(filter)) {
            log.debug("REST request to get all SpotifyTokens where appUser is null");
            return StreamSupport
                .stream(spotifyTokenRepository.findAll().spliterator(), false)
                .filter(spotifyToken -> spotifyToken.getAppUser() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all SpotifyTokens");
        return spotifyTokenRepository.findAll();
    }

    /**
     * {@code GET  /spotify-tokens/:id} : get the "id" spotifyToken.
     *
     * @param id the id of the spotifyToken to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spotifyToken, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/spotify-tokens/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SpotifyToken> getSpotifyToken(@PathVariable Long id) {
        log.debug("REST request to get SpotifyToken : {}", id);
        Optional<SpotifyToken> spotifyToken = spotifyTokenRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(spotifyToken);
    }

    /**
     * {@code DELETE  /spotify-tokens/:id} : delete the "id" spotifyToken.
     *
     * @param id the id of the spotifyToken to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/spotify-tokens/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteSpotifyToken(@PathVariable Long id) {
        log.debug("REST request to delete SpotifyToken : {}", id);
        spotifyTokenRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
