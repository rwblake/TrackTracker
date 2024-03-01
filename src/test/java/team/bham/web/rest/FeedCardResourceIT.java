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
import team.bham.domain.Card;
import team.bham.domain.Feed;
import team.bham.domain.FeedCard;
import team.bham.repository.FeedCardRepository;

/**
 * Integration tests for the {@link FeedCardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FeedCardResourceIT {

    private static final Boolean DEFAULT_LIKED = false;
    private static final Boolean UPDATED_LIKED = true;

    private static final String ENTITY_API_URL = "/api/feed-cards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FeedCardRepository feedCardRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeedCardMockMvc;

    private FeedCard feedCard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeedCard createEntity(EntityManager em) {
        FeedCard feedCard = new FeedCard().liked(DEFAULT_LIKED);
        // Add required entity
        Feed feed;
        if (TestUtil.findAll(em, Feed.class).isEmpty()) {
            feed = FeedResourceIT.createEntity(em);
            em.persist(feed);
            em.flush();
        } else {
            feed = TestUtil.findAll(em, Feed.class).get(0);
        }
        feedCard.setFeed(feed);
        // Add required entity
        Card card;
        if (TestUtil.findAll(em, Card.class).isEmpty()) {
            card = CardResourceIT.createEntity(em);
            em.persist(card);
            em.flush();
        } else {
            card = TestUtil.findAll(em, Card.class).get(0);
        }
        feedCard.setCard(card);
        return feedCard;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeedCard createUpdatedEntity(EntityManager em) {
        FeedCard feedCard = new FeedCard().liked(UPDATED_LIKED);
        // Add required entity
        Feed feed;
        if (TestUtil.findAll(em, Feed.class).isEmpty()) {
            feed = FeedResourceIT.createUpdatedEntity(em);
            em.persist(feed);
            em.flush();
        } else {
            feed = TestUtil.findAll(em, Feed.class).get(0);
        }
        feedCard.setFeed(feed);
        // Add required entity
        Card card;
        if (TestUtil.findAll(em, Card.class).isEmpty()) {
            card = CardResourceIT.createUpdatedEntity(em);
            em.persist(card);
            em.flush();
        } else {
            card = TestUtil.findAll(em, Card.class).get(0);
        }
        feedCard.setCard(card);
        return feedCard;
    }

    @BeforeEach
    public void initTest() {
        feedCard = createEntity(em);
    }

    @Test
    @Transactional
    void createFeedCard() throws Exception {
        int databaseSizeBeforeCreate = feedCardRepository.findAll().size();
        // Create the FeedCard
        restFeedCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feedCard)))
            .andExpect(status().isCreated());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeCreate + 1);
        FeedCard testFeedCard = feedCardList.get(feedCardList.size() - 1);
        assertThat(testFeedCard.getLiked()).isEqualTo(DEFAULT_LIKED);
    }

    @Test
    @Transactional
    void createFeedCardWithExistingId() throws Exception {
        // Create the FeedCard with an existing ID
        feedCard.setId(1L);

        int databaseSizeBeforeCreate = feedCardRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feedCard)))
            .andExpect(status().isBadRequest());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLikedIsRequired() throws Exception {
        int databaseSizeBeforeTest = feedCardRepository.findAll().size();
        // set the field null
        feedCard.setLiked(null);

        // Create the FeedCard, which fails.

        restFeedCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feedCard)))
            .andExpect(status().isBadRequest());

        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeedCards() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        // Get all the feedCardList
        restFeedCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedCard.getId().intValue())))
            .andExpect(jsonPath("$.[*].liked").value(hasItem(DEFAULT_LIKED.booleanValue())));
    }

    @Test
    @Transactional
    void getFeedCard() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        // Get the feedCard
        restFeedCardMockMvc
            .perform(get(ENTITY_API_URL_ID, feedCard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feedCard.getId().intValue()))
            .andExpect(jsonPath("$.liked").value(DEFAULT_LIKED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingFeedCard() throws Exception {
        // Get the feedCard
        restFeedCardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeedCard() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();

        // Update the feedCard
        FeedCard updatedFeedCard = feedCardRepository.findById(feedCard.getId()).get();
        // Disconnect from session so that the updates on updatedFeedCard are not directly saved in db
        em.detach(updatedFeedCard);
        updatedFeedCard.liked(UPDATED_LIKED);

        restFeedCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFeedCard.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFeedCard))
            )
            .andExpect(status().isOk());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
        FeedCard testFeedCard = feedCardList.get(feedCardList.size() - 1);
        assertThat(testFeedCard.getLiked()).isEqualTo(UPDATED_LIKED);
    }

    @Test
    @Transactional
    void putNonExistingFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feedCard.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(feedCard))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(feedCard))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feedCard)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeedCardWithPatch() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();

        // Update the feedCard using partial update
        FeedCard partialUpdatedFeedCard = new FeedCard();
        partialUpdatedFeedCard.setId(feedCard.getId());

        restFeedCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFeedCard))
            )
            .andExpect(status().isOk());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
        FeedCard testFeedCard = feedCardList.get(feedCardList.size() - 1);
        assertThat(testFeedCard.getLiked()).isEqualTo(DEFAULT_LIKED);
    }

    @Test
    @Transactional
    void fullUpdateFeedCardWithPatch() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();

        // Update the feedCard using partial update
        FeedCard partialUpdatedFeedCard = new FeedCard();
        partialUpdatedFeedCard.setId(feedCard.getId());

        partialUpdatedFeedCard.liked(UPDATED_LIKED);

        restFeedCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFeedCard))
            )
            .andExpect(status().isOk());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
        FeedCard testFeedCard = feedCardList.get(feedCardList.size() - 1);
        assertThat(testFeedCard.getLiked()).isEqualTo(UPDATED_LIKED);
    }

    @Test
    @Transactional
    void patchNonExistingFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, feedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(feedCard))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(feedCard))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeedCard() throws Exception {
        int databaseSizeBeforeUpdate = feedCardRepository.findAll().size();
        feedCard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedCardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(feedCard)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeedCard in the database
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeedCard() throws Exception {
        // Initialize the database
        feedCardRepository.saveAndFlush(feedCard);

        int databaseSizeBeforeDelete = feedCardRepository.findAll().size();

        // Delete the feedCard
        restFeedCardMockMvc
            .perform(delete(ENTITY_API_URL_ID, feedCard.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FeedCard> feedCardList = feedCardRepository.findAll();
        assertThat(feedCardList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
