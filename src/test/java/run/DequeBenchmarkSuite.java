package run;

import benchmark.AvgTimeBenchmark;
import data.TestObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import report.CollectionReportGenerator;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

public class DequeBenchmarkSuite {

    private static abstract class CommonSteps {

        private static Instant createdAt;

        protected CollectionReportGenerator report;

        @BeforeAll
        static void setUpAll() {
            createdAt = Instant.now();
        }

        @BeforeEach
        void setUp() {
            report = new CollectionReportGenerator(getDeque().getClass(), createdAt);
        }

        @AfterEach
        void after() {
            report.write();
        }

        abstract Deque<Object> getDeque();

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueAddBegin(int warmUp, int tests, int n) {
            var queue = getDeque();
            queue.addAll(TestObject.randomList(n));
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase((in, ctx) -> {
                    queue.addFirst(in);
                    return queue;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("queueAddBegin", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueAddEnd(int warmUp, int tests, int n) {
            var queue = getDeque();
            var data = TestObject.randomList(n);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase((in, ctx) -> {
                    queue.add(in);
                    return queue;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("queueAddEnd", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueRemoveRandom(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>();
            var queue = getDeque();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    stack.clear();
                    stack.addAll(data);
                    Collections.shuffle(stack);
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase((in, ctx) -> queue.remove())
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("queueRemoveRandom", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueRemoveEnd(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getDeque();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> queue.removeLast())
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("queueRemoveEnd", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueRemoveBegin(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getDeque();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> queue.removeFirst())
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("queueRemoveBegin", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueFullBrowseIterator(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getDeque();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    var iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        ctx.jitAssert(iterator.next());
                    }
                    return iterator;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("queueFullBrowseIterator", warmUp, tests, n, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          10",
            "100,       10,          10",
            "0,         10,          100",
            "100,       10,          100",
            "0,         10,          1000",
            "100,       10,          1000",
            "0,         10,          10000",
            "100,       10,          10000",
            "0,         10,          100000",
            "100,       10,          100000",
        })
        void queueFullBrowseForLoop(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getDeque();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    for (int i = 0; i < queue.size() - 1; i++) {
                        ctx.jitAssert(queue.pop());
                    }
                    return queue;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("queueFullBrowseForLoop", warmUp, tests, n, result);
        }
    }

    @Nested
    class LinkedListBenchmark extends CommonSteps {
        @Override
        Deque<Object> getDeque() {
            return new LinkedList<>();
        }
    }

    @Nested
    class ArrayDequeBenchmark extends CommonSteps {
        @Override
        Deque<Object> getDeque() {
            return new ArrayDeque<>();
        }
    }
}
