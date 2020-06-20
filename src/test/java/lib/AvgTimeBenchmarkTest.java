package lib;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AvgTimeBenchmarkTest {

    @Test
    void shouldReturnZeroWhenNoIterations() {
        var benchmark = new AvgTimeBenchmark.Builder<>()
            .testCaseIterations(0)
            .warmUpIterations(0)
            .testCase((i, ctx) -> Collections.emptyList())
            .build();

        var result = benchmark.run();

        assertThat(result).isZero();
    }
}
