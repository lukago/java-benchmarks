package run;

import lib.AvgTimeBenchmark;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class SetBenchmarkSuite {

    private static abstract class CommonSteps {

        abstract Set<Object> getSet();

        @ParameterizedTest
        @CsvSource({
            "0,         10",
            "0,         100",
            "0,         1000",
            "10,        10",
            "100,       100",
            "1000,      1000",
        })
        void setAddTest(int warmUp, int tests) {
            var set = getSet();
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> TestObject.random())
                .testCase(set::add)
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
        void setRemoveTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>(data);
            Collections.shuffle(stack);
            var set = getSet();
            set.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase(set::remove)
                .build();

            var result = benchmark.run();

            assertThat(set.size()).isCloseTo(n - warmUp - tests, Percentage.withPercentage(0.5));
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
        void setFullBrowseTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var set = getSet();
            set.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .testCase(in -> {
                    var iterator = set.iterator();
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
        void setExistsTest(int warmUp, int tests, int n) {
            var data = TestObject.randomList(n);
            var stack = new LinkedList<>(data);
            Collections.shuffle(stack);
            var set = getSet();
            set.addAll(data);
            var benchmark = new AvgTimeBenchmark.Builder<>()
                .warmUpIterations(warmUp)
                .testCaseIterations(tests)
                .dataProvider(i -> stack.pop())
                .testCase(set::contains)
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            System.out.println(result);
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

