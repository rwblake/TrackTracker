package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import team.bham.domain.SpotifyToken;
import team.bham.repository.SpotifyTokenRepository;

/**
 * Integration tests for the {@link SpotifyTokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpotifyTokenResourceIT {

    private static final String DEFAULT_ACCESS_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_ACCESS_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_USER_SCOPE = "AAAAAAAAAA";
    private static final String UPDATED_USER_SCOPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRES = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REFRESH_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_REFRESH_TOKEN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/spotify-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpotifyTokenRepository spotifyTokenRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpotifyTokenMockMvc;

    private SpotifyToken spotifyToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpotifyToken createEntity(EntityManager em) {
        SpotifyToken spotifyToken = new SpotifyToken()
            .accessToken(DEFAULT_ACCESS_TOKEN)
            .tokenType(DEFAULT_TOKEN_TYPE)
            .userScope(DEFAULT_USER_SCOPE)
            .expires(DEFAULT_EXPIRES)
            .refreshToken(DEFAULT_REFRESH_TOKEN);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        spotifyToken.setAppUser(appUser);
        return spotifyToken;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpotifyToken createUpdatedEntity(EntityManager em) {
        SpotifyToken spotifyToken = new SpotifyToken()
            .accessToken(UPDATED_ACCESS_TOKEN)
            .tokenType(UPDATED_TOKEN_TYPE)
            .userScope(UPDATED_USER_SCOPE)
            .expires(UPDATED_EXPIRES)
            .refreshToken(UPDATED_REFRESH_TOKEN);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createUpdatedEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        spotifyToken.setAppUser(appUser);
        return spotifyToken;
    }

    @BeforeEach
    public void initTest() {
        spotifyToken = createEntity(em);
    }

    @Test
    @Transactional
    void createSpotifyToken() throws Exception {
        int databaseSizeBeforeCreate = spotifyTokenRepository.findAll().size();
        // Create the SpotifyToken
        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isCreated());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeCreate + 1);
        SpotifyToken testSpotifyToken = spotifyTokenList.get(spotifyTokenList.size() - 1);
        assertThat(testSpotifyToken.getAccessToken()).isEqualTo(DEFAULT_ACCESS_TOKEN);
        assertThat(testSpotifyToken.getTokenType()).isEqualTo(DEFAULT_TOKEN_TYPE);
        assertThat(testSpotifyToken.getUserScope()).isEqualTo(DEFAULT_USER_SCOPE);
        assertThat(testSpotifyToken.getExpires()).isEqualTo(DEFAULT_EXPIRES);
        assertThat(testSpotifyToken.getRefreshToken()).isEqualTo(DEFAULT_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void createSpotifyTokenWithExistingId() throws Exception {
        // Create the SpotifyToken with an existing ID
        spotifyToken.setId(1L);

        int databaseSizeBeforeCreate = spotifyTokenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAccessTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotifyTokenRepository.findAll().size();
        // set the field null
        spotifyToken.setAccessToken(null);

        // Create the SpotifyToken, which fails.

        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotifyTokenRepository.findAll().size();
        // set the field null
        spotifyToken.setTokenType(null);

        // Create the SpotifyToken, which fails.

        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUserScopeIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotifyTokenRepository.findAll().size();
        // set the field null
        spotifyToken.setUserScope(null);

        // Create the SpotifyToken, which fails.

        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotifyTokenRepository.findAll().size();
        // set the field null
        spotifyToken.setExpires(null);

        // Create the SpotifyToken, which fails.

        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRefreshTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotifyTokenRepository.findAll().size();
        // set the field null
        spotifyToken.setRefreshToken(null);

        // Create the SpotifyToken, which fails.

        restSpotifyTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isBadRequest());

        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpotifyTokens() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        // Get all the spotifyTokenList
        restSpotifyTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spotifyToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].accessToken").value(hasItem(DEFAULT_ACCESS_TOKEN)))
            .andExpect(jsonPath("$.[*].tokenType").value(hasItem(DEFAULT_TOKEN_TYPE)))
            .andExpect(jsonPath("$.[*].userScope").value(hasItem(DEFAULT_USER_SCOPE)))
            .andExpect(jsonPath("$.[*].expires").value(hasItem(DEFAULT_EXPIRES.toString())))
            .andExpect(jsonPath("$.[*].refreshToken").value(hasItem(DEFAULT_REFRESH_TOKEN)));
    }

    @Test
    @Transactional
    void getSpotifyToken() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        // Get the spotifyToken
        restSpotifyTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, spotifyToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spotifyToken.getId().intValue()))
            .andExpect(jsonPath("$.accessToken").value(DEFAULT_ACCESS_TOKEN))
            .andExpect(jsonPath("$.tokenType").value(DEFAULT_TOKEN_TYPE))
            .andExpect(jsonPath("$.userScope").value(DEFAULT_USER_SCOPE))
            .andExpect(jsonPath("$.expires").value(DEFAULT_EXPIRES.toString()))
            .andExpect(jsonPath("$.refreshToken").value(DEFAULT_REFRESH_TOKEN));
    }

    @Test
    @Transactional
    void getNonExistingSpotifyToken() throws Exception {
        // Get the spotifyToken
        restSpotifyTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpotifyToken() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();

        // Update the spotifyToken
        SpotifyToken updatedSpotifyToken = spotifyTokenRepository.findById(spotifyToken.getId()).get();
        // Disconnect from session so that the updates on updatedSpotifyToken are not directly saved in db
        em.detach(updatedSpotifyToken);
        updatedSpotifyToken
            .accessToken(UPDATED_ACCESS_TOKEN)
            .tokenType(UPDATED_TOKEN_TYPE)
            .userScope(UPDATED_USER_SCOPE)
            .expires(UPDATED_EXPIRES)
            .refreshToken(UPDATED_REFRESH_TOKEN);

        restSpotifyTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpotifyToken.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpotifyToken))
            )
            .andExpect(status().isOk());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
        SpotifyToken testSpotifyToken = spotifyTokenList.get(spotifyTokenList.size() - 1);
        assertThat(testSpotifyToken.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testSpotifyToken.getTokenType()).isEqualTo(UPDATED_TOKEN_TYPE);
        assertThat(testSpotifyToken.getUserScope()).isEqualTo(UPDATED_USER_SCOPE);
        assertThat(testSpotifyToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
        assertThat(testSpotifyToken.getRefreshToken()).isEqualTo(UPDATED_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void putNonExistingSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spotifyToken.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spotifyToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spotifyToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spotifyToken)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpotifyTokenWithPatch() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();

        // Update the spotifyToken using partial update
        SpotifyToken partialUpdatedSpotifyToken = new SpotifyToken();
        partialUpdatedSpotifyToken.setId(spotifyToken.getId());

        partialUpdatedSpotifyToken.accessToken(UPDATED_ACCESS_TOKEN).expires(UPDATED_EXPIRES).refreshToken(UPDATED_REFRESH_TOKEN);

        restSpotifyTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpotifyToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpotifyToken))
            )
            .andExpect(status().isOk());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
        SpotifyToken testSpotifyToken = spotifyTokenList.get(spotifyTokenList.size() - 1);
        assertThat(testSpotifyToken.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testSpotifyToken.getTokenType()).isEqualTo(DEFAULT_TOKEN_TYPE);
        assertThat(testSpotifyToken.getUserScope()).isEqualTo(DEFAULT_USER_SCOPE);
        assertThat(testSpotifyToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
        assertThat(testSpotifyToken.getRefreshToken()).isEqualTo(UPDATED_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void fullUpdateSpotifyTokenWithPatch() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();

        // Update the spotifyToken using partial update
        SpotifyToken partialUpdatedSpotifyToken = new SpotifyToken();
        partialUpdatedSpotifyToken.setId(spotifyToken.getId());

        partialUpdatedSpotifyToken
            .accessToken(UPDATED_ACCESS_TOKEN)
            .tokenType(UPDATED_TOKEN_TYPE)
            .userScope(UPDATED_USER_SCOPE)
            .expires(UPDATED_EXPIRES)
            .refreshToken(UPDATED_REFRESH_TOKEN);

        restSpotifyTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpotifyToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpotifyToken))
            )
            .andExpect(status().isOk());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
        SpotifyToken testSpotifyToken = spotifyTokenList.get(spotifyTokenList.size() - 1);
        assertThat(testSpotifyToken.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testSpotifyToken.getTokenType()).isEqualTo(UPDATED_TOKEN_TYPE);
        assertThat(testSpotifyToken.getUserScope()).isEqualTo(UPDATED_USER_SCOPE);
        assertThat(testSpotifyToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
        assertThat(testSpotifyToken.getRefreshToken()).isEqualTo(UPDATED_REFRESH_TOKEN);
    }

    @Test
    @Transactional
    void patchNonExistingSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spotifyToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spotifyToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spotifyToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpotifyToken() throws Exception {
        int databaseSizeBeforeUpdate = spotifyTokenRepository.findAll().size();
        spotifyToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpotifyTokenMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(spotifyToken))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpotifyToken in the database
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpotifyToken() throws Exception {
        // Initialize the database
        spotifyTokenRepository.saveAndFlush(spotifyToken);

        int databaseSizeBeforeDelete = spotifyTokenRepository.findAll().size();

        // Delete the spotifyToken
        restSpotifyTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, spotifyToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SpotifyToken> spotifyTokenList = spotifyTokenRepository.findAll();
        assertThat(spotifyTokenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
