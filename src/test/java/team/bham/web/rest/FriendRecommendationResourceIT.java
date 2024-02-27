package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import team.bham.domain.FriendRecommendation;
import team.bham.repository.FriendRecommendationRepository;

/**
 * Integration tests for the {@link FriendRecommendationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FriendRecommendationResourceIT {

    private static final Float DEFAULT_SIMILARITY = 0.0F;
    private static final Float UPDATED_SIMILARITY = 1F;

    private static final LocalDate DEFAULT_CREATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_AT = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/friend-recommendations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FriendRecommendationRepository friendRecommendationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFriendRecommendationMockMvc;

    private FriendRecommendation friendRecommendation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FriendRecommendation createEntity(EntityManager em) {
        FriendRecommendation friendRecommendation = new FriendRecommendation().similarity(DEFAULT_SIMILARITY).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        friendRecommendation.setAboutAppUser(appUser);
        return friendRecommendation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FriendRecommendation createUpdatedEntity(EntityManager em) {
        FriendRecommendation friendRecommendation = new FriendRecommendation().similarity(UPDATED_SIMILARITY).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createUpdatedEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        friendRecommendation.setAboutAppUser(appUser);
        return friendRecommendation;
    }

    @BeforeEach
    public void initTest() {
        friendRecommendation = createEntity(em);
    }

    @Test
    @Transactional
    void createFriendRecommendation() throws Exception {
        int databaseSizeBeforeCreate = friendRecommendationRepository.findAll().size();
        // Create the FriendRecommendation
        restFriendRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isCreated());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeCreate + 1);
        FriendRecommendation testFriendRecommendation = friendRecommendationList.get(friendRecommendationList.size() - 1);
        assertThat(testFriendRecommendation.getSimilarity()).isEqualTo(DEFAULT_SIMILARITY);
        assertThat(testFriendRecommendation.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createFriendRecommendationWithExistingId() throws Exception {
        // Create the FriendRecommendation with an existing ID
        friendRecommendation.setId(1L);

        int databaseSizeBeforeCreate = friendRecommendationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFriendRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSimilarityIsRequired() throws Exception {
        int databaseSizeBeforeTest = friendRecommendationRepository.findAll().size();
        // set the field null
        friendRecommendation.setSimilarity(null);

        // Create the FriendRecommendation, which fails.

        restFriendRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = friendRecommendationRepository.findAll().size();
        // set the field null
        friendRecommendation.setCreatedAt(null);

        // Create the FriendRecommendation, which fails.

        restFriendRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFriendRecommendations() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        // Get all the friendRecommendationList
        restFriendRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(friendRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].similarity").value(hasItem(DEFAULT_SIMILARITY.doubleValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFriendRecommendation() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        // Get the friendRecommendation
        restFriendRecommendationMockMvc
            .perform(get(ENTITY_API_URL_ID, friendRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(friendRecommendation.getId().intValue()))
            .andExpect(jsonPath("$.similarity").value(DEFAULT_SIMILARITY.doubleValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFriendRecommendation() throws Exception {
        // Get the friendRecommendation
        restFriendRecommendationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFriendRecommendation() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();

        // Update the friendRecommendation
        FriendRecommendation updatedFriendRecommendation = friendRecommendationRepository.findById(friendRecommendation.getId()).get();
        // Disconnect from session so that the updates on updatedFriendRecommendation are not directly saved in db
        em.detach(updatedFriendRecommendation);
        updatedFriendRecommendation.similarity(UPDATED_SIMILARITY).createdAt(UPDATED_CREATED_AT);

        restFriendRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFriendRecommendation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFriendRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
        FriendRecommendation testFriendRecommendation = friendRecommendationList.get(friendRecommendationList.size() - 1);
        assertThat(testFriendRecommendation.getSimilarity()).isEqualTo(UPDATED_SIMILARITY);
        assertThat(testFriendRecommendation.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, friendRecommendation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFriendRecommendationWithPatch() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();

        // Update the friendRecommendation using partial update
        FriendRecommendation partialUpdatedFriendRecommendation = new FriendRecommendation();
        partialUpdatedFriendRecommendation.setId(friendRecommendation.getId());

        partialUpdatedFriendRecommendation.similarity(UPDATED_SIMILARITY).createdAt(UPDATED_CREATED_AT);

        restFriendRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFriendRecommendation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFriendRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
        FriendRecommendation testFriendRecommendation = friendRecommendationList.get(friendRecommendationList.size() - 1);
        assertThat(testFriendRecommendation.getSimilarity()).isEqualTo(UPDATED_SIMILARITY);
        assertThat(testFriendRecommendation.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateFriendRecommendationWithPatch() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();

        // Update the friendRecommendation using partial update
        FriendRecommendation partialUpdatedFriendRecommendation = new FriendRecommendation();
        partialUpdatedFriendRecommendation.setId(friendRecommendation.getId());

        partialUpdatedFriendRecommendation.similarity(UPDATED_SIMILARITY).createdAt(UPDATED_CREATED_AT);

        restFriendRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFriendRecommendation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFriendRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
        FriendRecommendation testFriendRecommendation = friendRecommendationList.get(friendRecommendationList.size() - 1);
        assertThat(testFriendRecommendation.getSimilarity()).isEqualTo(UPDATED_SIMILARITY);
        assertThat(testFriendRecommendation.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, friendRecommendation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isBadRequest());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFriendRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = friendRecommendationRepository.findAll().size();
        friendRecommendation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFriendRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(friendRecommendation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FriendRecommendation in the database
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFriendRecommendation() throws Exception {
        // Initialize the database
        friendRecommendationRepository.saveAndFlush(friendRecommendation);

        int databaseSizeBeforeDelete = friendRecommendationRepository.findAll().size();

        // Delete the friendRecommendation
        restFriendRecommendationMockMvc
            .perform(delete(ENTITY_API_URL_ID, friendRecommendation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FriendRecommendation> friendRecommendationList = friendRecommendationRepository.findAll();
        assertThat(friendRecommendationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
