package run;

import lib.AvgTimeBenchmark;
import org.assertj.core.data.Percentage;
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

        abstract List<Object> getList();

        @ParameterizedTest
        @CsvSource({
            "0,         10",
            "0,         100",
            "0,         1000",
            "10,        10",
            "100,       100",
            "1000,      1000",
        })
        void listAddBegin(int warmUp, int tests) {
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase(in -> {
                    list.add(0, in);
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10",
            "0,         100",
            "0,         1000",
            "10,        10",
            "100,       100",
            "1000,      1000",
        })
        void listAddEnd(int warmUp, int tests) {
            var list = getList();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase(in -> {
                    list.add(in);
                    return list;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         10",
            "0,         100",
            "0,         1000",
            "10,        10",
            "100,       100",
            "1000,      1000",
        })
        void listAddRandom(int warmUp, int tests) {
            var list = getList();
            var input = TestObject.random();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, List<?>>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> rnd.nextInt(list.size()))
                .testCase(in -> {
                    list.add(in, input);
                    return list;
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
        void listRemoveRandomTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>(data);
            Collections.shuffle(stack);
            var list = getList();
            list.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase(list::remove)
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
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
        void listRemoveEndTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            list.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> list.remove(list.size() - 1))
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
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
        void listRemoveBeginTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            list.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> list.remove(0))
                .build();

            var result = benchmark.run();

            assertThat(list.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
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
        void listFullBrowseIteratorTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            list.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> {
                    var iterator = list.iterator();
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
        void listFullBrowseForLoopTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var list = getList();
            list.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> {
                    Object lookUp = null;
                    for (int i = 0; i < list.size(); i++) {
                        lookUp = list.get(i);
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
