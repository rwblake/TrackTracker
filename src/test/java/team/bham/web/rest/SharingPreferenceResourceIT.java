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
import team.bham.domain.SharingPreference;
import team.bham.domain.UserPreferences;
import team.bham.domain.enumeration.CardType;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.SharingPreferenceRepository;

/**
 * Integration tests for the {@link SharingPreferenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SharingPreferenceResourceIT {

    private static final CardType DEFAULT_METRIC = CardType.LISTENING_DURATION;
    private static final CardType UPDATED_METRIC = CardType.GENRE;

    private static final VisibilityPreference DEFAULT_VISIBILITY = VisibilityPreference.GLOBAL;
    private static final VisibilityPreference UPDATED_VISIBILITY = VisibilityPreference.FRIENDS_OF_FRIENDS;

    private static final String ENTITY_API_URL = "/api/sharing-preferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SharingPreferenceRepository sharingPreferenceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSharingPreferenceMockMvc;

    private SharingPreference sharingPreference;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SharingPreference createEntity(EntityManager em) {
        SharingPreference sharingPreference = new SharingPreference().metric(DEFAULT_METRIC).visibility(DEFAULT_VISIBILITY);
        // Add required entity
        UserPreferences userPreferences;
        if (TestUtil.findAll(em, UserPreferences.class).isEmpty()) {
            userPreferences = UserPreferencesResourceIT.createEntity(em);
            em.persist(userPreferences);
            em.flush();
        } else {
            userPreferences = TestUtil.findAll(em, UserPreferences.class).get(0);
        }
        sharingPreference.setAppUser(userPreferences);
        return sharingPreference;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SharingPreference createUpdatedEntity(EntityManager em) {
        SharingPreference sharingPreference = new SharingPreference().metric(UPDATED_METRIC).visibility(UPDATED_VISIBILITY);
        // Add required entity
        UserPreferences userPreferences;
        if (TestUtil.findAll(em, UserPreferences.class).isEmpty()) {
            userPreferences = UserPreferencesResourceIT.createUpdatedEntity(em);
            em.persist(userPreferences);
            em.flush();
        } else {
            userPreferences = TestUtil.findAll(em, UserPreferences.class).get(0);
        }
        sharingPreference.setAppUser(userPreferences);
        return sharingPreference;
    }

    @BeforeEach
    public void initTest() {
        sharingPreference = createEntity(em);
    }

    @Test
    @Transactional
    void createSharingPreference() throws Exception {
        int databaseSizeBeforeCreate = sharingPreferenceRepository.findAll().size();
        // Create the SharingPreference
        restSharingPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isCreated());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeCreate + 1);
        SharingPreference testSharingPreference = sharingPreferenceList.get(sharingPreferenceList.size() - 1);
        assertThat(testSharingPreference.getMetric()).isEqualTo(DEFAULT_METRIC);
        assertThat(testSharingPreference.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
    }

    @Test
    @Transactional
    void createSharingPreferenceWithExistingId() throws Exception {
        // Create the SharingPreference with an existing ID
        sharingPreference.setId(1L);

        int databaseSizeBeforeCreate = sharingPreferenceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSharingPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isBadRequest());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSharingPreferences() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        // Get all the sharingPreferenceList
        restSharingPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sharingPreference.getId().intValue())))
            .andExpect(jsonPath("$.[*].metric").value(hasItem(DEFAULT_METRIC.toString())))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }

    @Test
    @Transactional
    void getSharingPreference() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        // Get the sharingPreference
        restSharingPreferenceMockMvc
            .perform(get(ENTITY_API_URL_ID, sharingPreference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sharingPreference.getId().intValue()))
            .andExpect(jsonPath("$.metric").value(DEFAULT_METRIC.toString()))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSharingPreference() throws Exception {
        // Get the sharingPreference
        restSharingPreferenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSharingPreference() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();

        // Update the sharingPreference
        SharingPreference updatedSharingPreference = sharingPreferenceRepository.findById(sharingPreference.getId()).get();
        // Disconnect from session so that the updates on updatedSharingPreference are not directly saved in db
        em.detach(updatedSharingPreference);
        updatedSharingPreference.metric(UPDATED_METRIC).visibility(UPDATED_VISIBILITY);

        restSharingPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSharingPreference.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSharingPreference))
            )
            .andExpect(status().isOk());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
        SharingPreference testSharingPreference = sharingPreferenceList.get(sharingPreferenceList.size() - 1);
        assertThat(testSharingPreference.getMetric()).isEqualTo(UPDATED_METRIC);
        assertThat(testSharingPreference.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void putNonExistingSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sharingPreference.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isBadRequest());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isBadRequest());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSharingPreferenceWithPatch() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();

        // Update the sharingPreference using partial update
        SharingPreference partialUpdatedSharingPreference = new SharingPreference();
        partialUpdatedSharingPreference.setId(sharingPreference.getId());

        restSharingPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSharingPreference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSharingPreference))
            )
            .andExpect(status().isOk());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
        SharingPreference testSharingPreference = sharingPreferenceList.get(sharingPreferenceList.size() - 1);
        assertThat(testSharingPreference.getMetric()).isEqualTo(DEFAULT_METRIC);
        assertThat(testSharingPreference.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
    }

    @Test
    @Transactional
    void fullUpdateSharingPreferenceWithPatch() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();

        // Update the sharingPreference using partial update
        SharingPreference partialUpdatedSharingPreference = new SharingPreference();
        partialUpdatedSharingPreference.setId(sharingPreference.getId());

        partialUpdatedSharingPreference.metric(UPDATED_METRIC).visibility(UPDATED_VISIBILITY);

        restSharingPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSharingPreference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSharingPreference))
            )
            .andExpect(status().isOk());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
        SharingPreference testSharingPreference = sharingPreferenceList.get(sharingPreferenceList.size() - 1);
        assertThat(testSharingPreference.getMetric()).isEqualTo(UPDATED_METRIC);
        assertThat(testSharingPreference.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void patchNonExistingSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sharingPreference.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isBadRequest());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isBadRequest());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSharingPreference() throws Exception {
        int databaseSizeBeforeUpdate = sharingPreferenceRepository.findAll().size();
        sharingPreference.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSharingPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sharingPreference))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SharingPreference in the database
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSharingPreference() throws Exception {
        // Initialize the database
        sharingPreferenceRepository.saveAndFlush(sharingPreference);

        int databaseSizeBeforeDelete = sharingPreferenceRepository.findAll().size();

        // Delete the sharingPreference
        restSharingPreferenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, sharingPreference.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SharingPreference> sharingPreferenceList = sharingPreferenceRepository.findAll();
        assertThat(sharingPreferenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
