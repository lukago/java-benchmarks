package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;
import java.util.UUID;

public class TestObject implements Comparable<TestObject> {

    private final static Random rn = new Random();

    private final String testString;
    private final Integer testInt;
    private final List<String> testList;

    private TestObject(String testString, Integer testInt, List<String> testList) {
        this.testString = testString;
        this.testInt = testInt;
        this.testList = testList;
    }

    public static TestObject random() {
        return new TestObject(UUID.randomUUID().toString(),
            rn.nextInt(Integer.MAX_VALUE),
            List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
    }

    public static List<TestObject> randomList(int n) {
        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(random());
        }
        return list;
    }

    @Override
    public int compareTo(TestObject o) {
        return testString.compareTo(o.testString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TestObject that = (TestObject) o;
        return Objects.equals(testString, that.testString) &&
            Objects.equals(testInt, that.testInt) &&
            Objects.equals(testList, that.testList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testString, testInt, testList);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TestObject.class.getSimpleName() + "[", "]")
            .add("testString='" + testString + "'")
            .add("testInt=" + testInt)
            .add("testList=" + testList)
            .toString();
    }
}
