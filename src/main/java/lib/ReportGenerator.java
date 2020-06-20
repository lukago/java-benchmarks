package lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.toList;

public class ReportGenerator {

    private final Class<?> testedClass;
    private final List<Entry> entries;

    public ReportGenerator(Class<?> testedClass) {
        this.testedClass = testedClass;
        this.entries = new ArrayList<>();
    }

    public void addEntry(String method, int warmup, int test, int n, Duration duration) {
        entries.add(new Entry(warmup, test, n, duration, method));
    }

    public void write() {
        String filename = "benchmark" + testedClass.getSimpleName() + ".txt";
        List<String> entriesStr = entries.stream().map(Entry::toString).collect(toList());
        try {
            Files.write(Paths.get(filename), entriesStr, defaultCharset(), APPEND, CREATE);
        } catch (IOException e) {
            throw new RuntimeException("IO error", e);
        }
    }

    private static class Entry {
        final int warmupIterations;
        final int testIterations;
        final int collectionSize;
        final Duration avgTime;
        final String testedMethod;

        public Entry(int warmupIterations,
            int testIterations,
            int collectionSize,
            Duration avgTime,
            String testedMethod) {
            this.warmupIterations = warmupIterations;
            this.testIterations = testIterations;
            this.collectionSize = collectionSize;
            this.avgTime = avgTime;
            this.testedMethod = testedMethod;
        }

        @Override
        public String toString() {
            return "{" +
                "\nwarmupIterations = " + warmupIterations +
                "\ntestIterations = " + testIterations +
                "\ncollectionSize = " + collectionSize +
                "\navgTime = " + avgTime +
                "\ntestedMethod = '" + testedMethod + '\'' +
                "\n}";
        }
    }
}
