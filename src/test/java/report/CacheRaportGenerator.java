package report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.toList;

public class CacheRaportGenerator {

    private final List<Entry> entries;
    private final String filename;

    public CacheRaportGenerator(Class<?> testedClass, Instant createdAt) {
        this.entries = new ArrayList<>();
        this.filename = "results/cache/cache" + testedClass.getSimpleName() + createdAt + ".txt";
    }

    public void addEntry(String method, int warmup, int tests, int missed, int evicted, Duration duration) {
        entries.add(new Entry(method,
            warmup,
            tests,
            missed,
            tests - missed,
            (tests - missed) / (tests * 1.0) * 100.0,
            evicted,
            duration));
    }

    public void write() {
        try {
            Files.createDirectories(Paths.get("results/cache"));
            List<String> entriesStr = entries.stream().map(Entry::toString).collect(toList());
            Files.write(Paths.get(filename), entriesStr, defaultCharset(), APPEND, CREATE);
        } catch (IOException e) {
            throw new RuntimeException("IO error", e);
        }
    }

    private static class Entry {
        private final String method;
        private final int warmup;
        private final int tests;
        private final int missed;
        private final int hit;
        private final double hitPercentage;
        private final int evicted;
        private final Duration avgReadTime;

        public Entry(String method,
            int warmup,
            int tests,
            int missed,
            int hit,
            double hitPercentage,
            int evicted,
            Duration averageCacheLoadTime) {
            this.method = method;
            this.warmup = warmup;
            this.tests = tests;
            this.missed = missed;
            this.hit = hit;
            this.hitPercentage = hitPercentage;
            this.evicted = evicted;
            this.avgReadTime = averageCacheLoadTime;
        }

        @Override
        public String toString() {
            return "{" +
                "\nmethod='" + method + '\'' +
                "\nwarmup=" + warmup +
                "\ntests=" + tests +
                "\nmissed=" + missed +
                "\nhit=" + hit +
                "\nhitPercentage=" + hitPercentage +
                "\nevicted=" + evicted +
                "\navgReadTime=" + avgReadTime +
                "\n}";
        }
    }
}
