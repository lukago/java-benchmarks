package run;

import lib.AvgTimeBenchmark;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueBenchmarkSuite {

    private static final Random rnd = new Random();

    private static abstract class CommonSteps {

        abstract Queue<Object> getQueue();

        @ParameterizedTest
        @CsvSource({
            "0,         10",
            "0,         100",
            "0,         1000",
            "10,        10",
            "100,       100",
            "1000,      1000",
        })
        void queueAddEnd(int warmUp, int tests) {
            var queue = getQueue();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase(in -> {
                    queue.add(in);
                    return queue;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          1000",
            "0,         100 ,        1000",
            "0,         1000 ,       1000",
            "10,        10 ,         1000",
            "100,       100 ,        1000",
            "1000,      1000 ,       2000",
        })
        void queueRemoveRandomTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>(data);
            Collections.shuffle(stack);
            var queue = getQueue();
            queue.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase(queue::remove)
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          1000",
            "0,         100 ,        1000",
            "0,         1000 ,       1000",
            "10,        10 ,         1000",
            "100,       100 ,        1000",
            "1000,      1000 ,       2000",
        })
        void queueRemoveEndTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getQueue();
            queue.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> queue.remove(queue.size() - 1))
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          1000",
            "0,         100 ,        1000",
            "0,         1000 ,       1000",
            "10,        10 ,         1000",
            "100,       100 ,        1000",
            "1000,      1000 ,       2000",
        })
        void queueRemoveBeginTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getQueue();
            queue.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> queue.remove(0))
                .build();

            var result = benchmark.run();

            assertThat(queue.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          1000",
            "0,         100 ,        1000",
            "0,         1000 ,       1000",
            "10,        10 ,         1000",
            "100,       100 ,        1000",
            "1000,      1000 ,       2000",
        })
        void queueFullBrowseIteratorTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getQueue();
            queue.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> {
                    var iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                    }
                    return iterator;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10,          1000",
            "0,         100 ,        1000",
            "0,         1000 ,       1000",
            "10,        10 ,         1000",
            "100,       100 ,        1000",
            "1000,      1000 ,       2000",
        })
        void queueFullBrowseForLoopTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var queue = getQueue();
            queue.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> {
                    Object lookUp = null;
                    for (int i = 0; i < queue.size(); i++) {
                        lookUp = queue.poll();
                    }
                    return lookUp;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
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
