package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class PlaylistStatsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlaylistStats.class);
        PlaylistStats playlistStats1 = new PlaylistStats();
        playlistStats1.setId(1L);
        PlaylistStats playlistStats2 = new PlaylistStats();
        playlistStats2.setId(playlistStats1.getId());
        assertThat(playlistStats1).isEqualTo(playlistStats2);
        playlistStats2.setId(2L);
        assertThat(playlistStats1).isNotEqualTo(playlistStats2);
        playlistStats1.setId(null);
        assertThat(playlistStats1).isNotEqualTo(playlistStats2);
    }
}
