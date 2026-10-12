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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sorting.collection.MyArrayList;
import sorting.contract.ResultWriter;
import sorting.input.ConsoleInput;
import sorting.model.Car;

class FileOutputConsoleApplicationTest {
    private static final List<String> ORIGINAL_RECORDS = List.of(
            "150;Лада Веста;2020", "90;Renault;2014", "150;Kia;2022",
            "150;Kia;2018", "150;Kia;2018");
    private static final List<String> SORTED_RECORDS = List.of(
            "90;Renault;2014", "150;Kia;2018", "150;Kia;2018",
            "150;Kia;2022", "150;Лада Веста;2020");
    private static final String SORTED_DISPLAY = """
            Результат сортировки (5):
            1. 90;Renault;2014
            2. 150;Kia;2018
            3. 150;Kia;2018
            4. 150;Kia;2022
            5. 150;Лада Веста;2020
            """;

    @TempDir
    Path directory;

    private Path source;

    @BeforeEach
    void createInputFile() throws IOException {
        source = directory.resolve("cars.txt");
        Files.write(source, ORIGINAL_RECORDS, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Меню сохраняет отсортированные автомобили в новый файл UTF-8")
    void savesSortedCarsToNewFile() throws IOException {
        Path target = directory.resolve("result.txt");

        String text = run(loadAndSort() + "6\n" + target + "\n0\n");

        assertTrue(Files.isRegularFile(target));
        assertEquals(SORTED_RECORDS, Files.readAllLines(target, StandardCharsets.UTF_8));
        assertEquals(ORIGINAL_RECORDS, Files.readAllLines(source, StandardCharsets.UTF_8));
        assertTrue(text.contains("Автомобили сохранены в файл: " + target));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Повторное сохранение дописывает весь результат в тот же файл")
    void repeatedSaveAppendsSortedCars() throws IOException {
        Path target = directory.resolve("result.txt");

        String text = run(loadAndSort() + "6\n" + target + "\n6\n" + target + "\n0\n");

        List<String> expected = List.of(
                "90;Renault;2014", "150;Kia;2018", "150;Kia;2018",
                "150;Kia;2022", "150;Лада Веста;2020",
                "90;Renault;2014", "150;Kia;2018", "150;Kia;2018",
                "150;Kia;2022", "150;Лада Веста;2020");
        assertEquals(expected, Files.readAllLines(target, StandardCharsets.UTF_8));
        assertEquals(2, countOccurrences(text, "Автомобили сохранены в файл: " + target));
    }

    @Test
    @DisplayName("До сортировки сохранение не запрашивает путь и не вызывает запись")
    void doesNotAskForOutputPathBeforeSorting() throws IOException {
        RecordingWriter writer = new RecordingWriter();

        String text = run(load(source, 5) + "6\n0\n", modulesWithWriter(writer));

        assertEquals(0, writer.calls);
        assertTrue(text.contains("Результата сортировки пока нет."));
        assertEquals(1, countOccurrences(text, "Путь к файлу: "));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Сохранение использует переданный через Modules контракт записи")
    void usesWriterProvidedByModules() throws IOException {
        RecordingWriter writer = new RecordingWriter();
        Path target = directory.resolve("recorded.txt");

        String text = run(loadAndSort() + "6\n  " + target + "  \n0\n",
                modulesWithWriter(writer));

        assertEquals(1, writer.calls);
        assertEquals(target, writer.path);
        assertEquals(SORTED_RECORDS, records(writer.items));
        assertFalse(Files.exists(target));
        assertTrue(text.contains("Автомобили сохранены в файл: " + target));
    }

    @Test
    @DisplayName("Недоступный модуль записи возвращает в меню без запроса пути")
    void unavailableWriterKeepsResultWithoutAskingForPath() throws IOException {
        String text = run(loadAndSort() + "6\n4\n0\n", modulesWithWriter(null));

        assertTrue(text.contains("Запись результата в файл пока не реализована."));
        assertEquals(1, countOccurrences(text, "Путь к файлу: "));
        assertTrue(text.contains(SORTED_DISPLAY));
        assertFalse(text.contains("Автомобили сохранены в файл:"));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Ошибка записи сохраняет результат и позволяет продолжить работу")
    void writeFailureKeepsResultAndMenuRunning() throws IOException {
        RecordingWriter writer = new RecordingWriter();
        writer.failure = new IOException("Нет доступа к файлу.");
        Path target = directory.resolve("unwritten.txt");

        String text = run(loadAndSort() + "6\n" + target + "\n4\n0\n",
                modulesWithWriter(writer));

        assertEquals(1, writer.calls);
        assertTrue(text.contains("Ошибка при сохранении: Нет доступа к файлу."));
        assertTrue(text.contains(SORTED_DISPLAY));
        assertFalse(text.contains("Автомобили сохранены в файл:"));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Пустой путь отклоняется без вызова записи")
    void rejectsBlankOutputPath() throws IOException {
        RecordingWriter writer = new RecordingWriter();

        String text = run(loadAndSort() + "6\n   \n4\n0\n", modulesWithWriter(writer));

        assertEquals(0, writer.calls);
        assertTrue(text.contains("Путь к файлу не должен быть пустым."));
        assertTrue(text.contains(SORTED_DISPLAY));
        assertFalse(text.contains("Автомобили сохранены в файл:"));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Путь с символом NUL отклоняется без вызова записи")
    void rejectsInvalidOutputPath() throws IOException {
        RecordingWriter writer = new RecordingWriter();

        String text = run(loadAndSort() + "6\ninvalid\u0000path\n4\n0\n",
                modulesWithWriter(writer));

        assertEquals(0, writer.calls);
        assertTrue(text.contains("Некорректный путь к файлу."));
        assertTrue(text.contains(SORTED_DISPLAY));
        assertFalse(text.contains("Автомобили сохранены в файл:"));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    @Test
    @DisplayName("Загрузка новой коллекции запрещает сохранение устаревшего результата")
    void newLoadClearsPreviousResultBeforeSaving() throws IOException {
        Path replacement = directory.resolve("replacement.txt");
        Files.writeString(replacement, "80;Honda;2018\n", StandardCharsets.UTF_8);
        RecordingWriter writer = new RecordingWriter();

        String text = run(loadAndSort() + load(replacement, 1) + "6\n4\n3\n0\n",
                modulesWithWriter(writer));

        assertEquals(0, writer.calls);
        assertEquals(2, countOccurrences(text, "Результата сортировки пока нет."));
        assertEquals(2, countOccurrences(text, "Путь к файлу: "));
        assertTrue(text.contains("Исходная коллекция (1):\n1. 80;Honda;2018\n"));
        assertFalse(text.contains(SORTED_DISPLAY));
        assertTrue(text.endsWith("Работа программы завершена.\n"));
    }

    private String loadAndSort() {
        return load(source, 5) + "2\n1\n1\n";
    }

    private static String load(Path path, int length) {
        return "1\n1\n" + path + "\n" + length + "\n";
    }

    private static String run(String commands) throws IOException {
        return run(commands, new Modules());
    }

    private static String run(String commands, Modules modules) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            ConsoleInput input = new ConsoleInput(new StringReader(commands), stream);
            new ConsoleApplication(input, stream, modules).run();
        }
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static Modules modulesWithWriter(ResultWriter<Car> writer) {
        return new Modules() {
            @Override
            public ResultWriter<Car> carResultWriter() {
                return writer;
            }
        };
    }

    private static List<String> records(MyArrayList<Car> cars) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            result.add(car.getPower() + ";" + car.getModel() + ";" + car.getProductionYear());
        }
        return result;
    }

    private static int countOccurrences(String text, String fragment) {
        return (text.length() - text.replace(fragment, "").length()) / fragment.length();
    }

    private static final class RecordingWriter implements ResultWriter<Car> {
        private int calls;
        private MyArrayList<Car> items;
        private Path path;
        private IOException failure;

        @Override
        public void append(MyArrayList<Car> items, Path path) throws IOException {
            calls++;
            this.items = items.copy();
            this.path = path;
            if (failure != null) {
                throw failure;
            }
        }
    }
}
