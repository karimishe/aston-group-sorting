package compare;

import model.Car;

import java.util.Comparator;
import java.util.Objects;

public class CarProductionYearComparator implements Comparator<Car> {
    @Override
    public int compare(Car first, Car second) {
        Objects.requireNonNull(first, "Первый автомобиль не должен быть null.");
        Objects.requireNonNull(second, "Второй автомобиль не должен быть null.");

        return Integer.compare(first.getProductionYear(), second.getProductionYear());
    }
}
