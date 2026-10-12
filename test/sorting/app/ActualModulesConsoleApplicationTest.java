package sorting.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sorting.input.ConsoleInput;

class ActualModulesConsoleApplicationTest {
    @ParameterizedTest
    @MethodSource("sortChoices")
    void sortsManualInputUsingActualAlgorithmAndComparator(int algorithm, int comparator,
            String expected) throws IOException {
        String commands = "1\n2\n3\n300\nAudi\n2020\n100\nVolvo\n2022\n200\nKia\n2018\n"
                + "2\n" + algorithm + "\n" + comparator + "\n4\n3\n0\n";

        String text = run(commands);

        assertTrue(text.contains("Загружено автомобилей: 3"));
        assertTrue(text.contains("Отсортировано автомобилей: 3"));
        assertTrue(text.contains(collection("Результат сортировки", expected.split("\n"))));
        assertTrue(text.contains(collection("Исходная коллекция",
                "300;Audi;2020", "100;Volvo;2022", "200;Kia;2018")));
        assertFalse(text.contains("(не реализовано)"));
        assertTrue(text.contains("Работа программы завершена."));
    }

    static Stream<Arguments> sortChoices() {
        return Stream.of(
                Arguments.of(1, 1, "100;Volvo;2022\n200;Kia;2018\n300;Audi;2020"),
                Arguments.of(2, 2, "100;Volvo;2022\n200;Kia;2018\n300;Audi;2020"),
                Arguments.of(3, 3, "300;Audi;2020\n200;Kia;2018\n100;Volvo;2022"),
                Arguments.of(1, 4, "200;Kia;2018\n300;Audi;2020\n100;Volvo;2022"));
    }

    @Test
    void loadsRequestedNumberOfValidRandomCars() throws IOException {
        String text = run("1\n3\n7\n3\n0\n");

        assertTrue(text.contains("Загружено автомобилей: 7"));
        assertTrue(text.contains("Исходная коллекция (7):"));
        assertFalse(text.contains("Путь к файлу:"));
        assertFalse(text.contains("Введите мощность:"));
        Matcher rows = Pattern.compile("(?m)^(\\d+)\\. (\\d+);([^;\\r\\n]+);(\\d+)\\r?$")
                .matcher(text);
        int count = 0;
        int currentYear = LocalDate.now().getYear();
        while (rows.find()) {
            count++;
            assertEquals(count, Integer.parseInt(rows.group(1)));
            assertTrue(Integer.parseInt(rows.group(2)) > 0);
            String model = rows.group(3);
            assertFalse(model.trim().isEmpty());
            assertEquals(model.trim(), model);
            int year = Integer.parseInt(rows.group(4));
            assertTrue(year >= 1900 && year <= currentYear);
        }
        assertEquals(7, count, "Выведено ровно запрошенное количество корректных записей");
        assertTrue(text.contains("Работа программы завершена."));
    }

    private String collection(String title, String... records) {
        StringBuilder text = new StringBuilder(title).append(" (").append(records.length)
                .append("):").append(System.lineSeparator());
        for (int i = 0; i < records.length; i++) {
            text.append(i + 1).append(". ").append(records[i]).append(System.lineSeparator());
        }
        return text.toString();
    }

    private String run(String commands) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        ConsoleInput input = new ConsoleInput(new StringReader(commands), output);
        new ConsoleApplication(input, output, new Modules()).run();
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
