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
import team.bham.domain.UserPreferences;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.domain.enumeration.VisibilityPreference;
import team.bham.repository.UserPreferencesRepository;

/**
 * Integration tests for the {@link UserPreferencesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserPreferencesResourceIT {

    private static final VisibilityPreference DEFAULT_VISIBILITY = VisibilityPreference.GLOBAL;
    private static final VisibilityPreference UPDATED_VISIBILITY = VisibilityPreference.FRIENDS_OF_FRIENDS;

    private static final Boolean DEFAULT_IS_DARK_MODE = false;
    private static final Boolean UPDATED_IS_DARK_MODE = true;

    private static final Boolean DEFAULT_IS_HIGH_CONTRAST = false;
    private static final Boolean UPDATED_IS_HIGH_CONTRAST = true;

    private static final VisibilityPreference DEFAULT_PLAYLIST_PRIVACY = VisibilityPreference.GLOBAL;
    private static final VisibilityPreference UPDATED_PLAYLIST_PRIVACY = VisibilityPreference.FRIENDS_OF_FRIENDS;

    private static final String ENTITY_API_URL = "/api/user-preferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserPreferencesMockMvc;

    private UserPreferences userPreferences;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPreferences createEntity(EntityManager em) {
        UserPreferences userPreferences = new UserPreferences()
            .visibility(DEFAULT_VISIBILITY)
            .isDarkMode(DEFAULT_IS_DARK_MODE)
            .isHighContrast(DEFAULT_IS_HIGH_CONTRAST)
            .playlistPrivacy(DEFAULT_PLAYLIST_PRIVACY);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        userPreferences.setAppUser(appUser);
        return userPreferences;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPreferences createUpdatedEntity(EntityManager em) {
        UserPreferences userPreferences = new UserPreferences()
            .visibility(UPDATED_VISIBILITY)
            .isDarkMode(UPDATED_IS_DARK_MODE)
            .isHighContrast(UPDATED_IS_HIGH_CONTRAST)
            .playlistPrivacy(UPDATED_PLAYLIST_PRIVACY);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createUpdatedEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        userPreferences.setAppUser(appUser);
        return userPreferences;
    }

    @BeforeEach
    public void initTest() {
        userPreferences = createEntity(em);
    }

    @Test
    @Transactional
    void createUserPreferences() throws Exception {
        int databaseSizeBeforeCreate = userPreferencesRepository.findAll().size();
        // Create the UserPreferences
        restUserPreferencesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isCreated());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeCreate + 1);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
        assertThat(testUserPreferences.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testUserPreferences.getIsDarkMode()).isEqualTo(DEFAULT_IS_DARK_MODE);
        assertThat(testUserPreferences.getIsHighContrast()).isEqualTo(DEFAULT_IS_HIGH_CONTRAST);
        assertThat(testUserPreferences.getPlaylistPrivacy()).isEqualTo(DEFAULT_PLAYLIST_PRIVACY);
    }

    @Test
    @Transactional
    void createUserPreferencesWithExistingId() throws Exception {
        // Create the UserPreferences with an existing ID
        userPreferences.setId(1L);

        int databaseSizeBeforeCreate = userPreferencesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPreferencesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        // Get all the userPreferencesList
        restUserPreferencesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPreferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].isDarkMode").value(hasItem(DEFAULT_IS_DARK_MODE.booleanValue())))
            .andExpect(jsonPath("$.[*].isHighContrast").value(hasItem(DEFAULT_IS_HIGH_CONTRAST.booleanValue())))
            .andExpect(jsonPath("$.[*].playlistPrivacy").value(hasItem(DEFAULT_PLAYLIST_PRIVACY.toString())));
    }

    @Test
    @Transactional
    void getUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        // Get the userPreferences
        restUserPreferencesMockMvc
            .perform(get(ENTITY_API_URL_ID, userPreferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userPreferences.getId().intValue()))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()))
            .andExpect(jsonPath("$.isDarkMode").value(DEFAULT_IS_DARK_MODE.booleanValue()))
            .andExpect(jsonPath("$.isHighContrast").value(DEFAULT_IS_HIGH_CONTRAST.booleanValue()))
            .andExpect(jsonPath("$.playlistPrivacy").value(DEFAULT_PLAYLIST_PRIVACY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserPreferences() throws Exception {
        // Get the userPreferences
        restUserPreferencesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();

        // Update the userPreferences
        UserPreferences updatedUserPreferences = userPreferencesRepository.findById(userPreferences.getId()).get();
        // Disconnect from session so that the updates on updatedUserPreferences are not directly saved in db
        em.detach(updatedUserPreferences);
        updatedUserPreferences
            .visibility(UPDATED_VISIBILITY)
            .isDarkMode(UPDATED_IS_DARK_MODE)
            .isHighContrast(UPDATED_IS_HIGH_CONTRAST)
            .playlistPrivacy(UPDATED_PLAYLIST_PRIVACY);

        restUserPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserPreferences.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserPreferences))
            )
            .andExpect(status().isOk());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
        assertThat(testUserPreferences.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testUserPreferences.getIsDarkMode()).isEqualTo(UPDATED_IS_DARK_MODE);
        assertThat(testUserPreferences.getIsHighContrast()).isEqualTo(UPDATED_IS_HIGH_CONTRAST);
        assertThat(testUserPreferences.getPlaylistPrivacy()).isEqualTo(UPDATED_PLAYLIST_PRIVACY);
    }

    @Test
    @Transactional
    void putNonExistingUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userPreferences.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserPreferencesWithPatch() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();

        // Update the userPreferences using partial update
        UserPreferences partialUpdatedUserPreferences = new UserPreferences();
        partialUpdatedUserPreferences.setId(userPreferences.getId());

        partialUpdatedUserPreferences.isHighContrast(UPDATED_IS_HIGH_CONTRAST);

        restUserPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPreferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPreferences))
            )
            .andExpect(status().isOk());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
        assertThat(testUserPreferences.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testUserPreferences.getIsDarkMode()).isEqualTo(DEFAULT_IS_DARK_MODE);
        assertThat(testUserPreferences.getIsHighContrast()).isEqualTo(UPDATED_IS_HIGH_CONTRAST);
        assertThat(testUserPreferences.getPlaylistPrivacy()).isEqualTo(DEFAULT_PLAYLIST_PRIVACY);
    }

    @Test
    @Transactional
    void fullUpdateUserPreferencesWithPatch() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();

        // Update the userPreferences using partial update
        UserPreferences partialUpdatedUserPreferences = new UserPreferences();
        partialUpdatedUserPreferences.setId(userPreferences.getId());

        partialUpdatedUserPreferences
            .visibility(UPDATED_VISIBILITY)
            .isDarkMode(UPDATED_IS_DARK_MODE)
            .isHighContrast(UPDATED_IS_HIGH_CONTRAST)
            .playlistPrivacy(UPDATED_PLAYLIST_PRIVACY);

        restUserPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPreferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPreferences))
            )
            .andExpect(status().isOk());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
        UserPreferences testUserPreferences = userPreferencesList.get(userPreferencesList.size() - 1);
        assertThat(testUserPreferences.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testUserPreferences.getIsDarkMode()).isEqualTo(UPDATED_IS_DARK_MODE);
        assertThat(testUserPreferences.getIsHighContrast()).isEqualTo(UPDATED_IS_HIGH_CONTRAST);
        assertThat(testUserPreferences.getPlaylistPrivacy()).isEqualTo(UPDATED_PLAYLIST_PRIVACY);
    }

    @Test
    @Transactional
    void patchNonExistingUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userPreferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserPreferences() throws Exception {
        int databaseSizeBeforeUpdate = userPreferencesRepository.findAll().size();
        userPreferences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPreferences))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPreferences in the database
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserPreferences() throws Exception {
        // Initialize the database
        userPreferencesRepository.saveAndFlush(userPreferences);

        int databaseSizeBeforeDelete = userPreferencesRepository.findAll().size();

        // Delete the userPreferences
        restUserPreferencesMockMvc
            .perform(delete(ENTITY_API_URL_ID, userPreferences.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findAll();
        assertThat(userPreferencesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
