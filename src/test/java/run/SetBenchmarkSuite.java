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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class SetBenchmarkSuite {

    private static abstract class CommonSteps {

        private static Instant createdAt;

        protected CollectionReportGenerator report;

        @BeforeAll
        static void setUpAll() {
            createdAt = Instant.now();
        }

        @BeforeEach
        void setUp() {
            report = new CollectionReportGenerator(getSet().getClass(), createdAt);
        }

        @AfterEach
        void after() {
            report.write();
        }

        abstract Set<Object> getSet();

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
        void setAdd(int warmUp, int tests, int n) {
            var set = getSet();
            var data = TestObject.randomList(n);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    set.clear();
                    set.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase((in, ctx) -> {
                    ctx.jitAssert(set.add(in));
                    return set;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("setAdd", warmUp, tests, n, result);
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
        void setRemove(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>();
            var set = getSet();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    stack.clear();
                    stack.addAll(data);
                    Collections.shuffle(stack);
                    set.clear();
                    set.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase((in, ctx) -> set.remove(in))
                .build();

            var result = benchmark.run();

            assertThat(set.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("setRemove", warmUp, tests, n, result);
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
        void setFullBrowse(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var set = getSet();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    set.clear();
                    set.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    var iterator = set.iterator();
                    while (iterator.hasNext()) {
                        ctx.jitAssert(iterator.next());
                    }
                    return iterator;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("setFullBrowse", warmUp, tests, n, result);
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
        void setExists(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>();
            var set = getSet();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    stack.clear();
                    stack.addAll(data);
                    Collections.shuffle(stack);
                    set.clear();
                    set.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase((in, ctx) -> set.contains(in))
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("setExists", warmUp, tests, n, result);
        }
    }

    @Nested
    class HashSetBenchmark extends CommonSteps {
        @Override
        Set<Object> getSet() {
            return new HashSet<>();
        }
    }

    @Nested
    class TreeSetBenchmark extends CommonSteps {
        @Override
        Set<Object> getSet() {
            return new TreeSet<>();
        }
    }

    @Nested
    class LinkedHashSetBenchmark extends CommonSteps {
        @Override
        Set<Object> getSet() {
            return new LinkedHashSet<>();
        }
    }
}

