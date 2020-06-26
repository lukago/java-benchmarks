package run;

import benchmark.AvgTimeBenchmark;
import cache.Cache;
import cache.FIFOCache;
import cache.LFRUCache;
import cache.LRUCache;
import cache.RRCache;
import data.CsvRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import report.CacheRaportGenerator;

import java.time.Instant;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheBenchmarkSuite {

    private static final int DB_SIZE = 15000;
    private static final Random rnd = new Random();

    private static abstract class CommonSteps {

        private static Instant createdAt;

        protected CsvRepo csvRepo;
        protected CacheRaportGenerator report;

        @BeforeAll
        static void setUpAll() {
            createdAt = Instant.now();
        }

        @BeforeEach
        void setUp() {
            csvRepo = new CsvRepo();
            report = new CacheRaportGenerator(getCache().getClass(), createdAt);
        }

        @AfterEach
        void after() {
            report.write();
        }

        abstract Cache<Integer, String> getCache();

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheMissLoadTimeLinearRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomLinear())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse($ -> ctx.exclueResult(), () -> {
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheMissLoadTimeLinearRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheMissLoadTimeGaussianRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomGaussian())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse($ -> ctx.exclueResult(), () -> {
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheMissLoadTimeGaussianRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheHitLoadTimeLinearRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomLinear())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse(ctx::jitAssert, () -> {
                        ctx.exclueResult();
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheHitLoadTimeLinearRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheHitLoadTimeGaussianRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomGaussian())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse(ctx::jitAssert, () -> {
                        ctx.exclueResult();
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheHitLoadTimeGaussianRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheMissAndHitTimeGaussianRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomGaussian())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse(ctx::jitAssert, () -> {
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheMissAndHitTimeGaussianRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        @ParameterizedTest
        @CsvSource({
            "0,         1000",
            "10000,     1000",
        })
        void averageCacheMissAndHitLoadTimeLinearRandom(int warmUp, int tests) {
            var cache = getCache();
            var benchmark = new AvgTimeBenchmark.Builder<Integer, Object>()
                .testCaseIterations(tests)
                .warmUpIterations(warmUp)
                .afterWarmupCallback(cache::clearStats)
                .dataProvider(i -> randomLinear())
                .testCase((in, ctx) -> {
                    cache.load(in).ifPresentOrElse(ctx::jitAssert, () -> {
                        var value = csvRepo.load(in);
                        cache.cache(in, value);
                    });
                    return cache;
                })
                .build();

            var result = benchmark.run();

            assertThat(result).isNotNull();
            report.addEntry("averageCacheMissAndHitLoadTimeLinearRandom",
                warmUp,
                tests,
                cache.missCount(),
                cache.evictedCount(),
                result);
        }

        private Integer randomLinear() {
            return rnd.nextInt(DB_SIZE);
        }

        private Integer randomGaussian() {
            long index = Math.round(rnd.nextGaussian() * DB_SIZE/15 + DB_SIZE/2.0);
            if (index > DB_SIZE) {
                return DB_SIZE;
            }
            if (index < 0) {
                return 0;
            }
            return (int) index;
        }
    }

    @Nested
    class FIFOBenchmark extends CommonSteps {
        @Override
        Cache<Integer, String> getCache() {
            return new FIFOCache<>(DB_SIZE / 4);
        }
    }

    @Nested
    class LFRUBenchmark extends CommonSteps {
        @Override
        Cache<Integer, String> getCache() {
            return new LFRUCache<>(DB_SIZE / 4);
        }
    }

    @Nested
    class LRUBenchmark extends CommonSteps {
        @Override
        Cache<Integer, String> getCache() {
            return new LRUCache<>(DB_SIZE / 4);
        }
    }

    @Nested
    class RRenchmark extends CommonSteps {
        @Override
        Cache<Integer, String> getCache() {
            return new RRCache<>(DB_SIZE / 4);
        }
    }
}
