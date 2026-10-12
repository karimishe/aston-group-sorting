package sorting.compare;

import java.util.Comparator;
import java.util.Objects;
import sorting.model.Car;

public final class CarPowerComparator implements Comparator<Car> {
    @Override
    public int compare(Car first, Car second) {
        Objects.requireNonNull(first, "Первый автомобиль не должен быть null.");
        Objects.requireNonNull(second, "Второй автомобиль не должен быть null.");

        return Integer.compare(first.getPower(), second.getPower());
    }
}
