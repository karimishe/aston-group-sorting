package output;
import collection.MyArrayList;
import contract.ResultWriter;
import model.Car;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class CarFileWriter implements ResultWriter<Car> {
    Scanner scanner = new Scanner(System.in);
    @Override
   public void append(MyArrayList<Car> items, Path path) throws IOException {
        Path filePath = path;

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        if (needsLeadingNewLine(filePath)) {
            appendText(filePath, System.lineSeparator());
        }

        for (int i = 0; i < items.size(); i++) {
            Car car = items.get(i);
            String line = String.format("%d;%s;%d",
                    car.getPower(), car.getModel(), car.getProductionYear());
            appendText(filePath, line + System.lineSeparator());
        }
    }
    private void appendText(Path filePath, String text) throws IOException {
        try (var writer = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(text);
        }
    }
    private boolean needsLeadingNewLine(Path filePath) throws IOException {
        if (Files.size(filePath) == 0) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            long length = raf.length();
            raf.seek(length - 1);
            int lastByte = raf.read();
            return lastByte != '\n';
        }
    }

}