package compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import compare.CarPowerComparator;
import org.junit.jupiter.api.Test;
import model.Car;

public class CarPowerComparatorTest {
    private final Comparator<Car> comparator = new CarPowerComparator();

    @Test
    void ordersCarsByIncreasingPower() {
        Car lowerPower = car(100, "Volvo", 2020);
        Car mediumPower = car(150, "Audi", 2018);
        Car higherPower = car(200, "Kia", 2022);

        assertTrue(comparator.compare(lowerPower, mediumPower) < 0);
        assertTrue(comparator.compare(mediumPower, higherPower) < 0);
        assertTrue(comparator.compare(higherPower, lowerPower) > 0);
    }

    @Test
    void returnsZeroForEqualPowerRegardlessOfModelAndYear() {
        Car first = car(100, "Volvo", 2020);
        Car samePower = car(100, "Audi", 1900);

        assertEquals(0, comparator.compare(first, samePower));
    }

    @Test
    void returnsZeroForSameCar() {
        Car car = car(100, "Volvo", 2020);

        assertEquals(0, comparator.compare(car, car));
    }

    @Test
    void comparesMinimumAndMaximumPower() {
        Car minimumPower = car(1, "Ford", 2020);
        Car maximumPower = car(Integer.MAX_VALUE, "Ford", 1900);

        assertTrue(comparator.compare(minimumPower, maximumPower) < 0);
        assertTrue(comparator.compare(maximumPower, minimumPower) > 0);
    }

    @Test
    void rejectsNullFirstCar() {
        Car car = car(100, "Volvo", 2020);

        assertThrows(NullPointerException.class, () -> comparator.compare(null, car));
    }

    @Test
    void rejectsNullSecondCar() {
        Car car = car(100, "Volvo", 2020);

        assertThrows(NullPointerException.class, () -> comparator.compare(car, null));
    }

    @Test
    void rejectsBothNullCars() {
        assertThrows(NullPointerException.class, () -> comparator.compare(null, null));
    }

    private static Car car(int power, String model, int productionYear) {
        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(productionYear)
                .build();
    }
}
