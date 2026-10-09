package sorting.input;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Objects;

public final class ConsoleInput {
    private final BufferedReader reader;
    private final PrintStream output;

    public ConsoleInput(Reader reader, PrintStream output) {
        this.reader = new BufferedReader(reader);
        this.output = Objects.requireNonNull(output, "Поток вывода не должен быть null.");
    }

    public String readLine(String prompt) throws IOException {
        output.print(prompt);
        output.flush();
        String line = reader.readLine();
        if (line == null) {
            throw new EOFException("Ввод завершён до выбора выхода.");
        }
        return line;
    }

    public int readInt(String prompt, int min, int max) throws IOException {
        if (min > max) {
            throw new IllegalArgumentException("Минимальное значение не должно превышать максимальное.");
        }

        while (true) {
            String line = readLine(prompt);
            int value;
            try {
                value = Integer.parseInt(line.trim());
            } catch (NumberFormatException exception) {
                output.println("Введите целое число.");
                continue;
            }

            if (value < min || value > max) {
                output.println("Введите число от " + min + " до " + max + ".");
                continue;
            }
            return value;
        }
    }
}
