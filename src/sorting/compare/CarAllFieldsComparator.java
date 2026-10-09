package sorting.compare;

import java.util.Comparator;
import java.util.Objects;
import sorting.model.Car;

public final class CarAllFieldsComparator implements Comparator<Car> {
    @Override
    public int compare(Car first, Car second) {
        Objects.requireNonNull(first, "Первый автомобиль не должен быть null.");
        Objects.requireNonNull(second, "Второй автомобиль не должен быть null.");

        int result = Integer.compare(first.getPower(), second.getPower());
        if (result != 0) {
            return result;
        }

        result = first.getModel().compareTo(second.getModel());
        if (result != 0) {
            return result;
        }

        return Integer.compare(first.getProductionYear(), second.getProductionYear());
    }
}
