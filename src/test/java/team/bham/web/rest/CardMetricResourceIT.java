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
import team.bham.domain.CardMetric;
import team.bham.domain.CardTemplate;
import team.bham.domain.enumeration.CardType;
import team.bham.repository.CardMetricRepository;

/**
 * Integration tests for the {@link CardMetricResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CardMetricResourceIT {

    private static final CardType DEFAULT_METRIC = CardType.LISTENING_DURATION;
    private static final CardType UPDATED_METRIC = CardType.TOP_GENRE;

    private static final String ENTITY_API_URL = "/api/card-metrics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CardMetricRepository cardMetricRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCardMetricMockMvc;

    private CardMetric cardMetric;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardMetric createEntity(EntityManager em) {
        CardMetric cardMetric = new CardMetric().metric(DEFAULT_METRIC);
        // Add required entity
        CardTemplate cardTemplate;
        if (TestUtil.findAll(em, CardTemplate.class).isEmpty()) {
            cardTemplate = CardTemplateResourceIT.createEntity(em);
            em.persist(cardTemplate);
            em.flush();
        } else {
            cardTemplate = TestUtil.findAll(em, CardTemplate.class).get(0);
        }
        cardMetric.setCardTemplate(cardTemplate);
        return cardMetric;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardMetric createUpdatedEntity(EntityManager em) {
        CardMetric cardMetric = new CardMetric().metric(UPDATED_METRIC);
        // Add required entity
        CardTemplate cardTemplate;
        if (TestUtil.findAll(em, CardTemplate.class).isEmpty()) {
            cardTemplate = CardTemplateResourceIT.createUpdatedEntity(em);
            em.persist(cardTemplate);
            em.flush();
        } else {
            cardTemplate = TestUtil.findAll(em, CardTemplate.class).get(0);
        }
        cardMetric.setCardTemplate(cardTemplate);
        return cardMetric;
    }

    @BeforeEach
    public void initTest() {
        cardMetric = createEntity(em);
    }

    @Test
    @Transactional
    void createCardMetric() throws Exception {
        int databaseSizeBeforeCreate = cardMetricRepository.findAll().size();
        // Create the CardMetric
        restCardMetricMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardMetric)))
            .andExpect(status().isCreated());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeCreate + 1);
        CardMetric testCardMetric = cardMetricList.get(cardMetricList.size() - 1);
        assertThat(testCardMetric.getMetric()).isEqualTo(DEFAULT_METRIC);
    }

    @Test
    @Transactional
    void createCardMetricWithExistingId() throws Exception {
        // Create the CardMetric with an existing ID
        cardMetric.setId(1L);

        int databaseSizeBeforeCreate = cardMetricRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardMetricMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardMetric)))
            .andExpect(status().isBadRequest());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMetricIsRequired() throws Exception {
        int databaseSizeBeforeTest = cardMetricRepository.findAll().size();
        // set the field null
        cardMetric.setMetric(null);

        // Create the CardMetric, which fails.

        restCardMetricMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardMetric)))
            .andExpect(status().isBadRequest());

        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCardMetrics() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        // Get all the cardMetricList
        restCardMetricMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cardMetric.getId().intValue())))
            .andExpect(jsonPath("$.[*].metric").value(hasItem(DEFAULT_METRIC.toString())));
    }

    @Test
    @Transactional
    void getCardMetric() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        // Get the cardMetric
        restCardMetricMockMvc
            .perform(get(ENTITY_API_URL_ID, cardMetric.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cardMetric.getId().intValue()))
            .andExpect(jsonPath("$.metric").value(DEFAULT_METRIC.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCardMetric() throws Exception {
        // Get the cardMetric
        restCardMetricMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCardMetric() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();

        // Update the cardMetric
        CardMetric updatedCardMetric = cardMetricRepository.findById(cardMetric.getId()).get();
        // Disconnect from session so that the updates on updatedCardMetric are not directly saved in db
        em.detach(updatedCardMetric);
        updatedCardMetric.metric(UPDATED_METRIC);

        restCardMetricMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCardMetric.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCardMetric))
            )
            .andExpect(status().isOk());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
        CardMetric testCardMetric = cardMetricList.get(cardMetricList.size() - 1);
        assertThat(testCardMetric.getMetric()).isEqualTo(UPDATED_METRIC);
    }

    @Test
    @Transactional
    void putNonExistingCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cardMetric.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cardMetric))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cardMetric))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cardMetric)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCardMetricWithPatch() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();

        // Update the cardMetric using partial update
        CardMetric partialUpdatedCardMetric = new CardMetric();
        partialUpdatedCardMetric.setId(cardMetric.getId());

        restCardMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardMetric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCardMetric))
            )
            .andExpect(status().isOk());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
        CardMetric testCardMetric = cardMetricList.get(cardMetricList.size() - 1);
        assertThat(testCardMetric.getMetric()).isEqualTo(DEFAULT_METRIC);
    }

    @Test
    @Transactional
    void fullUpdateCardMetricWithPatch() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();

        // Update the cardMetric using partial update
        CardMetric partialUpdatedCardMetric = new CardMetric();
        partialUpdatedCardMetric.setId(cardMetric.getId());

        partialUpdatedCardMetric.metric(UPDATED_METRIC);

        restCardMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardMetric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCardMetric))
            )
            .andExpect(status().isOk());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
        CardMetric testCardMetric = cardMetricList.get(cardMetricList.size() - 1);
        assertThat(testCardMetric.getMetric()).isEqualTo(UPDATED_METRIC);
    }

    @Test
    @Transactional
    void patchNonExistingCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cardMetric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cardMetric))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cardMetric))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCardMetric() throws Exception {
        int databaseSizeBeforeUpdate = cardMetricRepository.findAll().size();
        cardMetric.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMetricMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cardMetric))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardMetric in the database
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCardMetric() throws Exception {
        // Initialize the database
        cardMetricRepository.saveAndFlush(cardMetric);

        int databaseSizeBeforeDelete = cardMetricRepository.findAll().size();

        // Delete the cardMetric
        restCardMetricMockMvc
            .perform(delete(ENTITY_API_URL_ID, cardMetric.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CardMetric> cardMetricList = cardMetricRepository.findAll();
        assertThat(cardMetricList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
