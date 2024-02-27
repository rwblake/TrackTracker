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
import team.bham.domain.Song;
import team.bham.domain.Stream;
import team.bham.repository.StreamRepository;

/**
 * Integration tests for the {@link StreamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StreamResourceIT {

    private static final Instant DEFAULT_PLAYED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLAYED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/streams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStreamMockMvc;

    private Stream stream;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stream createEntity(EntityManager em) {
        Stream stream = new Stream().playedAt(DEFAULT_PLAYED_AT);
        // Add required entity
        Song song;
        if (TestUtil.findAll(em, Song.class).isEmpty()) {
            song = SongResourceIT.createEntity(em);
            em.persist(song);
            em.flush();
        } else {
            song = TestUtil.findAll(em, Song.class).get(0);
        }
        stream.setSong(song);
        return stream;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stream createUpdatedEntity(EntityManager em) {
        Stream stream = new Stream().playedAt(UPDATED_PLAYED_AT);
        // Add required entity
        Song song;
        if (TestUtil.findAll(em, Song.class).isEmpty()) {
            song = SongResourceIT.createUpdatedEntity(em);
            em.persist(song);
            em.flush();
        } else {
            song = TestUtil.findAll(em, Song.class).get(0);
        }
        stream.setSong(song);
        return stream;
    }

    @BeforeEach
    public void initTest() {
        stream = createEntity(em);
    }

    @Test
    @Transactional
    void createStream() throws Exception {
        int databaseSizeBeforeCreate = streamRepository.findAll().size();
        // Create the Stream
        restStreamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stream)))
            .andExpect(status().isCreated());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeCreate + 1);
        Stream testStream = streamList.get(streamList.size() - 1);
        assertThat(testStream.getPlayedAt()).isEqualTo(DEFAULT_PLAYED_AT);
    }

    @Test
    @Transactional
    void createStreamWithExistingId() throws Exception {
        // Create the Stream with an existing ID
        stream.setId(1L);

        int databaseSizeBeforeCreate = streamRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStreamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stream)))
            .andExpect(status().isBadRequest());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPlayedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = streamRepository.findAll().size();
        // set the field null
        stream.setPlayedAt(null);

        // Create the Stream, which fails.

        restStreamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stream)))
            .andExpect(status().isBadRequest());

        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStreams() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        // Get all the streamList
        restStreamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stream.getId().intValue())))
            .andExpect(jsonPath("$.[*].playedAt").value(hasItem(DEFAULT_PLAYED_AT.toString())));
    }

    @Test
    @Transactional
    void getStream() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        // Get the stream
        restStreamMockMvc
            .perform(get(ENTITY_API_URL_ID, stream.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stream.getId().intValue()))
            .andExpect(jsonPath("$.playedAt").value(DEFAULT_PLAYED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStream() throws Exception {
        // Get the stream
        restStreamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStream() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        int databaseSizeBeforeUpdate = streamRepository.findAll().size();

        // Update the stream
        Stream updatedStream = streamRepository.findById(stream.getId()).get();
        // Disconnect from session so that the updates on updatedStream are not directly saved in db
        em.detach(updatedStream);
        updatedStream.playedAt(UPDATED_PLAYED_AT);

        restStreamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStream.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStream))
            )
            .andExpect(status().isOk());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
        Stream testStream = streamList.get(streamList.size() - 1);
        assertThat(testStream.getPlayedAt()).isEqualTo(UPDATED_PLAYED_AT);
    }

    @Test
    @Transactional
    void putNonExistingStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stream.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stream))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stream))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stream)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStreamWithPatch() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        int databaseSizeBeforeUpdate = streamRepository.findAll().size();

        // Update the stream using partial update
        Stream partialUpdatedStream = new Stream();
        partialUpdatedStream.setId(stream.getId());

        partialUpdatedStream.playedAt(UPDATED_PLAYED_AT);

        restStreamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStream.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStream))
            )
            .andExpect(status().isOk());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
        Stream testStream = streamList.get(streamList.size() - 1);
        assertThat(testStream.getPlayedAt()).isEqualTo(UPDATED_PLAYED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStreamWithPatch() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        int databaseSizeBeforeUpdate = streamRepository.findAll().size();

        // Update the stream using partial update
        Stream partialUpdatedStream = new Stream();
        partialUpdatedStream.setId(stream.getId());

        partialUpdatedStream.playedAt(UPDATED_PLAYED_AT);

        restStreamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStream.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStream))
            )
            .andExpect(status().isOk());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
        Stream testStream = streamList.get(streamList.size() - 1);
        assertThat(testStream.getPlayedAt()).isEqualTo(UPDATED_PLAYED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stream.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stream))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stream))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStream() throws Exception {
        int databaseSizeBeforeUpdate = streamRepository.findAll().size();
        stream.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStreamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stream)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stream in the database
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStream() throws Exception {
        // Initialize the database
        streamRepository.saveAndFlush(stream);

        int databaseSizeBeforeDelete = streamRepository.findAll().size();

        // Delete the stream
        restStreamMockMvc
            .perform(delete(ENTITY_API_URL_ID, stream.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stream> streamList = streamRepository.findAll();
        assertThat(streamList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
