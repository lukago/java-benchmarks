package lib;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.NANOS;

public class AvgTimeBenchmark<IN, OUT> implements Benchmark {

    private final int warmUpIterations;
    private final int testCaseIterations;
    private final Function<IN, OUT> testCase;
    private final Function<Integer, IN> dataProvider;
    private final Runnable afterWarmUpCallback;
    private final List<Duration> times;

    private AvgTimeBenchmark(int warmUpIterations,
        int testCaseIterations,
        Function<IN, OUT> testCase,
        Function<Integer, IN> dataProvider,
        Runnable afterWarmUpCallback) {
        Objects.requireNonNull(testCase);
        this.warmUpIterations = warmUpIterations;
        this.testCaseIterations = testCaseIterations;
        this.testCase = testCase;
        this.dataProvider = dataProvider;
        this.afterWarmUpCallback = afterWarmUpCallback;
        this.times = new ArrayList<>();
    }

    @Override
    public Duration run() {
        for (int i = 0; i < warmUpIterations; i++) {
            warmUpIteration(i);
        }

        afterWarmUpCallback.run();

        for (int i = 0; i < testCaseIterations; i++) {
            iteration(i);
        }

        double avgNanos = times.stream()
            .mapToLong(duration -> duration.get(NANOS))
            .average()
            .orElse(Double.NaN);

        return Duration.ofNanos(Math.round(avgNanos));
    }

    private void iteration(Integer iteration) {
        IN input = dataProvider.apply(iteration);
        System.gc();

        long start = Instant.now().getNano();
        Object result = testCase.apply(input);
        long time = System.nanoTime() - start;

        times.add(Duration.ofNanos(time));
        jitAssert(result);
    }

    private void warmUpIteration(Integer iteration) {
        Object result = testCase.apply(dataProvider.apply(iteration));
        jitAssert(result);
    }


    /**
     * Do some operations on tested objects so it will prevent JIT from eliminating
     * useless computations.
     *
     * @param result object to prevent JIT optimizations
     */
    public void jitAssert(Object result) {
        if (result instanceof Collection && result.hashCode() - ((Collection) result).size() == 123345) {
            System.out.println("[DEBUG] JIT assertion for collection");
        } else {
            if ((result.hashCode() + result.toString()).equals("123345")) {
                System.out.println("[DEBUG] JIT assertion for object");
            }
        }
    }

    public static class Builder<I, O> {
        private int warmUpIterations;
        private int testCaseIterations;
        private Function<I, O> testCase;
        private Function<Integer, I> dataProvider;
        private Runnable afterWarmUpCallback;

        public Builder() {
            this.afterWarmUpCallback = () -> { };
            this.dataProvider = i -> null;
        }

        public Builder<I, O> warmUpIterations(int warmUpIterations) {
            this.warmUpIterations = warmUpIterations;
            return this;
        }

        public Builder<I, O> testCaseIterations(int testCaseIterations) {
            this.testCaseIterations = testCaseIterations;
            return this;
        }

        public Builder<I, O> testCase(Function<I, O> testCase) {
            this.testCase = testCase;
            return this;
        }

        public Builder<I, O> dataProvider(Function<Integer, I> dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }

        public Builder<I, O> afterWarmUpCallback(Runnable afterWarmUpCallback) {
            this.afterWarmUpCallback = afterWarmUpCallback;
            return this;
        }

        public AvgTimeBenchmark<I, O> build() {
            return new AvgTimeBenchmark<>(warmUpIterations,
                testCaseIterations,
                testCase,
                dataProvider,
                afterWarmUpCallback);
        }
    }
}
