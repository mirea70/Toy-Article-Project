package kuke.board.articleread.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OptimizedCacheTTLTest {
    @Test
    void ofTest() {
        // given
        long ttlSeconds = 10;

        // when
        OptimizeCacheTTL optimizeCacheTTL = OptimizeCacheTTL.of(ttlSeconds);

        // then
        assertThat(optimizeCacheTTL.getLogicalTTL()).isEqualTo(Duration.ofSeconds(ttlSeconds));
        assertThat(optimizeCacheTTL.getPhysicalTTL()).isEqualTo(
                Duration.ofSeconds(ttlSeconds).plusSeconds(OptimizeCacheTTL.PHYSICAL_TTL_DELAY_SECONDS)
        );
    }

}