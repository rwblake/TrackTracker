package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class FriendRecommendationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FriendRecommendation.class);
        FriendRecommendation friendRecommendation1 = new FriendRecommendation();
        friendRecommendation1.setId(1L);
        FriendRecommendation friendRecommendation2 = new FriendRecommendation();
        friendRecommendation2.setId(friendRecommendation1.getId());
        assertThat(friendRecommendation1).isEqualTo(friendRecommendation2);
        friendRecommendation2.setId(2L);
        assertThat(friendRecommendation1).isNotEqualTo(friendRecommendation2);
        friendRecommendation1.setId(null);
        assertThat(friendRecommendation1).isNotEqualTo(friendRecommendation2);
    }
}
