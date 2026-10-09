package sorting.input;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

public class ConsoleInputTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleInput input = new ConsoleInput(new StringReader("  Kia  \n\n5\n"),
                new PrintStream(output, true, "UTF-8"));
        check("  Kia  ".equals(input.readLine("Модель: ")), "Чтение строки сохраняет пробелы");
        check("".equals(input.readLine("Строка: ")), "Пустая строка является вводом, а не EOF");
        check(input.readInt("Число: ", 1, 5) == 5, "После строк читается следующее число");
        check("Модель: Строка: Число: ".equals(output.toString("UTF-8")),
                "Приглашения выводятся без лишних переводов строк");

        input = new ConsoleInput(new StringReader("\t0 \n4\n-2147483648\n2147483647\n"),
                new PrintStream(new ByteArrayOutputStream()));
        check(input.readInt("", 0, 4) == 0, "Нижняя граница допустима, пробелы удаляются");
        check(input.readInt("", 0, 4) == 4, "Верхняя граница допустима");
        check(input.readInt("", Integer.MIN_VALUE, Integer.MAX_VALUE) == Integer.MIN_VALUE,
                "Допускается минимальное значение int");
        check(input.readInt("", Integer.MIN_VALUE, Integer.MAX_VALUE) == Integer.MAX_VALUE,
                "Допускается максимальное значение int");

        output.reset();
        input = new ConsoleInput(new StringReader("\nтекст\n1.5\n2147483648\n-1\n5\n 2 \n"),
                new PrintStream(output, true, "UTF-8"));
        check(input.readInt("Число: ", 0, 4) == 2, "Ошибки не мешают прочитать допустимое число");
        String expected = "";
        for (int i = 0; i < 4; i++) {
            expected += "Число: Введите целое число." + System.lineSeparator();
        }
        for (int i = 0; i < 2; i++) {
            expected += "Число: Введите число от 0 до 4." + System.lineSeparator();
        }
        check((expected + "Число: ").equals(output.toString("UTF-8")),
                "Каждая ошибка получает пояснение и новое приглашение");

        input = new ConsoleInput(new StringReader("7\n"), new PrintStream(new ByteArrayOutputStream()));
        try {
            input.readInt("", 8, 7);
            throw new AssertionError("Ожидалась ошибка неверных границ");
        } catch (IllegalArgumentException exception) {
            checks++;
        }
        check(input.readInt("", 7, 7) == 7, "Неверные границы не потребляют ввод; равные допустимы");
        checkEndOfInput("");
        checkEndOfInput("ошибка\n");
        checkReadFailure();

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkEndOfInput(String text) throws IOException {
        ConsoleInput input = new ConsoleInput(new StringReader(text),
                new PrintStream(new ByteArrayOutputStream()));
        try {
            input.readInt("", 0, 4);
            throw new AssertionError("Ожидалась ошибка окончания ввода");
        } catch (EOFException exception) {
            check("Ввод завершён до выбора выхода.".equals(exception.getMessage()),
                    "Окончание ввода сообщается явно, в том числе после неверного числа");
        }
    }

    private static void checkReadFailure() throws IOException {
        IOException failure = new IOException("Ошибка чтения");
        int[] attempts = {0};
        Reader reader = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                attempts[0]++;
                throw failure;
            }

            @Override
            public void close() {
                throw new AssertionError("ConsoleInput не должен закрывать переданный Reader");
            }
        };
        ConsoleInput input = new ConsoleInput(reader, new PrintStream(new ByteArrayOutputStream()));
        try {
            input.readInt("", 0, 4);
            throw new AssertionError("Ожидалась ошибка чтения");
        } catch (IOException exception) {
            check(exception == failure, "Ошибка чтения передаётся вызывающему коду");
        }
        check(attempts[0] == 1, "При ошибке чтения нет бесконечных повторов");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
