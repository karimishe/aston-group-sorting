package source;

import java.io.IOException;
import java.util.Objects;
import collection.MyArrayList;
import contract.DataSource;
import input.ConsoleInput;
import model.Car;

public final class ManualCarSource implements DataSource<Car> {
    private final ConsoleInput input;

    public ManualCarSource(ConsoleInput input) {
        this.input = Objects.requireNonNull(input, "Читатель консоли не должен быть null.");
    }

    @Override
    public MyArrayList<Car> load(int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        MyArrayList<Car> cars = new MyArrayList<>();
        CarConsoleReader carConsoleReader = new CarConsoleReader(input);
        for (int i = 0; i < length; i++) {
            cars.add(carConsoleReader.readCar());
        }
        return cars;
    }
}
