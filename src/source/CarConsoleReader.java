package source;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import input.ConsoleInput;
import model.Car;

public class CarConsoleReader {
    private final ConsoleInput input;

    public CarConsoleReader(ConsoleInput input) {
        this.input = Objects.requireNonNull(input, "Читатель консоли не должен быть null.");
    }

    public Car readCar() throws IOException {
        int power = input.readInt("Введите мощность: ", 1, Integer.MAX_VALUE);
        String model = input.readLine("Введите модель: ").trim();
        while (model.isEmpty() || model.indexOf(';') >= 0) {
            model = input.readLine("Модель должна быть непустой и не содержать ';'. "
                    + "Введите модель: ").trim();
        }
        int year = input.readInt("Введите год выпуска: ", 1900, LocalDate.now().getYear());

        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(year)
                .build();
    }
}
