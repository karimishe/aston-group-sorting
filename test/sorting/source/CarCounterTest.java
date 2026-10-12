package sorting.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sorting.collection.MyArrayList;
import sorting.model.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CarCounterTest {
    // сколько машин каждого типа будет находиться в своем списке
    private static final int CARS1_COUNT = 7;
    private static final int CARS2_COUNT = 15;
    private static final int CARS3_COUNT = 1;
    private static final int CARS4_COUNT = 10;
    private static final int CARS5_COUNT = 0;

    private CarCounter counter;
    private MyArrayList<Car> cars;

    private Car car1;
    private Car car2;
    private Car car3;
    private Car car4;
    private Car car5;

    @BeforeEach
    void setUp() {
        counter = new CarCounter();
        cars = new MyArrayList<>();

        for (int i = 0; i < CARS1_COUNT; i++) {
            cars.add(makeCar1());
        }
        for (int i = 0; i < CARS2_COUNT; i++) {
            cars.add(makeCar2());
        }
        for (int i = 0; i < CARS3_COUNT; i++) {
            cars.add(makeCar3());
        }
        for (int i = 0; i < CARS4_COUNT; i++) {
            cars.add(makeCar4());
        }

        car1 = makeCar1();
        car2 = makeCar2();
        car3 = makeCar3();
        car4 = makeCar4();
        car5 = makeCar5();
    }

    @ParameterizedTest(name = "потоков: {0}")
    @ValueSource(ints = {1, 2, 3, 4, 7})
    @DisplayName("Корректный подсчёт при разном количестве потоков")
    void testCount(int threadCount) throws InterruptedException {
        assertEquals(CARS1_COUNT, counter.count(cars, car1, threadCount), message(threadCount, 1));
        assertEquals(CARS2_COUNT, counter.count(cars, car2, threadCount), message(threadCount, 2));
        assertEquals(CARS3_COUNT, counter.count(cars, car3, threadCount), message(threadCount, 3));
        assertEquals(CARS4_COUNT, counter.count(cars, car4, threadCount), message(threadCount, 4));
        assertEquals(CARS5_COUNT, counter.count(cars, car5, threadCount), message(threadCount, 5));
    }

    @ParameterizedTest(name = "потоков: {0}")
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("IllegalArgumentException при недопустимом количестве потоков")
    void testInvalidThreadCount(int threadCount) {
        assertThrows(IllegalArgumentException.class, () -> counter.count(cars, car1, threadCount));
    }

    @Test
    void emptyCollectionHasNoMatches() throws InterruptedException {
        assertEquals(0, counter.count(new MyArrayList<>(), car1, 3));
    }

    @Test
    void countsWhenThereAreMoreThreadsThanElements() throws InterruptedException {
        MyArrayList<Car> small = new MyArrayList<>();
        small.add(makeCar1());
        small.add(makeCar2());
        small.add(makeCar1());

        assertEquals(2, counter.count(small, car1, 5));
        assertEquals(0, counter.count(small, car5, 5));
    }

    @Test
    void rejectsNullCollectionAndTarget() {
        assertThrows(IllegalArgumentException.class, () -> counter.count(null, car1, 2));
        assertThrows(IllegalArgumentException.class, () -> counter.count(cars, null, 2));
    }

    @Test
    @DisplayName("InterruptedException при прерывании потока")
    void testInterruptedException() {
        Thread.currentThread().interrupt();
        try {
            counter.count(cars, car1, 1);
            fail("Ожидалось InterruptedException");
        } catch (InterruptedException expected) {
            // тест пройден
        } finally {
            Thread.interrupted();
        }
    }

    private static String message(int threadCount, int carIndex) {
        return "Неверный подсчёт. потоков: " + threadCount + ", авто: " + carIndex;
    }

    private static Car makeCar1() {
        return new Car.Builder().power(11).model("Car").productionYear(2010).build();
    }

    private static Car makeCar2() {
        return new Car.Builder().power(22).model("Car").productionYear(2010).build();
    }

    private static Car makeCar3() {
        return new Car.Builder().power(11).model("raC").productionYear(2010).build();
    }

    private static Car makeCar4() {
        return new Car.Builder().power(11).model("Car").productionYear(1999).build();
    }

    private static Car makeCar5() {
        return new Car.Builder().power(22).model("raC").productionYear(1999).build();
    }
}
