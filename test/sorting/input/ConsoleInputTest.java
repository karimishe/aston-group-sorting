package sorting.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ConsoleInputTest {
    @Test
    void preservesSpacesAndEmptyLinesAndReadsNextNumber() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleInput input = input("  Kia  \n\n5\n", output);

        assertEquals("  Kia  ", input.readLine("Модель: "));
        assertEquals("", input.readLine("Строка: "));
        assertEquals(5, input.readInt("Число: ", 1, 5));
        assertEquals("Модель: Строка: Число: ", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void acceptsRangeBoundariesAndTrimsNumbers() throws IOException {
        ConsoleInput input = input("\t0 \n4\n-2147483648\n2147483647\n");

        assertEquals(0, input.readInt("", 0, 4));
        assertEquals(4, input.readInt("", 0, 4));
        assertEquals(Integer.MIN_VALUE, input.readInt("", Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, input.readInt("", Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void explainsEachInvalidNumberAndRepeatsPrompt() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleInput input = input("\nтекст\n1.5\n2147483648\n-1\n5\n 2 \n", output);

        assertEquals(2, input.readInt("Число: ", 0, 4));

        String invalidNumber = "Число: Введите целое число." + System.lineSeparator();
        String outsideRange = "Число: Введите число от 0 до 4." + System.lineSeparator();
        assertEquals(invalidNumber.repeat(4) + outsideRange.repeat(2) + "Число: ",
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void rejectsReversedBoundsWithoutConsumingInputAndAcceptsEqualBounds() throws IOException {
        ConsoleInput input = input("7\n");

        assertThrows(IllegalArgumentException.class, () -> input.readInt("", 8, 7));
        assertEquals(7, input.readInt("", 7, 7));
    }

    @Test
    void reportsEndOfInputBeforeNumber() {
        ConsoleInput input = input("");

        EOFException exception = assertThrows(EOFException.class, () -> input.readInt("", 0, 4));

        assertEquals("Ввод завершён до выбора выхода.", exception.getMessage());
    }

    @Test
    void reportsEndOfInputAfterInvalidNumber() {
        ConsoleInput input = input("ошибка\n");

        EOFException exception = assertThrows(EOFException.class, () -> input.readInt("", 0, 4));

        assertEquals("Ввод завершён до выбора выхода.", exception.getMessage());
    }

    @Test
    void propagatesReadFailureWithoutRetryingOrClosingReader() {
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
                fail("ConsoleInput не должен закрывать переданный Reader");
            }
        };
        ConsoleInput input = new ConsoleInput(reader, new PrintStream(new ByteArrayOutputStream()));

        IOException exception = assertThrows(IOException.class, () -> input.readInt("", 0, 4));

        assertSame(failure, exception);
        assertEquals(1, attempts[0]);
    }

    private ConsoleInput input(String text) {
        return input(text, new ByteArrayOutputStream());
    }

    private ConsoleInput input(String text, ByteArrayOutputStream output) {
        return new ConsoleInput(new StringReader(text),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
