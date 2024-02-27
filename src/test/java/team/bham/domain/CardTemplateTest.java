package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class CardTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CardTemplate.class);
        CardTemplate cardTemplate1 = new CardTemplate();
        cardTemplate1.setId(1L);
        CardTemplate cardTemplate2 = new CardTemplate();
        cardTemplate2.setId(cardTemplate1.getId());
        assertThat(cardTemplate1).isEqualTo(cardTemplate2);
        cardTemplate2.setId(2L);
        assertThat(cardTemplate1).isNotEqualTo(cardTemplate2);
        cardTemplate1.setId(null);
        assertThat(cardTemplate1).isNotEqualTo(cardTemplate2);
    }
}
