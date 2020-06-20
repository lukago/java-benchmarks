package run;

import lib.AvgTimeBenchmark;
import lib.ReportGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ListBenchmarkSuite {

    private static final Random rnd = new Random();

    private static abstract class CommonSteps {

        protected ReportGenerator report;

        @BeforeEach
        void setUp() {
            report = new ReportGenerator(getList().getClass());
        }

        @AfterEach
        void after() {
            report.write();
        }

        abstract List<Object> getList();

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
        void listAddBegin(int warmUp, int tests, int n) {
            var list = getList();
            var data = TestObject.randomList(n);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase((in, ctx) -> {
                    list.add(0, in);
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("listAddBegin", warmUp, tests, n, result);
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
        void listAddEnd(int warmUp, int tests, int n) {
            var list = getList();
            var data = TestObject.randomList(n);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase((in, ctx) -> {
                    list.add(in);
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("listAddEnd", warmUp, tests, n, result);
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
        void listAddRandom(int warmUp, int tests, int n) {
            var list = getList();
            var data = TestObject.randomList(n);
            var input = TestObject.random();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, List<?>>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> rnd.nextInt(list.size()))
                .testCase((in, ctx) -> {
                    list.add(in, input);
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("listAddRandom", warmUp, tests, n, result);
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
        void listRemoveRandom(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>();
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    stack.clear();
                    stack.addAll(data);
                    Collections.shuffle(stack);
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase((in, ctx) -> list.remove(in))
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("listRemoveRandom", warmUp, tests, n, result);
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
        void listRemoveEnd(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> list.remove(list.size() - 1))
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("listRemoveEnd", warmUp, tests, n, result);
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
        void listRemoveBegin(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> list.remove(0))
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isEqualTo(n - 1);
            assertThat(result).isNotNull();
            report.addEntry("listRemoveBegin", warmUp, tests, n, result);
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
        void listFullBrowseIterator(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    var iterator = list.iterator();
                    while (iterator.hasNext()) {
                        ctx.jitAssert(iterator.next());
                    }
                    return iterator;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("listFullBrowseIterator", warmUp, tests, n, result);
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
        void listFullBrowseForLoop(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .beforeTestCallback(() -> {
                    list.clear();
                    list.addAll(data);
                })
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase((in, ctx) -> {
                    for (Object o : list) {
                        ctx.jitAssert(o);
                    }
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("listFullBrowseForLoop", warmUp, tests, n, result);
        }
    }

    @Nested
    class ArrayListBenchmark extends CommonSteps {
        @Override
        List<Object> getList() {
            return new ArrayList<>();
        }
    }

    @Nested
    class LinkedListBenchmark extends CommonSteps {
        @Override
        List<Object> getList() {
            return new LinkedList<>();
        }
    }
}
