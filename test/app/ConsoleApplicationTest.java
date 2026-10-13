package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import app.ConsoleApplication;
import app.Modules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import contract.DataSource;
import contract.SortStrategy;
import input.ConsoleInput;
import model.Car;

class ConsoleApplicationTest {
    private static final String[] ORIGINAL = {
        "150;Kia;2022", "200;Audi;2010", "150;Audi;2022",
        "100;Volvo;2022", "150;Kia;2018", "150;Kia;2018"
    };
    private static final String[] SORTED = {
        "100;Volvo;2022", "150;Audi;2022", "150;Kia;2018",
        "150;Kia;2018", "150;Kia;2022", "200;Audi;2010"
    };

    @TempDir
    Path directory;

    private Path cars;
    private Path replacement;
    private Path invalid;

    @BeforeEach
    void setUpPaths() {
        cars = directory.resolve("cars.txt");
        replacement = directory.resolve("replacement.txt");
        invalid = directory.resolve("invalid.txt");
    }

    @Test
    void exitsWithoutReadingFurtherInputOrClosingReader() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader("0\n1\n"), stream);
        new ConsoleApplication(input, stream, new Modules()).run();
        String text = output.toString("UTF-8");
        assertTrue(text.startsWith("Приложение сортировки автомобилей"), "Показано название программы");
        assertEquals(1, countLines(text, "Меню:"), "Перед немедленным выходом меню показано один раз");
        assertTrue(text.endsWith("Работа программы завершена." + System.lineSeparator()),
                "Ноль завершает программу с сообщением");
        assertEquals("1", input.readLine(""), "После выхода ввод не читается и Reader не закрывается");
    }

    @Test
    void reportsMissingDataAndKeepsMainMenuRunning() throws IOException {
        String text = run("2\n1\n3\n4\n0\n");
        assertEquals(4, countLines(text, "Меню:"), "После каждой команды можно выбрать следующую");
        assertEquals(1, countLines(text, "Сначала загрузите автомобили для сортировки."),
                "Сортировка без данных предлагает сначала загрузить коллекцию");
        assertFalse(text.contains("Способ сравнения:"),
                "До загрузки данных приложение не запрашивает способ сравнения");
        assertEquals(1, countLines(text, "Исходная коллекция пока не загружена."),
                "До загрузки отсутствует исходная коллекция");
        assertEquals(1, countLines(text, "Результата сортировки пока нет."),
                "До сортировки отсутствует результат");
        assertEquals(1, countLines(text, "Работа программы завершена."), "Выход выполняется ровно один раз");
    }

    @Test
    void retriesInvalidMainMenuInput() throws IOException {
        String text = run("\nтекст\n1.5\n2147483648\n-1\n8\n 0 \n");
        assertEquals(1, countLines(text, "Меню:"), "Ошибочный выбор повторяет ввод без нового меню");
        assertEquals(4, countLines(text, "Введите целое число."), "Нечисловой ввод и переполнение обработаны");
        assertEquals(2, countLines(text, "Введите число от 0 до 7."), "Несуществующие команды обработаны");
        assertEquals(1, countLines(text, "Работа программы завершена."), "После ошибок можно выйти");
    }

    @Test
    void propagatesEndOfInputAtMainAndSubmenuPrompts() throws IOException {
        assertEndOfInput("");
        assertEndOfInput("ошибка\n");
        assertEndOfInput("1\n");
        assertEndOfInput("2\n");
        assertEndOfInput("1\n1\n");
        assertEndOfInput("1\n1\ncars.txt\n");
        assertEndOfInput("1\nошибка\n");
        assertEndOfInput("2\n4\n");
    }

    @Test
    void unavailableModulesAndCancellationReturnToMainMenu() throws IOException {
        String text = run("1\n2\n1\n3\n2\n2\n2\n3\n1\n0\n2\n0\n3\n4\n0\n",
                new UnavailableModules());
        assertEquals(3, countLines(text, "2. Вручную (не реализовано)"),
                "Ручной источник отмечен как не реализованный");
        assertEquals(3, countLines(text, "3. Случайные данные (не реализовано)"),
                "Случайный источник отмечен как не реализованный");
        assertEquals(3, countLines(text, "2. Выбором (не реализовано)"),
                "Сортировка выбором отмечена как не реализованная");
        assertEquals(3, countLines(text, "3. Пузырьком (не реализовано)"),
                "Пузырьковая сортировка отмечена как не реализованная");
        assertEquals(1, countLines(text, "Ручной ввод пока не реализован."),
                "Выбор ручного ввода сообщает об отсутствии реализации");
        assertEquals(1, countLines(text, "Случайное заполнение пока не реализовано."),
                "Выбор случайного источника сообщает об отсутствии реализации");
        assertEquals(1, countLines(text, "Сортировка выбором пока не реализована."),
                "Выбор сортировки выбором сообщает об отсутствии реализации даже без коллекции");
        assertEquals(1, countLines(text, "Пузырьковая сортировка пока не реализована."),
                "Выбор пузырьковой сортировки сообщает об отсутствии реализации даже без коллекции");
        assertEquals(9, countLines(text, "Меню:"),
                "Неготовые пункты и ноль в обоих подменю возвращают в главное меню");
        assertTrue(!text.contains("Путь к файлу:") && !text.contains("Количество автомобилей:"),
                "Неготовые источники и отмена не запрашивают путь или количество");
        assertTrue(!text.contains("Сначала загрузите автомобили для сортировки.")
                && !text.contains("Отсортировано автомобилей:"),
                "Неготовые алгоритмы и отмена не запускают доступную сортировку");
        assertEquals(1, countLines(text, "Исходная коллекция пока не загружена."),
                "Неготовые источники не создают коллекцию");
        assertEquals(1, countLines(text, "Результата сортировки пока нет."),
                "Неготовые алгоритмы не создают результат");
        assertEquals(1, countLines(text, "Работа программы завершена."),
                "Программу завершает только ноль в главном меню");
    }

    @Test
    void retriesInvalidSourceAndAlgorithmChoices() throws IOException {
        String text = run("1\nтекст\n-1\n4\n0\n2\n1.5\n-1\n4\n0\n0\n");
        assertEquals(2, countLines(text, "Введите целое число."),
                "Оба подменю повторяют запрос после нецелого ввода");
        assertEquals(4, countLines(text, "Введите число от 0 до 3."),
                "Оба подменю проверяют нижнюю и верхнюю границы выбора");
        assertTrue(countLines(text, "Источник данных:") == 1
                && countLines(text, "Алгоритм сортировки:") == 1,
                "Ошибочный ввод повторяет запрос внутри текущего подменю");
        assertEquals(3, countLines(text, "Меню:"),
                "Ошибочный выбор в подменю не возвращает в главное меню");
        assertEquals(1, countLines(text, "Работа программы завершена."),
                "После ошибок и возврата из подменю можно завершить программу");
    }

    @Test
    void loadsRequestedNumberOfCarsAndTrimsPath() throws IOException {
        createLoadingFiles();
        String text = run("1\n1\n  " + cars + "  \n2\n3\n0\n");
        assertEquals(1, countLines(text, "Загружено автомобилей: 2"),
                "Загружается запрошенное количество, пробелы вокруг пути удаляются");
        assertEquals(1, countLines(text, "Исходная коллекция (2):"),
                "Показана длина загруженной коллекции");
        assertEquals(1, countLines(text, "1. 150;Kia;2020"), "Показан первый автомобиль");
        assertEquals(1, countLines(text, "2. 100;Lada;2010"), "Показан второй автомобиль");
        assertEquals(0, countLines(text, "3. 200;Toyota;2022"),
                "Автомобили за пределами выбранной длины не добавляются");
        assertEquals(3, countLines(text, "Меню:"), "После загрузки и просмотра работа продолжается");
    }

    @Test
    void successfulLoadReplacesOriginalCollection() throws IOException {
        createLoadingFiles();
        String text = run("1\n1\n" + cars + "\n2\n1\n1\n" + replacement + "\n1\n3\n0\n");
        assertEquals(1, countLines(text, "Загружено автомобилей: 1"),
                "Повторная успешная загрузка сообщается пользователю");
        assertEquals(1, countLines(text, "Исходная коллекция (1):"),
                "Новая коллекция заменяет предыдущую");
        assertEquals(1, countLines(text, "1. 80;Honda;2018"), "Показаны новые данные");
        assertEquals(0, countLines(text, "1. 150;Kia;2020"), "Старые данные не сохраняются при замене");
    }

    @Test
    void failedLoadsPreserveOriginalCollection() throws IOException {
        createLoadingFiles();
        String text = run("1\n1\n" + cars + "\n1\n"
                + "1\n1\n" + invalid + "\n1\n3\n"
                + "1\n1\n" + directory.resolve("missing.txt") + "\n1\n3\n"
                + "1\n1\ninvalid\u0000path\n3\n0\n");
        assertEquals(1, countLines(text, "Загружено автомобилей: 1"),
                "Ошибочные загрузки не выдаются за успешные");
        assertEquals(1, countLines(text, "Не удалось загрузить файл: Строка 2: "
                + "Мощность должна быть положительным числом."),
                "Ошибка файла указывает причину и номер плохой строки");
        assertEquals(3, countLines(text, "Исходная коллекция (1):"),
                "После каждой ошибки сохраняется прежняя длина коллекции");
        assertEquals(3, countLines(text, "1. 150;Kia;2020"),
                "Плохая строка после выбранного количества, отсутствующий файл и плохой путь не теряют данные");
        assertEquals(1, countLines(text, "Некорректный путь к файлу."),
                "Некорректный путь обрабатывается без завершения программы");
    }

    @Test
    void retriesInvalidCollectionLength() throws IOException {
        createLoadingFiles();
        String text = run("1\n1\n" + cars + "\nтекст\n0\n-1\n2147483648\n2\n3\n0\n");
        assertEquals(2, countLines(text, "Введите целое число."),
                "Нечисловая длина и переполнение вызывают повторный запрос");
        assertEquals(2, countLines(text, "Введите число от 1 до 2147483647."),
                "Нулевая и отрицательная длина вызывают повторный запрос");
        assertEquals(1, countLines(text, "Исходная коллекция (2):"),
                "После ошибок длины коллекция успешно загружается");
    }

    @Test
    void rejectsBlankInputPath() throws IOException {
        createLoadingFiles();
        String text = run("1\n1\n   \n3\n0\n");
        assertEquals(1, countLines(text, "Путь к файлу не должен быть пустым."),
                "Пустой путь сопровождается понятным сообщением");
        assertEquals(1, countLines(text, "Исходная коллекция пока не загружена."),
                "После пустого пути выполняется следующая команда меню без запроса длины");
    }

    @Test
    void sortsRepeatedlyByAllFieldsWithoutChangingOriginal() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\n1\n4\n3\n2\n1\n1\n4\n0\n");
        assertEquals(2, countLines(text, "Отсортировано автомобилей: 6"),
                "Одну коллекцию можно сортировать повторно");
        assertEquals(2, countCollections(text, "Результат сортировки", SORTED),
                "Обе сортировки учитывают мощность, модель и год и сохраняют повторяющиеся автомобили");
        assertEquals(1, countCollections(text, "Исходная коллекция", ORIGINAL),
                "Сортировка не меняет исходный порядок автомобилей");
        assertEquals(7, countLines(text, "Меню:"), "После сортировки и просмотра меню продолжает работать");
    }

    @Test
    void newLoadClearsPreviousSortingResult() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\n1\n1\n1\n" + replacement
                + "\n1\n4\n3\n2\n1\n1\n4\n0\n");
        assertEquals(1, countLines(text, "Результата сортировки пока нет."),
                "Успешная загрузка новой коллекции сбрасывает предыдущий результат");
        assertEquals(0, countCollections(text, "Результат сортировки", SORTED),
                "После замены коллекции старый результат не показывается");
        assertEquals(1, countCollections(text, "Исходная коллекция", "80;Honda;2018"),
                "После замены показываются новые исходные данные");
        assertEquals(1, countLines(text, "Отсортировано автомобилей: 1"),
                "Сортировка использует вновь загруженную коллекцию");
        assertEquals(1, countCollections(text, "Результат сортировки", "80;Honda;2018"),
                "Коллекция из одного автомобиля успешно сортируется");
    }

    @Test
    void failedLoadsPreservePreviousSortingResult() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\n1\n"
                + "1\n1\n" + invalid + "\n1\n3\n4\n"
                + "1\n1\n" + directory.resolve("missing.txt") + "\n1\n3\n4\n"
                + "1\n1\n" + replacement + "\n2\n3\n4\n0\n");
        assertEquals(1, countLines(text, "Загружено автомобилей: 6"),
                "Перед ошибками загружена исходная коллекция");
        assertEquals(1, countLines(text, "Не удалось загрузить файл: Строка 2: "
                + "Мощность должна быть положительным числом."),
                "Плохая строка за пределами запрошенной длины отклоняет загрузку");
        assertEquals(1, countLines(text, "Не удалось загрузить файл: Файл не найден: "
                + directory.resolve("missing.txt")),
                "После сортировки отсутствие файла обрабатывается как ошибка загрузки");
        assertEquals(1, countLines(text, "Не удалось загрузить файл: В файле недостаточно автомобилей: "
                + "требуется 2, найдено 1."),
                "Недостаточное количество автомобилей отклоняет загрузку");
        assertEquals(3, countCollections(text, "Исходная коллекция", ORIGINAL),
                "Невалидный, отсутствующий и короткий файлы сохраняют исходную коллекцию");
        assertEquals(3, countCollections(text, "Результат сортировки", SORTED),
                "Каждая ошибочная загрузка сохраняет последний результат сортировки");
        assertEquals(0, countLines(text, "Результата сортировки пока нет."),
                "Ошибка загрузки не сбрасывает готовый результат");
    }

    @Test
    void unavailableSourcesAndAlgorithmsPreserveBothCollections() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\n1\n"
                + "1\n2\n3\n4\n"
                + "1\n3\n3\n4\n"
                + "1\n0\n3\n4\n"
                + "2\n2\n3\n4\n"
                + "2\n3\n3\n4\n"
                + "2\n0\n3\n4\n0\n", new UnavailableModules());
        assertEquals(6, countCollections(text, "Исходная коллекция", ORIGINAL),
                "Неготовые источники, алгоритмы и отмена сохраняют исходную коллекцию");
        assertEquals(6, countCollections(text, "Результат сортировки", SORTED),
                "Неготовые источники, алгоритмы и отмена сохраняют последний результат");
        assertEquals(1, countLines(text, "Загружено автомобилей: 6"),
                "Неготовые пункты не выполняют повторную загрузку");
        assertEquals(1, countLines(text, "Отсортировано автомобилей: 6"),
                "Неготовые пункты не выполняют повторную сортировку");
    }

    @Test
    void unavailableComparatorsAndCancellationPreserveBothCollections() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\n1\n"
                + "2\n1\n2\n3\n4\n"
                + "2\n1\n3\n3\n4\n"
                + "2\n1\n4\n3\n4\n"
                + "2\n1\n0\n3\n4\n0\n", new UnavailableModules());
        assertEquals(5, countLines(text, "1. По всем трём полям"),
                "Сравнение по трём полям доступно в каждом меню сравнения");
        assertEquals(5, countLines(text, "2. По мощности (не реализовано)"),
                "Неготовое сравнение по мощности отмечено в меню");
        assertEquals(5, countLines(text, "3. По модели (не реализовано)"),
                "Неготовое сравнение по модели отмечено в меню");
        assertEquals(5, countLines(text, "4. По году выпуска (не реализовано)"),
                "Неготовое сравнение по году отмечено в меню");
        assertEquals(3, countLines(text, "Этот способ сравнения пока не реализован."),
                "Каждый неготовый компаратор сопровождается сообщением");
        assertEquals(4, countCollections(text, "Исходная коллекция", ORIGINAL),
                "Неготовые компараторы и отмена сохраняют исходную коллекцию");
        assertEquals(4, countCollections(text, "Результат сортировки", SORTED),
                "Неготовые компараторы и отмена сохраняют результат");
        assertEquals(1, countLines(text, "Отсортировано автомобилей: 6"),
                "Неготовый компаратор не заменяется сравнением по умолчанию");
        assertEquals(1, countLines(text, "Работа программы завершена."),
                "Ноль в выборе компаратора возвращает в главное меню");
    }

    @Test
    void retriesInvalidComparatorChoice() throws IOException {
        createSortingFiles();
        String text = run("1\n1\n" + cars + "\n6\n2\n1\nтекст\n-1\n5\n1\n4\n0\n");
        assertEquals(1, countLines(text, "Введите целое число."),
                "Выбор компаратора повторяется после нечислового ввода");
        assertEquals(2, countLines(text, "Введите число от 0 до 4."),
                "Выбор компаратора проверяет обе границы");
        assertEquals(1, countLines(text, "Способ сравнения:"),
                "Ошибочный ввод повторяет запрос внутри меню сравнения");
        assertEquals(1, countCollections(text, "Результат сортировки", SORTED),
                "После ошибок выбора выполняется выбранная сортировка");
    }

    @Test
    void propagatesEndOfInputWhileChoosingComparator() throws IOException {
        createSortingFiles();
        assertEndOfInput("1\n1\n" + cars + "\n6\n2\n1\n");
        assertEndOfInput("1\n1\n" + cars + "\n6\n2\n1\nошибка\n");
    }

    private void createLoadingFiles() throws IOException {
        Files.write(cars, ("150;Kia;2020\n100;Lada;2010\n200;Toyota;2022\n")
                .getBytes(StandardCharsets.UTF_8));
        Files.write(replacement, "80;Honda;2018\n".getBytes(StandardCharsets.UTF_8));
        Files.write(invalid, "150;Kia;2020\n-1;Lada;2010\n".getBytes(StandardCharsets.UTF_8));
    }

    private void createSortingFiles() throws IOException {
        createLoadingFiles();
        Files.write(cars, String.join("\n", ORIGINAL).getBytes(StandardCharsets.UTF_8));
    }

    private static int countCollections(String text, String title, String... records) {
        StringBuilder block = new StringBuilder(title)
                .append(" (").append(records.length).append("):").append(System.lineSeparator());
        for (int i = 0; i < records.length; i++) {
            block.append(i + 1).append(". ").append(records[i]).append(System.lineSeparator());
        }
        String expected = block.toString();
        int count = 0;
        int position = text.indexOf(expected);
        while (position >= 0) {
            count++;
            position = text.indexOf(expected, position + expected.length());
        }
        return count;
    }

    private static String run(String commands) throws IOException {
        return run(commands, new Modules());
    }

    private static String run(String commands, Modules modules) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(commands), stream);
        new ConsoleApplication(input, stream, modules).run();
        return output.toString("UTF-8");
    }

    private static void assertEndOfInput(String value) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(value), stream);
        assertThrows(EOFException.class,
                () -> new ConsoleApplication(input, stream, new Modules()).run(),
                "Ожидалась ошибка окончания ввода");
        assertEquals(0, countLines(output.toString("UTF-8"), "Работа программы завершена."),
                "Окончание ввода не выдаётся за выбор выхода пользователем");
    }

    private static int countLines(String text, String ending) {
        int count = 0;
        for (String line : text.split("\\r?\\n")) {
            if (line.endsWith(ending)) {
                count++;
            }
        }
        return count;
    }

    private static final class UnavailableModules extends Modules {
        @Override
        public DataSource<Car> manualSource(ConsoleInput input) {
            return null;
        }

        @Override
        public DataSource<Car> randomSource() {
            return null;
        }

        @Override
        public SortStrategy<Car> selectionSort() {
            return null;
        }

        @Override
        public SortStrategy<Car> bubbleSort() {
            return null;
        }

        @Override
        public Comparator<Car> powerComparator() {
            return null;
        }

        @Override
        public Comparator<Car> modelComparator() {
            return null;
        }

        @Override
        public Comparator<Car> productionYearComparator() {
            return null;
        }
    }
}
