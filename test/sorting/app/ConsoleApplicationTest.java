package sorting.app;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import sorting.input.ConsoleInput;

public class ConsoleApplicationTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader("0\n1\n"), stream);
        new ConsoleApplication(input, stream, new Modules()).run();
        String text = output.toString("UTF-8");
        check(text.startsWith("Приложение сортировки автомобилей"), "Показано название программы");
        check(countLines(text, "Меню:") == 1, "Перед немедленным выходом меню показано один раз");
        check(text.endsWith("Работа программы завершена." + System.lineSeparator()),
                "Ноль завершает программу с сообщением");
        check("1".equals(input.readLine("")), "После выхода ввод не читается и Reader не закрывается");

        output.reset();
        input = new ConsoleInput(new StringReader("2\n3\n4\n0\n"), stream);
        new ConsoleApplication(input, stream, new Modules()).run();
        text = output.toString("UTF-8");
        check(countLines(text, "Меню:") == 4, "После каждой команды можно выбрать следующую");
        check(countLines(text, "Сортировка пока не реализована.") == 1,
                "Команда сортировки сообщает о предстоящем шаге");
        check(countLines(text, "Исходная коллекция пока не загружена.") == 1,
                "До загрузки отсутствует исходная коллекция");
        check(countLines(text, "Результата сортировки пока нет.") == 1,
                "До сортировки отсутствует результат");
        check(countLines(text, "Работа программы завершена.") == 1, "Выход выполняется ровно один раз");

        output.reset();
        input = new ConsoleInput(new StringReader("\nтекст\n1.5\n2147483648\n-1\n5\n 0 \n"), stream);
        new ConsoleApplication(input, stream, new Modules()).run();
        text = output.toString("UTF-8");
        check(countLines(text, "Меню:") == 1, "Ошибочный выбор повторяет ввод без нового меню");
        check(countLines(text, "Введите целое число.") == 4, "Нечисловой ввод и переполнение обработаны");
        check(countLines(text, "Введите число от 0 до 4.") == 2, "Несуществующие команды обработаны");
        check(countLines(text, "Работа программы завершена.") == 1, "После ошибок можно выйти");
        checkEndOfInput("");
        checkEndOfInput("ошибка\n");
        checkEndOfInput("1\n");
        checkEndOfInput("1\ncars.txt\n");
        checkFileLoading();
        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkFileLoading() throws IOException {
        Path directory = Files.createTempDirectory("car-menu-test-");
        Path cars = directory.resolve("cars.txt");
        Path replacement = directory.resolve("replacement.txt");
        Path invalid = directory.resolve("invalid.txt");
        try {
            Files.write(cars, ("150;Kia;2020\n100;Lada;2010\n200;Toyota;2022\n")
                    .getBytes(StandardCharsets.UTF_8));
            Files.write(replacement, "80;Honda;2018\n".getBytes(StandardCharsets.UTF_8));
            Files.write(invalid, "150;Kia;2020\n-1;Lada;2010\n".getBytes(StandardCharsets.UTF_8));

            String text = run("1\n  " + cars + "  \n2\n3\n0\n");
            check(countLines(text, "Загружено автомобилей: 2") == 1,
                    "Загружается запрошенное количество, пробелы вокруг пути удаляются");
            check(countLines(text, "Исходная коллекция (2):") == 1,
                    "Показана длина загруженной коллекции");
            check(countLines(text, "1. 150;Kia;2020") == 1, "Показан первый автомобиль");
            check(countLines(text, "2. 100;Lada;2010") == 1, "Показан второй автомобиль");
            check(countLines(text, "3. 200;Toyota;2022") == 0,
                    "Автомобили за пределами выбранной длины не добавляются");
            check(countLines(text, "Меню:") == 3, "После загрузки и просмотра работа продолжается");

            text = run("1\n" + cars + "\n2\n1\n" + replacement + "\n1\n3\n0\n");
            check(countLines(text, "Загружено автомобилей: 1") == 1,
                    "Повторная успешная загрузка сообщается пользователю");
            check(countLines(text, "Исходная коллекция (1):") == 1,
                    "Новая коллекция заменяет предыдущую");
            check(countLines(text, "1. 80;Honda;2018") == 1, "Показаны новые данные");
            check(countLines(text, "1. 150;Kia;2020") == 0, "Старые данные не сохраняются при замене");

            text = run("1\n" + cars + "\n1\n"
                    + "1\n" + invalid + "\n1\n3\n"
                    + "1\n" + directory.resolve("missing.txt") + "\n1\n3\n"
                    + "1\ninvalid\u0000path\n3\n0\n");
            check(countLines(text, "Загружено автомобилей: 1") == 1,
                    "Ошибочные загрузки не выдаются за успешные");
            check(countLines(text, "Не удалось загрузить файл: Строка 2: "
                    + "Мощность должна быть положительным числом.") == 1,
                    "Ошибка файла указывает причину и номер плохой строки");
            check(countLines(text, "Исходная коллекция (1):") == 3,
                    "После каждой ошибки сохраняется прежняя длина коллекции");
            check(countLines(text, "1. 150;Kia;2020") == 3,
                    "Плохая строка после выбранного количества, отсутствующий файл и плохой путь не теряют данные");
            check(countLines(text, "Некорректный путь к файлу.") == 1,
                    "Некорректный путь обрабатывается без завершения программы");

            text = run("1\n" + cars + "\nтекст\n0\n-1\n2147483648\n2\n3\n0\n");
            check(countLines(text, "Введите целое число.") == 2,
                    "Нечисловая длина и переполнение вызывают повторный запрос");
            check(countLines(text, "Введите число от 1 до 2147483647.") == 2,
                    "Нулевая и отрицательная длина вызывают повторный запрос");
            check(countLines(text, "Исходная коллекция (2):") == 1,
                    "После ошибок длины коллекция успешно загружается");

            text = run("1\n   \n3\n0\n");
            check(countLines(text, "Путь к файлу не должен быть пустым.") == 1,
                    "Пустой путь сопровождается понятным сообщением");
            check(countLines(text, "Исходная коллекция пока не загружена.") == 1,
                    "После пустого пути выполняется следующая команда меню без запроса длины");
        } finally {
            Files.deleteIfExists(invalid);
            Files.deleteIfExists(replacement);
            Files.deleteIfExists(cars);
            Files.deleteIfExists(directory);
        }
    }

    private static String run(String commands) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(commands), stream);
        new ConsoleApplication(input, stream, new Modules()).run();
        return output.toString("UTF-8");
    }

    private static void checkEndOfInput(String value) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(value), stream);
        try {
            new ConsoleApplication(input, stream, new Modules()).run();
            throw new AssertionError("Ожидалась ошибка окончания ввода");
        } catch (EOFException exception) {
            checks++;
        }
        check(countLines(output.toString("UTF-8"), "Работа программы завершена.") == 0,
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

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
