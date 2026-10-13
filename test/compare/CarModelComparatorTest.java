package compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import compare.CarModelComparator;
import org.junit.jupiter.api.Test;
import model.Car;

class CarModelComparatorTest {
    private final Comparator<Car> comparator = new CarModelComparator();

    @Test
    void ordersCarsByModelRegardlessOfPowerAndYear() {
        Car audi = car(200, "Audi", 2022);
        Car volvo = car(100, "Volvo", 1900);

        assertTrue(comparator.compare(audi, volvo) < 0);
        assertTrue(comparator.compare(volvo, audi) > 0);
    }

    @Test
    void returnsZeroForEqualModelsRegardlessOfOtherFields() {
        Car first = car(100, "Audi", 1900);
        Car sameModel = car(200, "Audi", 2022);

        assertEquals(0, comparator.compare(first, sameModel));
        assertEquals(0, comparator.compare(first, first));
    }

    @Test
    void comparesModelsWithCaseSensitivity() {
        Car uppercase = car(100, "Audi", 2020);
        Car lowercase = car(100, "audi", 2020);

        assertTrue(comparator.compare(uppercase, lowercase) < 0);
        assertTrue(comparator.compare(lowercase, uppercase) > 0);
    }

    @Test
    void comparesCyrillicModels() {
        Car lada = car(100, "Лада", 2020);
        Car moskvich = car(100, "Москвич", 2020);

        assertTrue(comparator.compare(lada, moskvich) < 0);
        assertTrue(comparator.compare(moskvich, lada) > 0);
    }

    @Test
    void rejectsNullCars() {
        Car car = car(100, "Audi", 2020);

        assertThrows(NullPointerException.class, () -> comparator.compare(null, car));
        assertThrows(NullPointerException.class, () -> comparator.compare(car, null));
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
