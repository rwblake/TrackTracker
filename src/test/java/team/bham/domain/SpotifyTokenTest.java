package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class SpotifyTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpotifyToken.class);
        SpotifyToken spotifyToken1 = new SpotifyToken();
        spotifyToken1.setId(1L);
        SpotifyToken spotifyToken2 = new SpotifyToken();
        spotifyToken2.setId(spotifyToken1.getId());
        assertThat(spotifyToken1).isEqualTo(spotifyToken2);
        spotifyToken2.setId(2L);
        assertThat(spotifyToken1).isNotEqualTo(spotifyToken2);
        spotifyToken1.setId(null);
        assertThat(spotifyToken1).isNotEqualTo(spotifyToken2);
    }
}
