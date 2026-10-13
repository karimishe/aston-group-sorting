package source;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import collection.MyArrayList;
import model.Car;
import source.CarGenerator;
import source.StreamRandomCarSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamRandomCarSourceTest {
    @Test
    void generatesExactlyRequestedNumberInsideStreamAndReturnsFreshObjects() {
        AtomicInteger generated = new AtomicInteger();
        CarGenerator generator = new CarGenerator() {
            @Override
            public Car get() {
                generated.incrementAndGet();
                return new Car.Builder()
                        .power(150)
                        .model("Ford")
                        .productionYear(2020)
                        .build();
            }
        };
        StreamRandomCarSource source = new StreamRandomCarSource(generator);

        MyArrayList<Car> first = source.load(3);
        MyArrayList<Car> second = source.load(2);

        assertEquals(3, first.size());
        assertEquals(2, second.size());
        assertEquals(5, generated.get());
        assertNotSame(first, second);
        assertNotSame(first.get(0), first.get(1));
        assertEquals("Ford", second.get(0).getModel());
    }

    @Test
    void generatedCarsHaveValidValues() {
        MyArrayList<Car> cars = new StreamRandomCarSource().load(300);

        assertEquals(300, cars.size());
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            assertNotNull(car);
            assertTrue(car.getPower() >= CarGenerator.POWER_MIN
                    && car.getPower() <= CarGenerator.POWER_MAX);
            assertTrue(Arrays.asList(CarGenerator.MODELS).contains(car.getModel()));
            assertTrue(car.getProductionYear() >= CarGenerator.YEAR_MIN
                    && car.getProductionYear() <= CarGenerator.YEAR_MAX);
        }
    }

    @Test
    void rejectsNonPositiveLength() {
        StreamRandomCarSource source = new StreamRandomCarSource();
        assertThrows(IllegalArgumentException.class, () -> source.load(0));
        assertThrows(IllegalArgumentException.class, () -> source.load(-1));
    }

    @Test
    void rejectsNullGenerator() {
        assertThrows(NullPointerException.class, () -> new StreamRandomCarSource(null));
    }
}
