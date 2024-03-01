package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class FriendRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FriendRequest.class);
        FriendRequest friendRequest1 = new FriendRequest();
        friendRequest1.setId(1L);
        FriendRequest friendRequest2 = new FriendRequest();
        friendRequest2.setId(friendRequest1.getId());
        assertThat(friendRequest1).isEqualTo(friendRequest2);
        friendRequest2.setId(2L);
        assertThat(friendRequest1).isNotEqualTo(friendRequest2);
        friendRequest1.setId(null);
        assertThat(friendRequest1).isNotEqualTo(friendRequest2);
    }
}
