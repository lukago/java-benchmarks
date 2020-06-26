package benchmark;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Double.NaN;
import static java.time.temporal.ChronoUnit.NANOS;

public class AvgTimeBenchmark<ITER_IN, ITER_OUT> implements Benchmark {

    private static final Random rn = new Random();

    private final int warmUpIterations;
    private final int testCaseIterations;
    private final BiFunction<ITER_IN, IterationContext, ITER_OUT> testCase;
    private final Function<Integer, ITER_IN> dataProvider;
    private final Runnable beforeTestCallback;
    private final Runnable afterWarmupCallback;
    private final List<Duration> times;

    private AvgTimeBenchmark(int warmUpIterations,
        int testCaseIterations,
        BiFunction<ITER_IN, IterationContext, ITER_OUT> testCase,
        Function<Integer, ITER_IN> dataProvider,
        Runnable beforeTestCallback,
        Runnable afterWarmupCallback) {
        Objects.requireNonNull(testCase);
        this.warmUpIterations = warmUpIterations;
        this.testCaseIterations = testCaseIterations;
        this.testCase = testCase;
        this.dataProvider = dataProvider;
        this.beforeTestCallback = beforeTestCallback;
        this.afterWarmupCallback = afterWarmupCallback;
        this.times = new ArrayList<>();
    }

    @Override
    public Duration run() {
        for (int i = 0; i < warmUpIterations; i++) {
            beforeTestCallback.run();
            warmUpIteration(i);
        }

        afterWarmupCallback.run();

        for (int i = 0; i < testCaseIterations; i++) {
            beforeTestCallback.run();
            iteration(i);
        }

        double avg = times.stream()
            .mapToLong(duration -> duration.get(NANOS))
            .average()
            .orElse(NaN);

        return Duration.ofNanos(Math.round(avg));
    }

    private void iteration(Integer iteration) {
        IterationContext context = new IterationContext();
        ITER_IN input = dataProvider.apply(iteration);
        System.gc();

        long start = System.nanoTime();
        Object result = testCase.apply(input, context);
        long time = System.nanoTime() - start - context.getTotalPauseNanos();

        if (!context.excludeResult) {
            times.add(Duration.ofNanos(time));
        }
        context.jitAssertNoPause(result);
    }

    private void warmUpIteration(Integer iteration) {
        IterationContext context = new IterationContext();
        context.isWarmup = true;
        Object result = testCase.apply(dataProvider.apply(iteration), context);
        context.jitAssertNoPause(result);
    }

    public static class IterationContext {

        private final List<Duration> pauseDurations;

        private boolean isWarmup;
        private boolean excludeResult;

        public IterationContext() {
            this.pauseDurations = new ArrayList<>();
        }

        public void pause(Runnable runnable) {
            if (!isWarmup) {
                long start = System.nanoTime();
                runnable.run();
                long time = System.nanoTime() - start;
                pauseDurations.add(Duration.ofNanos(time));
            }
        }

        public void jitAssert(Object result) {
            long start = System.nanoTime();
            consume(result);
            long time = System.nanoTime() - start;
            pauseDurations.add(Duration.ofNanos(time));
        }

        public void jitAssertNoPause(Object result) {
            consume(result);
        }

        /**
         * Do some operations on tested objects so it will prevent JIT from eliminating
         * useless computations.
         *
         * @param result object to prevent JIT optimizations
         */
        private void consume(Object result) {
            if (result == null) {
                System.out.println("[DEBUG] Null result from test");
            } else {
                if (result instanceof Collection &&
                    result.hashCode() - ((Collection) result).size() == rn.nextInt(200000)) {
                    System.out.println("[DEBUG] JIT assertion for collection");
                } else {
                    if ((result.hashCode() == rn.nextInt(200000))) {
                        System.out.println("[DEBUG] JIT assertion for object");
                    }
                }
            }
        }

        public long getTotalPauseNanos() {
            double pauseSum = pauseDurations.stream()
                .mapToLong(duration -> duration.get(NANOS))
                .sum();
            return Math.round(pauseSum);
        }

        public boolean isWarmup() {
            return isWarmup;
        }

        public void exclueResult() {
            this.excludeResult = true;
        }
    }

    public static class Builder<I, O> {
        private int warmUpIterations;
        private int testCaseIterations;
        private BiFunction<I, IterationContext, O> testCase;
        private Function<Integer, I> dataProvider;
        private Runnable beforeTestCallback;
        private Runnable afterWarmupCallback;

        public Builder() {
            this.beforeTestCallback = () -> { };
            this.afterWarmupCallback = () -> { };
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

        public Builder<I, O> testCase(BiFunction<I, IterationContext, O> testCase) {
            this.testCase = testCase;
            return this;
        }

        public Builder<I, O> dataProvider(Function<Integer, I> dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }

        public Builder<I, O> beforeTestCallback(Runnable beforeTestCallback) {
            this.beforeTestCallback = beforeTestCallback;
            return this;
        }

        public Builder<I, O> afterWarmupCallback(Runnable afterWarmupCallback) {
            this.afterWarmupCallback = afterWarmupCallback;
            return this;
        }

        public AvgTimeBenchmark<I, O> build() {
            return new AvgTimeBenchmark<>(warmUpIterations,
                testCaseIterations,
                testCase,
                dataProvider,
                beforeTestCallback,
                afterWarmupCallback);
        }
    }
}
