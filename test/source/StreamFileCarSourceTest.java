package source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import collection.MyArrayList;
import model.Car;
import source.StreamFileCarSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamFileCarSourceTest {
    @TempDir
    Path directory;

    @Test
    void returnsExactlyRequestedPrefixAndParsesInsideStream() throws IOException {
        Path file = directory.resolve("cars.txt");
        Files.writeString(file, "150;Kia;2020\n100;Ford;2000\n200;Volvo;2021\n");

        StreamFileCarSource source = new StreamFileCarSource(file);
        MyArrayList<Car> exactLength = source.load(3);
        MyArrayList<Car> cars = source.load(2);

        assertEquals(3, exactLength.size());
        assertEquals("Volvo", exactLength.get(2).getModel());
        assertEquals(2, cars.size());
        assertEquals("Kia", cars.get(0).getModel());
        assertEquals("Ford", cars.get(1).getModel());
        assertEquals(150, cars.get(0).getPower());
        assertNotSame(cars, source.load(2));
    }

    @Test
    void validatesLinesAfterRequestedPrefix() throws IOException {
        Path file = directory.resolve("cars-invalid-tail.txt");
        Files.writeString(file, "150;Kia;2020\n-1;Ford;2021\n");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new StreamFileCarSource(file).load(1));

        assertTrue(exception.getMessage().startsWith("Строка 2:"));
    }

    @Test
    void rejectsMalformedFirstLine() throws IOException {
        Path file = directory.resolve("cars-invalid.txt");
        Files.writeString(file, "bad;Kia;2020\n");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new StreamFileCarSource(file).load(1));

        assertTrue(exception.getMessage().startsWith("Строка 1:"));
    }

    @Test
    void rejectsFileShorterThanRequestedLength() throws IOException {
        Path file = directory.resolve("cars-short.txt");
        Files.writeString(file, "150;Kia;2020\n");

        assertThrows(IllegalArgumentException.class,
                () -> new StreamFileCarSource(file).load(2));
    }

    @Test
    void rejectsNonPositiveLength() {
        StreamFileCarSource source = new StreamFileCarSource(directory.resolve("unused.txt"));
        assertThrows(IllegalArgumentException.class, () -> source.load(0));
        assertThrows(IllegalArgumentException.class, () -> source.load(-1));
    }

    @Test
    void wrapsMissingFileAsIOException() {
        Path missing = directory.resolve("missing.txt");
        assertThrows(IOException.class, () -> new StreamFileCarSource(missing).load(1));
    }

    @Test
    void rejectsInvalidUtf8() throws IOException {
        Path file = directory.resolve("cars-invalid-encoding.txt");
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});

        assertThrows(IOException.class, () -> new StreamFileCarSource(file).load(1));
    }

    @Test
    void acceptsCyrillicCrLfAndMissingFinalLineBreak() throws IOException {
        Path file = directory.resolve("cars.txt");
        String content = "150;Лада Веста;2020\r\n100;Ford;2000";
        Files.writeString(file, content);

        MyArrayList<Car> cars = new StreamFileCarSource(file).load(2);

        assertEquals(2, cars.size());
        assertEquals("Лада Веста", cars.get(0).getModel());
        assertEquals("Ford", cars.get(1).getModel());
        assertEquals(content, Files.readString(file));
    }

    @Test
    void rejectsEmptyFile() throws IOException {
        Path file = directory.resolve("empty.txt");
        Files.writeString(file, "");

        assertThrows(IllegalArgumentException.class, () -> new StreamFileCarSource(file).load(1));
    }

    @Test
    void reportsEmptyAndWhitespaceOnlyLinesAfterRequestedPrefix() throws IOException {
        Path file = directory.resolve("cars.txt");
        for (String blank : new String[] {"", "   "}) {
            Files.writeString(file, "150;Kia;2020\n" + blank + "\n100;Ford;2000\n");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new StreamFileCarSource(file).load(1));

            assertTrue(exception.getMessage().startsWith("Строка 2:"));
        }
    }
}
