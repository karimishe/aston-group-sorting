package source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import model.Car;
import source.CarLineParser;

class CarLineParserTest {
    private final CarLineParser parser = new CarLineParser();

    @Test
    void parsesAllFieldsAndPreservesCyrillicAndInternalSpaces() {
        Car car = parser.parse(" 150 ; Лада Веста ; 2020 ");

        assertEquals(150, car.getPower());
        assertEquals("Лада Веста", car.getModel());
        assertEquals(2020, car.getProductionYear());
    }

    @Test
    void acceptsMinimumPowerAndYear() {
        Car car = parser.parse("1;Ford;1900");

        assertEquals(1, car.getPower());
        assertEquals(1900, car.getProductionYear());
    }

    @Test
    void acceptsMaximumPowerAndCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        Car car = parser.parse(Integer.MAX_VALUE + ";Ford;" + currentYear);

        assertEquals(Integer.MAX_VALUE, car.getPower());
        assertEquals(currentYear, car.getProductionYear());
    }

    @Test
    void rejectsEmptyLinesAndWrongFieldCount() {
        assertInvalidLines("", "   ", "100", "100;Ford", "100;Ford;2020;extra", "100;Ford;2020;");
    }

    @Test
    void rejectsEmptyFields() {
        assertInvalidLines(";Ford;2020", "100;;2020", "100;Ford;", "100;  ;2020");
    }

    @Test
    void rejectsInvalidPower() {
        assertInvalidLines("text;Ford;2020", "1.5;Ford;2020", "2147483648;Ford;2020",
                "0;Ford;2020", "-1;Ford;2020");
    }

    @Test
    void rejectsInvalidYear() {
        int futureYear = LocalDate.now().getYear() + 1;

        assertInvalidLines("100;Ford;year", "100;Ford;2147483648", "100;Ford;1899",
                "100;Ford;" + futureYear);
    }

    @Test
    void rejectsLineBreaksInsideModelAndAtItsEdges() {
        assertInvalidLines("100;Ford\nFocus;2020", "100;Ford\rFocus;2020",
                "100;\rFord;2020", "100;Ford\n;2020");
    }

    @Test
    void rejectsNullLine() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    private void assertInvalidLines(String... lines) {
        for (String line : lines) {
            assertThrows(IllegalArgumentException.class, () -> parser.parse(line),
                    "Ожидалась ошибка в строке: " + line);
        }
    }
}
