package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class SharingPreferenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SharingPreference.class);
        SharingPreference sharingPreference1 = new SharingPreference();
        sharingPreference1.setId(1L);
        SharingPreference sharingPreference2 = new SharingPreference();
        sharingPreference2.setId(sharingPreference1.getId());
        assertThat(sharingPreference1).isEqualTo(sharingPreference2);
        sharingPreference2.setId(2L);
        assertThat(sharingPreference1).isNotEqualTo(sharingPreference2);
        sharingPreference1.setId(null);
        assertThat(sharingPreference1).isNotEqualTo(sharingPreference2);
    }
}
