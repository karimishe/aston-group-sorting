package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import app.ConsoleApplication;
import app.Modules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import collection.MyArrayList;
import contract.DataSource;
import contract.OccurrenceCounter;
import input.ConsoleInput;
import model.Car;

class CounterConsoleApplicationTest {
    @TempDir
    Path directory;

    private Path source;

    @BeforeEach
    void createSource() throws IOException {
        source = directory.resolve("cars.txt");
        Files.writeString(source, "200;Kia;2020\n150;Kia;2020\n150;Kia;2021\n"
                + "150;Lada;2020\n150;Kia;2020\n", StandardCharsets.UTF_8);
    }

    @Test
    void countsMatchingCarsAndKeepsOriginalAndSortedCollections() throws IOException {
        String text = run(loadAndSort() + countTarget()
                + "5\n150\nHonda\n2020\n3\n3\n4\n0\n", new Modules());

        assertTrue(text.contains("Найдено совпадений: 2"));
        assertTrue(text.contains("Найдено совпадений: 0"));
        assertTrue(text.contains(original()));
        assertTrue(text.contains(sorted()));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void passesOriginalCollectionTargetAndThreadCountToProvidedCounter() throws IOException {
        MyArrayList<Car> cars = new MyArrayList<>();
        cars.add(new Car.Builder().power(150).model("Kia").productionYear(2020).build());
        RecordingCounter counter = new RecordingCounter();
        counter.result = 7;
        Modules modules = new Modules() {
            @Override
            public DataSource<Car> fileSource(Path path) {
                return length -> cars;
            }

            @Override
            public OccurrenceCounter<Car> carCounter() {
                return counter;
            }
        };

        String text = run("1\n1\n" + source + "\n1\n5\n150\n Kia \n2020\n3\n0\n", modules);

        assertEquals(1, counter.calls);
        assertSame(cars, counter.items);
        assertEquals(150, counter.target.getPower());
        assertEquals("Kia", counter.target.getModel());
        assertEquals(2020, counter.target.getProductionYear());
        assertEquals(3, counter.threadCount);
        assertTrue(text.contains("Найдено совпадений: 7"));
    }

    @Test
    void reportsMissingCollectionWithoutAskingForTarget() throws IOException {
        Modules modules = withCounter((items, target, threadCount) -> {
            fail("До загрузки данных подсчёт вызываться не должен");
            return 0;
        });

        String text = run("5\n0\n", modules);

        assertTrue(text.contains("Исходная коллекция пока не загружена."));
        assertFalse(text.contains("Укажите мощность автомобиля:"));
        assertFalse(text.contains("Укажите кол-во потоков:"));
        assertFalse(text.contains("Найдено совпадений:"));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void invalidTargetReturnsToMainMenuAndAllowsAnotherAttempt() throws IOException {
        RecordingCounter counter = new RecordingCounter();
        counter.result = 2;

        String text = run(load() + "5\n150\n   \n2020\n2\n"
                + countTarget() + "0\n", withCounter(counter));

        assertTrue(text.contains("Некорректные данные:"));
        assertEquals(1, counter.calls, "Невалидная цель не передаётся модулю подсчёта");
        assertEquals("Kia", counter.target.getModel());
        assertTrue(text.contains("Найдено совпадений: 2"));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void retriesInvalidPowerYearAndThreadCount() throws IOException {
        RecordingCounter counter = new RecordingCounter();
        int year = LocalDate.now().getYear();

        String text = run(load() + "5\nтекст\n0\n150\nKia\n1899\n"
                + (year + 1) + "\n2020\n0\n256\n2\n0\n", withCounter(counter));

        assertTrue(text.contains("Введите целое число."));
        assertTrue(text.contains("Введите число от 1 до 2147483647."));
        assertTrue(text.contains("Введите число от 1900 до " + year + "."));
        assertTrue(text.contains("Введите число от 1 до 255."));
        assertEquals(1, counter.calls);
        assertEquals(150, counter.target.getPower());
        assertEquals(2020, counter.target.getProductionYear());
        assertEquals(2, counter.threadCount);
        assertTrue(text.contains("Найдено совпадений: 0"));
    }

    @Test
    void counterFailureKeepsDataAndReturnsToMainMenu() throws IOException {
        Modules modules = withCounter((items, target, threadCount) -> {
            throw new IllegalArgumentException("Ошибка подсчёта");
        });

        String text = run(loadAndSort() + countTarget() + "3\n4\n0\n", modules);

        assertTrue(text.contains("Некорректные данные: Ошибка подсчёта"));
        assertFalse(text.contains("Найдено совпадений:"));
        assertTrue(text.contains(original()));
        assertTrue(text.contains(sorted()));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void interruptionRestoresInterruptFlagWithoutReportingPartialResult() throws IOException {
        Modules modules = withCounter((items, target, threadCount) -> {
            throw new InterruptedException("Подсчёт прерван");
        });

        try {
            assertFalse(Thread.currentThread().isInterrupted());
            String text = run(loadAndSort() + countTarget() + "3\n4\n0\n", modules);

            assertTrue(Thread.currentThread().isInterrupted());
            assertTrue(text.contains("Подсчёт прерван."));
            assertFalse(text.contains("Найдено совпадений:"));
            assertTrue(text.contains(original()));
            assertTrue(text.contains(sorted()));
            assertTrue(text.contains("Работа программы завершена."));
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void propagatesEndOfInputAtEveryCounterPrompt() throws IOException {
        String[] partialInputs = {"5\n", "5\n150\n", "5\n150\nKia\n", "5\n150\nKia\n2020\n"};
        for (String commands : partialInputs) {
            RecordingCounter counter = new RecordingCounter();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
            ConsoleInput input = new ConsoleInput(new StringReader(load() + commands), output);

            assertThrows(EOFException.class,
                    () -> new ConsoleApplication(input, output, withCounter(counter)).run());

            String text = bytes.toString(StandardCharsets.UTF_8);
            assertEquals(0, counter.calls);
            assertFalse(text.contains("Найдено совпадений:"));
            assertFalse(text.contains("Работа программы завершена."));
        }
    }

    private Modules withCounter(OccurrenceCounter<Car> counter) {
        return new Modules() {
            @Override
            public OccurrenceCounter<Car> carCounter() {
                return counter;
            }
        };
    }

    private String load() {
        return "1\n1\n" + source + "\n5\n";
    }

    private String loadAndSort() {
        return load() + "2\n1\n1\n";
    }

    private String countTarget() {
        return "5\n150\nKia\n2020\n2\n";
    }

    private String original() {
        return lines("Исходная коллекция (5):", "1. 200;Kia;2020", "2. 150;Kia;2020",
                "3. 150;Kia;2021", "4. 150;Lada;2020", "5. 150;Kia;2020");
    }

    private String sorted() {
        return lines("Результат сортировки (5):", "1. 150;Kia;2020", "2. 150;Kia;2020",
                "3. 150;Kia;2021", "4. 150;Lada;2020", "5. 200;Kia;2020");
    }

    private String lines(String... values) {
        return String.join(System.lineSeparator(), values) + System.lineSeparator();
    }

    private String run(String commands, Modules modules) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        ConsoleInput input = new ConsoleInput(new StringReader(commands), output);
        new ConsoleApplication(input, output, modules).run();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static class RecordingCounter implements OccurrenceCounter<Car> {
        private int calls;
        private MyArrayList<Car> items;
        private Car target;
        private int threadCount;
        private int result;

        @Override
        public int count(MyArrayList<Car> items, Car target, int threadCount) {
            calls++;
            this.items = items;
            this.target = target;
            this.threadCount = threadCount;
            return result;
        }
    }
}
