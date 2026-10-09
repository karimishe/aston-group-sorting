package sorting.app;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import sorting.input.ConsoleInput;

public class ConsoleApplicationTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader("0\n1\n"), stream);
        new ConsoleApplication(input, stream).run();
        String text = output.toString("UTF-8");
        check(text.startsWith("Приложение сортировки автомобилей"), "Показано название программы");
        check(countLines(text, "Меню:") == 1, "Перед немедленным выходом меню показано один раз");
        check(text.endsWith("Работа программы завершена." + System.lineSeparator()),
                "Ноль завершает программу с сообщением");
        check("1".equals(input.readLine("")), "После выхода ввод не читается и Reader не закрывается");

        output.reset();
        input = new ConsoleInput(new StringReader("1\n2\n3\n4\n0\n"), stream);
        new ConsoleApplication(input, stream).run();
        text = output.toString("UTF-8");
        check(countLines(text, "Меню:") == 5, "После каждой команды можно выбрать следующую");
        check(countLines(text, "Загрузка из файла пока не реализована.") == 1,
                "Команда загрузки сообщает о предстоящем шаге");
        check(countLines(text, "Сортировка пока не реализована.") == 1,
                "Команда сортировки сообщает о предстоящем шаге");
        check(countLines(text, "Исходная коллекция пока не загружена.") == 1,
                "До загрузки отсутствует исходная коллекция");
        check(countLines(text, "Результата сортировки пока нет.") == 1,
                "До сортировки отсутствует результат");
        check(countLines(text, "Работа программы завершена.") == 1, "Выход выполняется ровно один раз");

        output.reset();
        input = new ConsoleInput(new StringReader("\nтекст\n1.5\n2147483648\n-1\n5\n 0 \n"), stream);
        new ConsoleApplication(input, stream).run();
        text = output.toString("UTF-8");
        check(countLines(text, "Меню:") == 1, "Ошибочный выбор повторяет ввод без нового меню");
        check(countLines(text, "Введите целое число.") == 4, "Нечисловой ввод и переполнение обработаны");
        check(countLines(text, "Введите число от 0 до 4.") == 2, "Несуществующие команды обработаны");
        check(countLines(text, "Работа программы завершена.") == 1, "После ошибок можно выйти");
        checkEndOfInput("");
        checkEndOfInput("ошибка\n");
        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkEndOfInput(String value) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(value), stream);
        try {
            new ConsoleApplication(input, stream).run();
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
