package compare;

import java.util.Comparator;
import java.util.Objects;
import model.Car;

public class CarModelComparator implements Comparator<Car> {
    @Override
    public int compare(Car first, Car second){
        Objects.requireNonNull(first, "Первый автомобиль не должен быть null.");
        Objects.requireNonNull(second, "Второй автомобиль не должен быть null.");

        return first.getModel().compareTo(second.getModel());

    }
}
