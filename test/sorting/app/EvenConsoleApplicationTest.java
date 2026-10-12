package sorting.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sorting.contract.EvenSorter;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;

class EvenConsoleApplicationTest {
    @TempDir
    Path directory;

    private Path source;

    @BeforeEach
    void createSource() throws IOException {
        source = directory.resolve("cars.txt");
        Files.writeString(source, "8;Volvo;2020\n7;Лада;2019\n2;Kia;2022\n"
                + "3;Toyota;2021\n4;Audi;2018\n", StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void sortsEvenPowersWithEveryAlgorithmAndKeepsOriginal(int algorithm) throws IOException {
        String text = run(load() + "7\n" + algorithm + "\n4\n3\n0\n", new Modules());

        assertTrue(text.contains(lines("Результат сортировки (5):", "1. 2;Kia;2022",
                "2. 7;Лада;2019", "3. 4;Audi;2018", "4. 3;Toyota;2021", "5. 8;Volvo;2020")));
        assertTrue(text.contains(original()));
        assertFalse(text.contains("Способ сравнения:"));
    }

    @Test
    void reportsMissingCollection() throws IOException {
        String text = run("7\n1\n0\n", new Modules());

        assertTrue(text.contains("Сначала загрузите автомобили для сортировки."));
        assertFalse(text.contains("Отсортировано автомобилей:"));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void cancellingAlgorithmSelectionKeepsPreviousResult() throws IOException {
        String text = run(load() + "2\n1\n1\n7\n0\n4\n0\n", new Modules());

        assertTrue(text.contains(sortedAll()));
        assertFalse(text.contains("Чётные значения мощности расположены по возрастанию."));
    }

    @Test
    void unavailableSorterReturnsToMainMenu() throws IOException {
        Modules modules = new Modules() {
            @Override
            public EvenSorter<Car> evenSorter() {
                return null;
            }
        };
        String text = run("7\n0\n", modules);

        assertTrue(text.contains("7. Отсортировать автомобили с чётной мощностью (не реализовано)"));
        assertTrue(text.contains("Сортировка чётных значений пока не реализована."));
        assertFalse(text.contains("Алгоритм сортировки:"));
        assertTrue(text.contains("Работа программы завершена."));
    }

    @Test
    void savesEvenSortingResultThroughCommandSix() throws IOException {
        Path destination = directory.resolve("sorted.txt");

        String text = run(load() + "7\n1\n6\n" + destination + "\n0\n", new Modules());

        assertTrue(text.contains("Автомобили сохранены в файл: " + destination));
        assertEquals(Arrays.asList("2;Kia;2022", "7;Лада;2019", "4;Audi;2018",
                "3;Toyota;2021", "8;Volvo;2020"),
                Files.readAllLines(destination, StandardCharsets.UTF_8));
    }

    @Test
    void strategyFailureKeepsOriginalAndPreviousResult() throws IOException {
        Modules modules = new Modules() {
            @Override
            public SortStrategy<Car> selectionSort() {
                return (items, comparator) -> {
                    items.set(0, items.get(items.size() - 1));
                    throw new IllegalStateException("Ошибка стратегии");
                };
            }
        };

        String text = run(load() + "2\n1\n1\n7\n2\n3\n4\n0\n", modules);

        assertTrue(text.contains("Не удалось отсортировать автомобили: Ошибка стратегии"));
        assertTrue(text.contains(original()));
        assertTrue(text.contains(sortedAll()));
        assertFalse(text.contains("Чётные значения мощности расположены по возрастанию."));
        assertTrue(text.contains("Работа программы завершена."));
    }

    private String load() {
        return "1\n1\n" + source + "\n5\n";
    }

    private String original() {
        return lines("Исходная коллекция (5):", "1. 8;Volvo;2020", "2. 7;Лада;2019",
                "3. 2;Kia;2022", "4. 3;Toyota;2021", "5. 4;Audi;2018");
    }

    private String sortedAll() {
        return lines("Результат сортировки (5):", "1. 2;Kia;2022", "2. 3;Toyota;2021",
                "3. 4;Audi;2018", "4. 7;Лада;2019", "5. 8;Volvo;2020");
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
}
