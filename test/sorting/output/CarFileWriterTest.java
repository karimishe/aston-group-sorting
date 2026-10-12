package sorting.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sorting.collection.MyArrayList;
import sorting.model.Car;
import sorting.source.StreamFileCarSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarFileWriterTest {
    private static final String NEW_LINE = System.lineSeparator();

    @TempDir
    Path directory;

    private final CarFileWriter writer = new CarFileWriter();

    @Test
    void createsFileWithUtf8Records() throws IOException {
        Path path = directory.resolve("cars.txt");
        MyArrayList<Car> cars = cars(car(120, "Лада Веста", 2022), car(150, "Kia", 2020));

        writer.append(cars, path);

        String expected = "120;Лада Веста;2022" + NEW_LINE + "150;Kia;2020" + NEW_LINE;
        assertArrayEquals(expected.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(path));
    }

    @Test
    void appendsTwiceWithoutReplacingExistingContent() throws IOException {
        Path path = directory.resolve("cars.txt");
        String previousContent = "Существующие записи" + NEW_LINE;
        Files.writeString(path, previousContent, StandardCharsets.UTF_8);

        writer.append(cars(car(100, "Audi", 2018)), path);
        writer.append(cars(car(150, "Kia", 2020)), path);

        assertEquals(previousContent + "100;Audi;2018" + NEW_LINE + "150;Kia;2020" + NEW_LINE,
                Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void separatesNewRecordsWhenFileHasNoFinalNewline() throws IOException {
        Path path = directory.resolve("cars.txt");
        Files.writeString(path, "100;Audi;2018", StandardCharsets.UTF_8);

        writer.append(cars(car(150, "Kia", 2020)), path);

        assertEquals("100;Audi;2018" + NEW_LINE + "150;Kia;2020" + NEW_LINE,
                Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void preservesCrLfWithoutAddingAnEmptyLine() throws IOException {
        Path path = directory.resolve("cars.txt");
        Files.writeString(path, "100;Audi;2018\r\n", StandardCharsets.UTF_8);

        writer.append(cars(car(150, "Kia", 2020)), path);

        assertEquals("100;Audi;2018\r\n150;Kia;2020" + NEW_LINE,
                Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void savedCarsCanBeLoadedByFileSource() throws IOException {
        Path path = directory.resolve("cars.txt");
        MyArrayList<Car> original = cars(car(120, "Лада Веста", 2022), car(150, "Kia", 2020));

        writer.append(original, path);
        MyArrayList<Car> loaded = new StreamFileCarSource(path).load(2);

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).getPower(), loaded.get(i).getPower());
            assertEquals(original.get(i).getModel(), loaded.get(i).getModel());
            assertEquals(original.get(i).getProductionYear(), loaded.get(i).getProductionYear());
        }
    }

    @Test
    void preservesCollectionOrderAndDuplicates() throws IOException {
        Car first = car(150, "Kia", 2020);
        Car second = car(100, "Audi", 2018);
        MyArrayList<Car> cars = cars(first, second, first);
        Path path = directory.resolve("cars.txt");

        writer.append(cars, path);

        assertEquals(3, cars.size());
        assertSame(first, cars.get(0));
        assertSame(second, cars.get(1));
        assertSame(first, cars.get(2));
        assertEquals("150;Kia;2020" + NEW_LINE + "100;Audi;2018" + NEW_LINE
                + "150;Kia;2020" + NEW_LINE, Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void acceptsEmptyCollectionForNewFile() throws IOException {
        Path path = directory.resolve("empty.txt");

        writer.append(new MyArrayList<>(), path);

        assertEquals(0L, Files.size(path));
    }

    @Test
    void emptyCollectionPreservesExistingRecords() throws IOException {
        Path path = directory.resolve("cars.txt");
        String previousContent = "100;Audi;2018" + NEW_LINE;
        Files.writeString(path, previousContent, StandardCharsets.UTF_8);

        writer.append(new MyArrayList<>(), path);

        assertEquals(previousContent, Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void rejectsDirectoryAsOutputFile() {
        assertThrows(IOException.class,
                () -> writer.append(cars(car(150, "Kia", 2020)), directory));
    }

    @Test
    void reportsMissingParentDirectory() {
        Path path = directory.resolve("missing").resolve("cars.txt");

        assertThrows(IOException.class,
                () -> writer.append(cars(car(150, "Kia", 2020)), path));
    }

    @Test
    void rejectsNullCollection() {
        assertThrows(NullPointerException.class,
                () -> writer.append(null, directory.resolve("cars.txt")));
    }

    @Test
    void rejectsNullPath() {
        assertThrows(NullPointerException.class,
                () -> writer.append(cars(car(150, "Kia", 2020)), null));
    }

    private MyArrayList<Car> cars(Car... items) {
        MyArrayList<Car> result = new MyArrayList<>();
        for (Car item : items) {
            result.add(item);
        }
        return result;
    }

    private Car car(int power, String model, int year) {
        return new Car.Builder().power(power).model(model).productionYear(year).build();
    }
}
