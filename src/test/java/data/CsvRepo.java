package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvRepo {

    public String load(Integer key) {
        String filePath = CsvRepo.class.getClassLoader().getResource("values.csv").getPath();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                var arr = line.split(";");
                if (Integer.valueOf(arr[0].replace("\uFEFF", "")).equals(key)) {
                    return arr[1];
                }
            }

            throw new RuntimeException("Element not found: " + key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
