package benchmark;

import java.time.Duration;

public interface Benchmark {

    /**
     * Benchmarks time and returns result.
     *
     * @return benchmark result
     */
    Duration run();
}
