package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class StreamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stream.class);
        Stream stream1 = new Stream();
        stream1.setId(1L);
        Stream stream2 = new Stream();
        stream2.setId(stream1.getId());
        assertThat(stream1).isEqualTo(stream2);
        stream2.setId(2L);
        assertThat(stream1).isNotEqualTo(stream2);
        stream1.setId(null);
        assertThat(stream1).isNotEqualTo(stream2);
    }
}
