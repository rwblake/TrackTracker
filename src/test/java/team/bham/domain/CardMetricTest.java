package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class CardMetricTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CardMetric.class);
        CardMetric cardMetric1 = new CardMetric();
        cardMetric1.setId(1L);
        CardMetric cardMetric2 = new CardMetric();
        cardMetric2.setId(cardMetric1.getId());
        assertThat(cardMetric1).isEqualTo(cardMetric2);
        cardMetric2.setId(2L);
        assertThat(cardMetric1).isNotEqualTo(cardMetric2);
        cardMetric1.setId(null);
        assertThat(cardMetric1).isNotEqualTo(cardMetric2);
    }
}
