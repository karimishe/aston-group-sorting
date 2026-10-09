package sorting.source;

import java.time.LocalDate;
import sorting.model.Car;

public class CarLineParserTest {
    private static int checks;

    public static void main(String[] args) {
        CarLineParser parser = new CarLineParser();
        Car car = parser.parse(" 150 ; Лада Веста ; 2020 ");
        check(car.getPower() == 150, "Числовые поля допускают пробелы по краям");
        check("Лада Веста".equals(car.getModel()), "Модель сохраняет кириллицу и внутренний пробел");
        check(car.getProductionYear() == 2020, "Год прочитан из третьего поля");

        Car minimum = parser.parse("1;Ford;1900");
        check(minimum.getPower() == 1, "Допускается минимальная положительная мощность");
        check(minimum.getProductionYear() == 1900, "Допускается нижняя граница года");
        int currentYear = LocalDate.now().getYear();
        Car maximum = parser.parse(Integer.MAX_VALUE + ";Ford;" + currentYear);
        check(maximum.getPower() == Integer.MAX_VALUE, "Допускается максимальная мощность int");
        check(maximum.getProductionYear() == currentYear, "Допускается текущий год");

        String[] invalidLines = {
            "", "   ", "100", "100;Ford", "100;Ford;2020;extra", "100;Ford;2020;",
            ";Ford;2020", "100;;2020", "100;Ford;", "100;  ;2020",
            "text;Ford;2020", "1.5;Ford;2020", "2147483648;Ford;2020",
            "0;Ford;2020", "-1;Ford;2020", "100;Ford;year", "100;Ford;2147483648",
            "100;Ford;1899", "100;Ford;" + (currentYear + 1),
            "100;Ford\nFocus;2020", "100;Ford\rFocus;2020",
            "100;\rFord;2020", "100;Ford\n;2020"
        };
        for (String line : invalidLines) {
            expectInvalid(parser, line);
        }
        try {
            parser.parse(null);
            throw new AssertionError("Ожидалась ошибка для строки null");
        } catch (NullPointerException exception) {
            checks++;
        }
        System.out.println("Проверок пройдено: " + checks);
    }

    private static void expectInvalid(CarLineParser parser, String line) {
        try {
            parser.parse(line);
        } catch (IllegalArgumentException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидалась ошибка в строке: " + line);
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
