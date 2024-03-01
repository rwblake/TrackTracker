package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class FeedCardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeedCard.class);
        FeedCard feedCard1 = new FeedCard();
        feedCard1.setId(1L);
        FeedCard feedCard2 = new FeedCard();
        feedCard2.setId(feedCard1.getId());
        assertThat(feedCard1).isEqualTo(feedCard2);
        feedCard2.setId(2L);
        assertThat(feedCard1).isNotEqualTo(feedCard2);
        feedCard1.setId(null);
        assertThat(feedCard1).isNotEqualTo(feedCard2);
    }
}
