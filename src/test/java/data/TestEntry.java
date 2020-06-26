package data;

import java.util.Objects;

public class TestEntry {
    private final Integer key;
    private final String value;

    public TestEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TestEntry testEntry = (TestEntry) o;
        return Objects.equals(key, testEntry.key) &&
            Objects.equals(value, testEntry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
