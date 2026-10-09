package sorting.source;

import java.util.Objects;
import sorting.model.Car;

public final class CarLineParser {
    public Car parse(String line) {
        Objects.requireNonNull(line, "Строка не должна быть null.");
        if (line.trim().isEmpty()) {
            throw new IllegalArgumentException("Пустая строка.");
        }

        String[] fields = line.split(";", -1);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Ожидалось 3 поля: мощность;модель;год.");
        }

        int power = parseNumber(fields[0], "Мощность");
        int productionYear = parseNumber(fields[2], "Год выпуска");
        return new Car.Builder()
                .power(power)
                .model(fields[1])
                .productionYear(productionYear)
                .build();
    }

    private int parseNumber(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + ": требуется целое число в диапазоне int.", exception);
        }
    }
}
