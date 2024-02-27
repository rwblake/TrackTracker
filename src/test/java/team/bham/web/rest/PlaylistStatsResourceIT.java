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
import team.bham.domain.Playlist;
import team.bham.domain.PlaylistStats;
import team.bham.repository.PlaylistStatsRepository;

/**
 * Integration tests for the {@link PlaylistStatsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlaylistStatsResourceIT {

    private static final Integer DEFAULT_PLAYLIST_LENGTH = 1;
    private static final Integer UPDATED_PLAYLIST_LENGTH = 2;

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/playlist-stats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlaylistStatsRepository playlistStatsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlaylistStatsMockMvc;

    private PlaylistStats playlistStats;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlaylistStats createEntity(EntityManager em) {
        PlaylistStats playlistStats = new PlaylistStats().playlistLength(DEFAULT_PLAYLIST_LENGTH).lastUpdated(DEFAULT_LAST_UPDATED);
        // Add required entity
        Playlist playlist;
        if (TestUtil.findAll(em, Playlist.class).isEmpty()) {
            playlist = PlaylistResourceIT.createEntity(em);
            em.persist(playlist);
            em.flush();
        } else {
            playlist = TestUtil.findAll(em, Playlist.class).get(0);
        }
        playlistStats.setPlaylist(playlist);
        return playlistStats;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlaylistStats createUpdatedEntity(EntityManager em) {
        PlaylistStats playlistStats = new PlaylistStats().playlistLength(UPDATED_PLAYLIST_LENGTH).lastUpdated(UPDATED_LAST_UPDATED);
        // Add required entity
        Playlist playlist;
        if (TestUtil.findAll(em, Playlist.class).isEmpty()) {
            playlist = PlaylistResourceIT.createUpdatedEntity(em);
            em.persist(playlist);
            em.flush();
        } else {
            playlist = TestUtil.findAll(em, Playlist.class).get(0);
        }
        playlistStats.setPlaylist(playlist);
        return playlistStats;
    }

    @BeforeEach
    public void initTest() {
        playlistStats = createEntity(em);
    }

    @Test
    @Transactional
    void createPlaylistStats() throws Exception {
        int databaseSizeBeforeCreate = playlistStatsRepository.findAll().size();
        // Create the PlaylistStats
        restPlaylistStatsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playlistStats)))
            .andExpect(status().isCreated());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeCreate + 1);
        PlaylistStats testPlaylistStats = playlistStatsList.get(playlistStatsList.size() - 1);
        assertThat(testPlaylistStats.getPlaylistLength()).isEqualTo(DEFAULT_PLAYLIST_LENGTH);
        assertThat(testPlaylistStats.getLastUpdated()).isEqualTo(DEFAULT_LAST_UPDATED);
    }

    @Test
    @Transactional
    void createPlaylistStatsWithExistingId() throws Exception {
        // Create the PlaylistStats with an existing ID
        playlistStats.setId(1L);

        int databaseSizeBeforeCreate = playlistStatsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaylistStatsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playlistStats)))
            .andExpect(status().isBadRequest());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPlaylistStats() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        // Get all the playlistStatsList
        restPlaylistStatsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playlistStats.getId().intValue())))
            .andExpect(jsonPath("$.[*].playlistLength").value(hasItem(DEFAULT_PLAYLIST_LENGTH)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())));
    }

    @Test
    @Transactional
    void getPlaylistStats() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        // Get the playlistStats
        restPlaylistStatsMockMvc
            .perform(get(ENTITY_API_URL_ID, playlistStats.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(playlistStats.getId().intValue()))
            .andExpect(jsonPath("$.playlistLength").value(DEFAULT_PLAYLIST_LENGTH))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPlaylistStats() throws Exception {
        // Get the playlistStats
        restPlaylistStatsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlaylistStats() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();

        // Update the playlistStats
        PlaylistStats updatedPlaylistStats = playlistStatsRepository.findById(playlistStats.getId()).get();
        // Disconnect from session so that the updates on updatedPlaylistStats are not directly saved in db
        em.detach(updatedPlaylistStats);
        updatedPlaylistStats.playlistLength(UPDATED_PLAYLIST_LENGTH).lastUpdated(UPDATED_LAST_UPDATED);

        restPlaylistStatsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlaylistStats.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlaylistStats))
            )
            .andExpect(status().isOk());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
        PlaylistStats testPlaylistStats = playlistStatsList.get(playlistStatsList.size() - 1);
        assertThat(testPlaylistStats.getPlaylistLength()).isEqualTo(UPDATED_PLAYLIST_LENGTH);
        assertThat(testPlaylistStats.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void putNonExistingPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playlistStats.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playlistStats))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playlistStats))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playlistStats)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlaylistStatsWithPatch() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();

        // Update the playlistStats using partial update
        PlaylistStats partialUpdatedPlaylistStats = new PlaylistStats();
        partialUpdatedPlaylistStats.setId(playlistStats.getId());

        partialUpdatedPlaylistStats.playlistLength(UPDATED_PLAYLIST_LENGTH).lastUpdated(UPDATED_LAST_UPDATED);

        restPlaylistStatsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlaylistStats.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlaylistStats))
            )
            .andExpect(status().isOk());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
        PlaylistStats testPlaylistStats = playlistStatsList.get(playlistStatsList.size() - 1);
        assertThat(testPlaylistStats.getPlaylistLength()).isEqualTo(UPDATED_PLAYLIST_LENGTH);
        assertThat(testPlaylistStats.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void fullUpdatePlaylistStatsWithPatch() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();

        // Update the playlistStats using partial update
        PlaylistStats partialUpdatedPlaylistStats = new PlaylistStats();
        partialUpdatedPlaylistStats.setId(playlistStats.getId());

        partialUpdatedPlaylistStats.playlistLength(UPDATED_PLAYLIST_LENGTH).lastUpdated(UPDATED_LAST_UPDATED);

        restPlaylistStatsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlaylistStats.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlaylistStats))
            )
            .andExpect(status().isOk());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
        PlaylistStats testPlaylistStats = playlistStatsList.get(playlistStatsList.size() - 1);
        assertThat(testPlaylistStats.getPlaylistLength()).isEqualTo(UPDATED_PLAYLIST_LENGTH);
        assertThat(testPlaylistStats.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void patchNonExistingPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playlistStats.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playlistStats))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playlistStats))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlaylistStats() throws Exception {
        int databaseSizeBeforeUpdate = playlistStatsRepository.findAll().size();
        playlistStats.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistStatsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(playlistStats))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlaylistStats in the database
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlaylistStats() throws Exception {
        // Initialize the database
        playlistStatsRepository.saveAndFlush(playlistStats);

        int databaseSizeBeforeDelete = playlistStatsRepository.findAll().size();

        // Delete the playlistStats
        restPlaylistStatsMockMvc
            .perform(delete(ENTITY_API_URL_ID, playlistStats.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PlaylistStats> playlistStatsList = playlistStatsRepository.findAll();
        assertThat(playlistStatsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
