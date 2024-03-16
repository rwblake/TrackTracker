package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
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
import team.bham.repository.SongRepository;

/**
 * Integration tests for the {@link SongResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SongResourceIT {

    private static final String DEFAULT_SPOTIFY_ID = "AAAAAAAAAA";
    private static final String UPDATED_SPOTIFY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_RELEASE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RELEASE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    private static final Float DEFAULT_ACOUSTICNESS = 0.0F;
    private static final Float UPDATED_ACOUSTICNESS = 1F;

    private static final Float DEFAULT_DANCEABILITY = 0.0F;
    private static final Float UPDATED_DANCEABILITY = 1F;

    private static final Float DEFAULT_ENERGY = 0.0F;
    private static final Float UPDATED_ENERGY = 1F;

    private static final Float DEFAULT_INSTRUMENTALNESS = 0.0F;
    private static final Float UPDATED_INSTRUMENTALNESS = 1F;

    private static final Integer DEFAULT_MUSICAL_KEY = 1;
    private static final Integer UPDATED_MUSICAL_KEY = 2;

    private static final Float DEFAULT_LIVENESS = 0.0F;
    private static final Float UPDATED_LIVENESS = 1F;

    private static final Float DEFAULT_LOUDNESS = 1F;
    private static final Float UPDATED_LOUDNESS = 2F;

    private static final Boolean DEFAULT_MODE = false;
    private static final Boolean UPDATED_MODE = true;

    private static final Float DEFAULT_SPEECHINESS = 0.0F;
    private static final Float UPDATED_SPEECHINESS = 1F;

    private static final Float DEFAULT_TEMPO = 1F;
    private static final Float UPDATED_TEMPO = 2F;

    private static final Integer DEFAULT_TIME_SIGNATURE = 1;
    private static final Integer UPDATED_TIME_SIGNATURE = 2;

    private static final Float DEFAULT_VALENCE = 0.0F;
    private static final Float UPDATED_VALENCE = 1F;

    private static final String ENTITY_API_URL = "/api/songs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSongMockMvc;

    private Song song;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createEntity(EntityManager em) {
        Song song = new Song()
            .spotifyID(DEFAULT_SPOTIFY_ID)
            .name(DEFAULT_NAME)
            .imageURL(DEFAULT_IMAGE_URL)
            .releaseDate(DEFAULT_RELEASE_DATE)
            .duration(DEFAULT_DURATION)
            .acousticness(DEFAULT_ACOUSTICNESS)
            .danceability(DEFAULT_DANCEABILITY)
            .energy(DEFAULT_ENERGY)
            .instrumentalness(DEFAULT_INSTRUMENTALNESS)
            .musicalKey(DEFAULT_MUSICAL_KEY)
            .liveness(DEFAULT_LIVENESS)
            .loudness(DEFAULT_LOUDNESS)
            .mode(DEFAULT_MODE)
            .speechiness(DEFAULT_SPEECHINESS)
            .tempo(DEFAULT_TEMPO)
            .timeSignature(DEFAULT_TIME_SIGNATURE)
            .valence(DEFAULT_VALENCE);
        return song;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createUpdatedEntity(EntityManager em) {
        Song song = new Song()
            .spotifyID(UPDATED_SPOTIFY_ID)
            .name(UPDATED_NAME)
            .imageURL(UPDATED_IMAGE_URL)
            .releaseDate(UPDATED_RELEASE_DATE)
            .duration(UPDATED_DURATION)
            .acousticness(UPDATED_ACOUSTICNESS)
            .danceability(UPDATED_DANCEABILITY)
            .energy(UPDATED_ENERGY)
            .instrumentalness(UPDATED_INSTRUMENTALNESS)
            .musicalKey(UPDATED_MUSICAL_KEY)
            .liveness(UPDATED_LIVENESS)
            .loudness(UPDATED_LOUDNESS)
            .mode(UPDATED_MODE)
            .speechiness(UPDATED_SPEECHINESS)
            .tempo(UPDATED_TEMPO)
            .timeSignature(UPDATED_TIME_SIGNATURE)
            .valence(UPDATED_VALENCE);
        return song;
    }

    @BeforeEach
    public void initTest() {
        song = createEntity(em);
    }

    @Test
    @Transactional
    void createSong() throws Exception {
        int databaseSizeBeforeCreate = songRepository.findAll().size();
        // Create the Song
        restSongMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isCreated());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeCreate + 1);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getSpotifyID()).isEqualTo(DEFAULT_SPOTIFY_ID);
        assertThat(testSong.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSong.getImageURL()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
        assertThat(testSong.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testSong.getAcousticness()).isEqualTo(DEFAULT_ACOUSTICNESS);
        assertThat(testSong.getDanceability()).isEqualTo(DEFAULT_DANCEABILITY);
        assertThat(testSong.getEnergy()).isEqualTo(DEFAULT_ENERGY);
        assertThat(testSong.getInstrumentalness()).isEqualTo(DEFAULT_INSTRUMENTALNESS);
        assertThat(testSong.getMusicalKey()).isEqualTo(DEFAULT_MUSICAL_KEY);
        assertThat(testSong.getLiveness()).isEqualTo(DEFAULT_LIVENESS);
        assertThat(testSong.getLoudness()).isEqualTo(DEFAULT_LOUDNESS);
        assertThat(testSong.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testSong.getSpeechiness()).isEqualTo(DEFAULT_SPEECHINESS);
        assertThat(testSong.getTempo()).isEqualTo(DEFAULT_TEMPO);
        assertThat(testSong.getTimeSignature()).isEqualTo(DEFAULT_TIME_SIGNATURE);
        assertThat(testSong.getValence()).isEqualTo(DEFAULT_VALENCE);
    }

    @Test
    @Transactional
    void createSongWithExistingId() throws Exception {
        // Create the Song with an existing ID
        song.setId(1L);

        int databaseSizeBeforeCreate = songRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSongMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSpotifyIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().size();
        // set the field null
        song.setSpotifyID(null);

        // Create the Song, which fails.

        restSongMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().size();
        // set the field null
        song.setName(null);

        // Create the Song, which fails.

        restSongMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().size();
        // set the field null
        song.setDuration(null);

        // Create the Song, which fails.

        restSongMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSongs() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        // Get all the songList
        restSongMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(song.getId().intValue())))
            .andExpect(jsonPath("$.[*].spotifyID").value(hasItem(DEFAULT_SPOTIFY_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageURL").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())))
            .andExpect(jsonPath("$.[*].acousticness").value(hasItem(DEFAULT_ACOUSTICNESS.doubleValue())))
            .andExpect(jsonPath("$.[*].danceability").value(hasItem(DEFAULT_DANCEABILITY.doubleValue())))
            .andExpect(jsonPath("$.[*].energy").value(hasItem(DEFAULT_ENERGY.doubleValue())))
            .andExpect(jsonPath("$.[*].instrumentalness").value(hasItem(DEFAULT_INSTRUMENTALNESS.doubleValue())))
            .andExpect(jsonPath("$.[*].musicalKey").value(hasItem(DEFAULT_MUSICAL_KEY)))
            .andExpect(jsonPath("$.[*].liveness").value(hasItem(DEFAULT_LIVENESS.doubleValue())))
            .andExpect(jsonPath("$.[*].loudness").value(hasItem(DEFAULT_LOUDNESS.doubleValue())))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.booleanValue())))
            .andExpect(jsonPath("$.[*].speechiness").value(hasItem(DEFAULT_SPEECHINESS.doubleValue())))
            .andExpect(jsonPath("$.[*].tempo").value(hasItem(DEFAULT_TEMPO.doubleValue())))
            .andExpect(jsonPath("$.[*].timeSignature").value(hasItem(DEFAULT_TIME_SIGNATURE)))
            .andExpect(jsonPath("$.[*].valence").value(hasItem(DEFAULT_VALENCE.doubleValue())));
    }

    @Test
    @Transactional
    void getSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        // Get the song
        restSongMockMvc
            .perform(get(ENTITY_API_URL_ID, song.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(song.getId().intValue()))
            .andExpect(jsonPath("$.spotifyID").value(DEFAULT_SPOTIFY_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageURL").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()))
            .andExpect(jsonPath("$.acousticness").value(DEFAULT_ACOUSTICNESS.doubleValue()))
            .andExpect(jsonPath("$.danceability").value(DEFAULT_DANCEABILITY.doubleValue()))
            .andExpect(jsonPath("$.energy").value(DEFAULT_ENERGY.doubleValue()))
            .andExpect(jsonPath("$.instrumentalness").value(DEFAULT_INSTRUMENTALNESS.doubleValue()))
            .andExpect(jsonPath("$.musicalKey").value(DEFAULT_MUSICAL_KEY))
            .andExpect(jsonPath("$.liveness").value(DEFAULT_LIVENESS.doubleValue()))
            .andExpect(jsonPath("$.loudness").value(DEFAULT_LOUDNESS.doubleValue()))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE.booleanValue()))
            .andExpect(jsonPath("$.speechiness").value(DEFAULT_SPEECHINESS.doubleValue()))
            .andExpect(jsonPath("$.tempo").value(DEFAULT_TEMPO.doubleValue()))
            .andExpect(jsonPath("$.timeSignature").value(DEFAULT_TIME_SIGNATURE))
            .andExpect(jsonPath("$.valence").value(DEFAULT_VALENCE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingSong() throws Exception {
        // Get the song
        restSongMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeUpdate = songRepository.findAll().size();

        // Update the song
        Song updatedSong = songRepository.findById(song.getId()).get();
        // Disconnect from session so that the updates on updatedSong are not directly saved in db
        em.detach(updatedSong);
        updatedSong
            .spotifyID(UPDATED_SPOTIFY_ID)
            .name(UPDATED_NAME)
            .imageURL(UPDATED_IMAGE_URL)
            .releaseDate(UPDATED_RELEASE_DATE)
            .duration(UPDATED_DURATION)
            .acousticness(UPDATED_ACOUSTICNESS)
            .danceability(UPDATED_DANCEABILITY)
            .energy(UPDATED_ENERGY)
            .instrumentalness(UPDATED_INSTRUMENTALNESS)
            .musicalKey(UPDATED_MUSICAL_KEY)
            .liveness(UPDATED_LIVENESS)
            .loudness(UPDATED_LOUDNESS)
            .mode(UPDATED_MODE)
            .speechiness(UPDATED_SPEECHINESS)
            .tempo(UPDATED_TEMPO)
            .timeSignature(UPDATED_TIME_SIGNATURE)
            .valence(UPDATED_VALENCE);

        restSongMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSong.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSong))
            )
            .andExpect(status().isOk());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getSpotifyID()).isEqualTo(UPDATED_SPOTIFY_ID);
        assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSong.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testSong.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testSong.getAcousticness()).isEqualTo(UPDATED_ACOUSTICNESS);
        assertThat(testSong.getDanceability()).isEqualTo(UPDATED_DANCEABILITY);
        assertThat(testSong.getEnergy()).isEqualTo(UPDATED_ENERGY);
        assertThat(testSong.getInstrumentalness()).isEqualTo(UPDATED_INSTRUMENTALNESS);
        assertThat(testSong.getMusicalKey()).isEqualTo(UPDATED_MUSICAL_KEY);
        assertThat(testSong.getLiveness()).isEqualTo(UPDATED_LIVENESS);
        assertThat(testSong.getLoudness()).isEqualTo(UPDATED_LOUDNESS);
        assertThat(testSong.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testSong.getSpeechiness()).isEqualTo(UPDATED_SPEECHINESS);
        assertThat(testSong.getTempo()).isEqualTo(UPDATED_TEMPO);
        assertThat(testSong.getTimeSignature()).isEqualTo(UPDATED_TIME_SIGNATURE);
        assertThat(testSong.getValence()).isEqualTo(UPDATED_VALENCE);
    }

    @Test
    @Transactional
    void putNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(
                put(ENTITY_API_URL_ID, song.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(song))
            )
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(song))
            )
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeUpdate = songRepository.findAll().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong
            .spotifyID(UPDATED_SPOTIFY_ID)
            .name(UPDATED_NAME)
            .imageURL(UPDATED_IMAGE_URL)
            .releaseDate(UPDATED_RELEASE_DATE)
            .acousticness(UPDATED_ACOUSTICNESS)
            .energy(UPDATED_ENERGY)
            .loudness(UPDATED_LOUDNESS)
            .mode(UPDATED_MODE);

        restSongMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSong.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            )
            .andExpect(status().isOk());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getSpotifyID()).isEqualTo(UPDATED_SPOTIFY_ID);
        assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSong.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testSong.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testSong.getAcousticness()).isEqualTo(UPDATED_ACOUSTICNESS);
        assertThat(testSong.getDanceability()).isEqualTo(DEFAULT_DANCEABILITY);
        assertThat(testSong.getEnergy()).isEqualTo(UPDATED_ENERGY);
        assertThat(testSong.getInstrumentalness()).isEqualTo(DEFAULT_INSTRUMENTALNESS);
        assertThat(testSong.getMusicalKey()).isEqualTo(DEFAULT_MUSICAL_KEY);
        assertThat(testSong.getLiveness()).isEqualTo(DEFAULT_LIVENESS);
        assertThat(testSong.getLoudness()).isEqualTo(UPDATED_LOUDNESS);
        assertThat(testSong.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testSong.getSpeechiness()).isEqualTo(DEFAULT_SPEECHINESS);
        assertThat(testSong.getTempo()).isEqualTo(DEFAULT_TEMPO);
        assertThat(testSong.getTimeSignature()).isEqualTo(DEFAULT_TIME_SIGNATURE);
        assertThat(testSong.getValence()).isEqualTo(DEFAULT_VALENCE);
    }

    @Test
    @Transactional
    void fullUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeUpdate = songRepository.findAll().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong
            .spotifyID(UPDATED_SPOTIFY_ID)
            .name(UPDATED_NAME)
            .imageURL(UPDATED_IMAGE_URL)
            .releaseDate(UPDATED_RELEASE_DATE)
            .duration(UPDATED_DURATION)
            .acousticness(UPDATED_ACOUSTICNESS)
            .danceability(UPDATED_DANCEABILITY)
            .energy(UPDATED_ENERGY)
            .instrumentalness(UPDATED_INSTRUMENTALNESS)
            .musicalKey(UPDATED_MUSICAL_KEY)
            .liveness(UPDATED_LIVENESS)
            .loudness(UPDATED_LOUDNESS)
            .mode(UPDATED_MODE)
            .speechiness(UPDATED_SPEECHINESS)
            .tempo(UPDATED_TEMPO)
            .timeSignature(UPDATED_TIME_SIGNATURE)
            .valence(UPDATED_VALENCE);

        restSongMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSong.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            )
            .andExpect(status().isOk());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getSpotifyID()).isEqualTo(UPDATED_SPOTIFY_ID);
        assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSong.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testSong.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testSong.getAcousticness()).isEqualTo(UPDATED_ACOUSTICNESS);
        assertThat(testSong.getDanceability()).isEqualTo(UPDATED_DANCEABILITY);
        assertThat(testSong.getEnergy()).isEqualTo(UPDATED_ENERGY);
        assertThat(testSong.getInstrumentalness()).isEqualTo(UPDATED_INSTRUMENTALNESS);
        assertThat(testSong.getMusicalKey()).isEqualTo(UPDATED_MUSICAL_KEY);
        assertThat(testSong.getLiveness()).isEqualTo(UPDATED_LIVENESS);
        assertThat(testSong.getLoudness()).isEqualTo(UPDATED_LOUDNESS);
        assertThat(testSong.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testSong.getSpeechiness()).isEqualTo(UPDATED_SPEECHINESS);
        assertThat(testSong.getTempo()).isEqualTo(UPDATED_TEMPO);
        assertThat(testSong.getTimeSignature()).isEqualTo(UPDATED_TIME_SIGNATURE);
        assertThat(testSong.getValence()).isEqualTo(UPDATED_VALENCE);
    }

    @Test
    @Transactional
    void patchNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, song.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(song))
            )
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(song))
            )
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSongMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeDelete = songRepository.findAll().size();

        // Delete the song
        restSongMockMvc
            .perform(delete(ENTITY_API_URL_ID, song.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
