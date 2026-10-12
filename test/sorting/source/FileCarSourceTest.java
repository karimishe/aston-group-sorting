package sorting.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sorting.collection.MyArrayList;
import sorting.model.Car;

class FileCarSourceTest {
    private static final String VALID_CONTENT =
            "150;Лада Веста;2020\n100;Ford;2000\n200;Volvo;2021\n";

    @TempDir
    Path directory;

    @Test
    void loadsRequestedCarsInFileOrderWithAllFieldsAndCyrillic() throws IOException {
        FileCarSource source = new FileCarSource(write(VALID_CONTENT));

        MyArrayList<Car> cars = source.load(3);

        assertEquals(3, cars.size());
        assertEquals(150, cars.get(0).getPower());
        assertEquals("Лада Веста", cars.get(0).getModel());
        assertEquals(2020, cars.get(0).getProductionYear());
        assertEquals("Ford", cars.get(1).getModel());
        assertEquals("Volvo", cars.get(2).getModel());
    }

    @Test
    void returnsOnlyRequestedPrefix() throws IOException {
        FileCarSource source = new FileCarSource(write(VALID_CONTENT));

        MyArrayList<Car> cars = source.load(2);

        assertEquals(2, cars.size());
        assertEquals("Лада Веста", cars.get(0).getModel());
        assertEquals("Ford", cars.get(1).getModel());
    }

    @Test
    void repeatedLoadsCreateIndependentCollectionsAndCars() throws IOException {
        FileCarSource source = new FileCarSource(write(VALID_CONTENT));

        MyArrayList<Car> first = source.load(3);
        MyArrayList<Car> second = source.load(3);

        assertNotSame(first, second);
        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            assertNotSame(first.get(i), second.get(i), "Повторная загрузка создаёт новый Car " + i);
        }
        first.set(0, first.get(1));
        assertEquals("Лада Веста", second.get(0).getModel());
    }

    @Test
    void loadingDoesNotChangeSourceFile() throws IOException {
        Path file = write(VALID_CONTENT);
        FileCarSource source = new FileCarSource(file);

        source.load(3);
        source.load(2);

        assertEquals(VALID_CONTENT, Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void rejectsNonPositiveLength() throws IOException {
        FileCarSource source = new FileCarSource(write(VALID_CONTENT));

        assertThrows(IllegalArgumentException.class, () -> source.load(0));
        assertThrows(IllegalArgumentException.class, () -> source.load(-1));
    }

    @Test
    void rejectsFileShorterThanRequestedLength() throws IOException {
        FileCarSource source = new FileCarSource(write(VALID_CONTENT));

        assertThrows(IllegalArgumentException.class, () -> source.load(4));
    }

    @Test
    void acceptsCrLfAndMissingFinalLineBreak() throws IOException {
        FileCarSource source = new FileCarSource(write("150;Kia;2020\r\n100;Ford;2000"));

        MyArrayList<Car> cars = source.load(2);

        assertEquals(2, cars.size());
        assertEquals("Ford", cars.get(1).getModel());
    }

    @Test
    void validatesLinesAfterRequestedPrefix() throws IOException {
        FileCarSource source = new FileCarSource(
                write("150;Kia;2020\n100;Ford;2000\n-1;Volvo;2021\n"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> source.load(1));

        assertTrue(exception.getMessage().startsWith("Строка 3: "));
    }

    @Test
    void rejectsEmptyLineWithItsNumber() throws IOException {
        FileCarSource source = new FileCarSource(write("150;Kia;2020\n\n100;Ford;2000\n"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> source.load(1));

        assertTrue(exception.getMessage().startsWith("Строка 2: "));
    }

    @Test
    void rejectsWhitespaceOnlyLineWithItsNumber() throws IOException {
        FileCarSource source = new FileCarSource(write("150;Kia;2020\n   \n"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> source.load(1));

        assertTrue(exception.getMessage().startsWith("Строка 2: "));
    }

    @Test
    void reportsNumberOfMalformedFirstLine() throws IOException {
        FileCarSource source = new FileCarSource(write("bad;Kia;2020\n"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> source.load(1));

        assertTrue(exception.getMessage().startsWith("Строка 1: "));
    }

    @Test
    void rejectsEmptyFile() throws IOException {
        FileCarSource source = new FileCarSource(write(""));

        assertThrows(IllegalArgumentException.class, () -> source.load(1));
    }

    @Test
    void reportsMalformedUtf8AsIOException() throws IOException {
        Path file = directory.resolve("cars.txt");
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});
        FileCarSource source = new FileCarSource(file);

        assertThrows(IOException.class, () -> source.load(1));
    }

    @Test
    void reportsMissingFileAsIOException() {
        FileCarSource source = new FileCarSource(directory.resolve("missing.txt"));

        assertThrows(IOException.class, () -> source.load(1));
    }

    @Test
    void reportsDirectoryInsteadOfFileAsIOException() {
        FileCarSource source = new FileCarSource(directory);

        assertThrows(IOException.class, () -> source.load(1));
    }

    @Test
    void rejectsNullPath() {
        assertThrows(NullPointerException.class, () -> new FileCarSource(null));
    }

    private Path write(String content) throws IOException {
        Path file = directory.resolve("cars.txt");
        return Files.writeString(file, content, StandardCharsets.UTF_8);
    }
}
