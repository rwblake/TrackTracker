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
import team.bham.domain.CardTemplate;
import team.bham.repository.CardTemplateRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.CardTemplate}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CardTemplateResource {

    private final Logger log = LoggerFactory.getLogger(CardTemplateResource.class);

    private static final String ENTITY_NAME = "cardTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CardTemplateRepository cardTemplateRepository;

    public CardTemplateResource(CardTemplateRepository cardTemplateRepository) {
        this.cardTemplateRepository = cardTemplateRepository;
    }

    /**
     * {@code POST  /card-templates} : Create a new cardTemplate.
     *
     * @param cardTemplate the cardTemplate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cardTemplate, or with status {@code 400 (Bad Request)} if the cardTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/card-templates")
    public ResponseEntity<CardTemplate> createCardTemplate(@Valid @RequestBody CardTemplate cardTemplate) throws URISyntaxException {
        log.debug("REST request to save CardTemplate : {}", cardTemplate);
        if (cardTemplate.getId() != null) {
            throw new BadRequestAlertException("A new cardTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CardTemplate result = cardTemplateRepository.save(cardTemplate);
        return ResponseEntity
            .created(new URI("/api/card-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /card-templates/:id} : Updates an existing cardTemplate.
     *
     * @param id the id of the cardTemplate to save.
     * @param cardTemplate the cardTemplate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardTemplate,
     * or with status {@code 400 (Bad Request)} if the cardTemplate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cardTemplate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/card-templates/{id}")
    public ResponseEntity<CardTemplate> updateCardTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CardTemplate cardTemplate
    ) throws URISyntaxException {
        log.debug("REST request to update CardTemplate : {}, {}", id, cardTemplate);
        if (cardTemplate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardTemplate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CardTemplate result = cardTemplateRepository.save(cardTemplate);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cardTemplate.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /card-templates/:id} : Partial updates given fields of an existing cardTemplate, field will ignore if it is null
     *
     * @param id the id of the cardTemplate to save.
     * @param cardTemplate the cardTemplate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardTemplate,
     * or with status {@code 400 (Bad Request)} if the cardTemplate is not valid,
     * or with status {@code 404 (Not Found)} if the cardTemplate is not found,
     * or with status {@code 500 (Internal Server Error)} if the cardTemplate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/card-templates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CardTemplate> partialUpdateCardTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CardTemplate cardTemplate
    ) throws URISyntaxException {
        log.debug("REST request to partial update CardTemplate partially : {}, {}", id, cardTemplate);
        if (cardTemplate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardTemplate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CardTemplate> result = cardTemplateRepository
            .findById(cardTemplate.getId())
            .map(existingCardTemplate -> {
                if (cardTemplate.getColor() != null) {
                    existingCardTemplate.setColor(cardTemplate.getColor());
                }
                if (cardTemplate.getLayout() != null) {
                    existingCardTemplate.setLayout(cardTemplate.getLayout());
                }
                if (cardTemplate.getName() != null) {
                    existingCardTemplate.setName(cardTemplate.getName());
                }
                if (cardTemplate.getFont() != null) {
                    existingCardTemplate.setFont(cardTemplate.getFont());
                }

                return existingCardTemplate;
            })
            .map(cardTemplateRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cardTemplate.getId().toString())
        );
    }

    /**
     * {@code GET  /card-templates} : get all the cardTemplates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cardTemplates in body.
     */
    @GetMapping("/card-templates")
    public List<CardTemplate> getAllCardTemplates() {
        log.debug("REST request to get all CardTemplates");
        return cardTemplateRepository.findAll();
    }

    /**
     * {@code GET  /card-templates/:id} : get the "id" cardTemplate.
     *
     * @param id the id of the cardTemplate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cardTemplate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/card-templates/{id}")
    public ResponseEntity<CardTemplate> getCardTemplate(@PathVariable Long id) {
        log.debug("REST request to get CardTemplate : {}", id);
        Optional<CardTemplate> cardTemplate = cardTemplateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cardTemplate);
    }

    /**
     * {@code DELETE  /card-templates/:id} : delete the "id" cardTemplate.
     *
     * @param id the id of the cardTemplate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/card-templates/{id}")
    public ResponseEntity<Void> deleteCardTemplate(@PathVariable Long id) {
        log.debug("REST request to delete CardTemplate : {}", id);
        cardTemplateRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
