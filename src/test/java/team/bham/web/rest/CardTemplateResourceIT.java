package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import team.bham.IntegrationTest;
import team.bham.domain.AppUser;
import team.bham.domain.CardTemplate;
import team.bham.domain.enumeration.Color;
import team.bham.domain.enumeration.Font;
import team.bham.domain.enumeration.Layout;
import team.bham.repository.CardTemplateRepository;

/**
 * Integration tests for the {@link CardTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CardTemplateResourceIT {

    private static final Color DEFAULT_COLOR = Color.OPTION1;
    private static final Color UPDATED_COLOR = Color.OPTION1;

    private static final Layout DEFAULT_LAYOUT = Layout.OPTION1;
    private static final Layout UPDATED_LAYOUT = Layout.OPTION1;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Font DEFAULT_FONT = Font.OPTION1;
    private static final Font UPDATED_FONT = Font.OPTION1;

    private static final String ENTITY_API_URL = "/api/card-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CardTemplateRepository cardTemplateRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCardTemplateMockMvc;

    private CardTemplate cardTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardTemplate createEntity(EntityManager em) {
        CardTemplate cardTemplate = new CardTemplate().color(DEFAULT_COLOR).layout(DEFAULT_LAYOUT).name(DEFAULT_NAME).font(DEFAULT_FONT);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        cardTemplate.setAppUser(appUser);
        return cardTemplate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardTemplate createUpdatedEntity(EntityManager em) {
        CardTemplate cardTemplate = new CardTemplate().color(UPDATED_COLOR).layout(UPDATED_LAYOUT).name(UPDATED_NAME).font(UPDATED_FONT);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createUpdatedEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        cardTemplate.setAppUser(appUser);
        return cardTemplate;
    }

    @BeforeEach
    public void initTest() {
        cardTemplate = createEntity(em);
    }

    @Test
    @Transactional
    void createCardTemplate() throws Exception {
        int databaseSizeBeforeCreate = cardTemplateRepository.findAll().size();
        // Create the CardTemplate
        restCardTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardTemplate)))
            .andExpect(status().isCreated());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        CardTemplate testCardTemplate = cardTemplateList.get(cardTemplateList.size() - 1);
        assertThat(testCardTemplate.getColor()).isEqualTo(DEFAULT_COLOR);
        assertThat(testCardTemplate.getLayout()).isEqualTo(DEFAULT_LAYOUT);
        assertThat(testCardTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCardTemplate.getFont()).isEqualTo(DEFAULT_FONT);
    }

    @Test
    @Transactional
    void createCardTemplateWithExistingId() throws Exception {
        // Create the CardTemplate with an existing ID
        cardTemplate.setId(1L);

        int databaseSizeBeforeCreate = cardTemplateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardTemplate)))
            .andExpect(status().isBadRequest());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCardTemplates() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        // Get all the cardTemplateList
        restCardTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cardTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].font").value(hasItem(DEFAULT_FONT.toString())));
    }

    @Test
    @Transactional
    void getCardTemplate() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        // Get the cardTemplate
        restCardTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, cardTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cardTemplate.getId().intValue()))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR.toString()))
            .andExpect(jsonPath("$.layout").value(DEFAULT_LAYOUT.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.font").value(DEFAULT_FONT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCardTemplate() throws Exception {
        // Get the cardTemplate
        restCardTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCardTemplate() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();

        // Update the cardTemplate
        CardTemplate updatedCardTemplate = cardTemplateRepository.findById(cardTemplate.getId()).get();
        // Disconnect from session so that the updates on updatedCardTemplate are not directly saved in db
        em.detach(updatedCardTemplate);
        updatedCardTemplate.color(UPDATED_COLOR).layout(UPDATED_LAYOUT).name(UPDATED_NAME).font(UPDATED_FONT);

        restCardTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCardTemplate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCardTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
        CardTemplate testCardTemplate = cardTemplateList.get(cardTemplateList.size() - 1);
        assertThat(testCardTemplate.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testCardTemplate.getLayout()).isEqualTo(UPDATED_LAYOUT);
        assertThat(testCardTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCardTemplate.getFont()).isEqualTo(UPDATED_FONT);
    }

    @Test
    @Transactional
    void putNonExistingCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cardTemplate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cardTemplate))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cardTemplate))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardTemplate)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCardTemplateWithPatch() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();

        // Update the cardTemplate using partial update
        CardTemplate partialUpdatedCardTemplate = new CardTemplate();
        partialUpdatedCardTemplate.setId(cardTemplate.getId());

        partialUpdatedCardTemplate.color(UPDATED_COLOR).font(UPDATED_FONT);

        restCardTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCardTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
        CardTemplate testCardTemplate = cardTemplateList.get(cardTemplateList.size() - 1);
        assertThat(testCardTemplate.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testCardTemplate.getLayout()).isEqualTo(DEFAULT_LAYOUT);
        assertThat(testCardTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCardTemplate.getFont()).isEqualTo(UPDATED_FONT);
    }

    @Test
    @Transactional
    void fullUpdateCardTemplateWithPatch() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();

        // Update the cardTemplate using partial update
        CardTemplate partialUpdatedCardTemplate = new CardTemplate();
        partialUpdatedCardTemplate.setId(cardTemplate.getId());

        partialUpdatedCardTemplate.color(UPDATED_COLOR).layout(UPDATED_LAYOUT).name(UPDATED_NAME).font(UPDATED_FONT);

        restCardTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCardTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
        CardTemplate testCardTemplate = cardTemplateList.get(cardTemplateList.size() - 1);
        assertThat(testCardTemplate.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testCardTemplate.getLayout()).isEqualTo(UPDATED_LAYOUT);
        assertThat(testCardTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCardTemplate.getFont()).isEqualTo(UPDATED_FONT);
    }

    @Test
    @Transactional
    void patchNonExistingCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cardTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cardTemplate))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cardTemplate))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCardTemplate() throws Exception {
        int databaseSizeBeforeUpdate = cardTemplateRepository.findAll().size();
        cardTemplate.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cardTemplate))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardTemplate in the database
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCardTemplate() throws Exception {
        // Initialize the database
        cardTemplateRepository.saveAndFlush(cardTemplate);

        int databaseSizeBeforeDelete = cardTemplateRepository.findAll().size();

        // Delete the cardTemplate
        restCardTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, cardTemplate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CardTemplate> cardTemplateList = cardTemplateRepository.findAll();
        assertThat(cardTemplateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
