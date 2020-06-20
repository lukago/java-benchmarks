package run;

import lib.AvgTimeBenchmark;
import lib.ReportGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueBenchmarkSuite {

    private static abstract class CommonSteps {

        protected ReportGenerator report;

        @BeforeEach
        void setUp() {
            report = new ReportGenerator(getQueue().getClass());
        }

        @AfterEach
        void after() {
            report.write();
        }

        abstract Queue<Object> getQueue();

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
            var queue = getQueue();
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
            var queue = getQueue();
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
                .testCase((in, ctx) -> queue.remove(in))
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
            var queue = getQueue();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> queue.remove())
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
        void queueFullBrowseIterator(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getQueue();
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
            var queue = getQueue();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .beforeTestCallback(() -> {
                    queue.clear();
                    queue.addAll(data);
                })
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    for (int i = 0; i < queue.size(); i++) {
                        ctx.jitAssert(queue.remove());
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
        Queue<Object> getQueue() {
            return new LinkedList<>();
        }
    }

    @Nested
    class PriorityQueueBenchmark extends CommonSteps {
        @Override
        Queue<Object> getQueue() {
            return new PriorityQueue<>();
        }
    }
}
