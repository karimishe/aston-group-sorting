package sorting.compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import sorting.model.Car;

public class CarAllFieldsComparatorTest {
    private final Comparator<Car> comparator = new CarAllFieldsComparator();

    @Test
    void comparesPowerBeforeModelAndYear() {
        Car lowerPower = car(100, "Volvo", 2020);
        Car higherPower = car(200, "Audi", 1900);

        assertTrue(comparator.compare(lowerPower, higherPower) < 0,
                "Мощность важнее модели и года: меньшая мощность идёт раньше");
        assertTrue(comparator.compare(higherPower, lowerPower) > 0,
                "Мощность важнее модели и года: большая мощность идёт позже");
    }

    @Test
    void comparesModelBeforeYearWhenPowerIsEqual() {
        Car earlierModel = car(100, "Audi", 2020);
        Car laterModel = car(100, "Volvo", 1900);

        assertTrue(comparator.compare(earlierModel, laterModel) < 0,
                "При равной мощности модель важнее года: Audi идёт раньше Volvo");
        assertTrue(comparator.compare(laterModel, earlierModel) > 0,
                "При равной мощности модель важнее года: Volvo идёт позже Audi");
    }

    @Test
    void comparesYearWhenPowerAndModelAreEqual() {
        Car older = car(100, "Audi", 2018);
        Car newer = car(100, "Audi", 2020);

        assertTrue(comparator.compare(older, newer) < 0,
                "При равных мощности и модели меньший год идёт раньше");
        assertTrue(comparator.compare(newer, older) > 0,
                "При равных мощности и модели больший год идёт позже");
    }

    @Test
    void returnsZeroForEqualFieldsAndSameObject() {
        Car first = car(100, "Audi", 2018);
        Car sameFields = car(100, "Audi", 2018);

        assertEquals(0, comparator.compare(first, sameFields),
                "Разные объекты с одинаковыми полями равны по порядку");
        assertEquals(0, comparator.compare(first, first),
                "Автомобиль равен самому себе по порядку");
    }

    @Test
    void comparesModelsWithCaseSensitivity() {
        Car uppercase = car(100, "Audi", 2020);
        Car lowercase = car(100, "audi", 2020);

        assertTrue(comparator.compare(uppercase, lowercase) < 0,
                "Сравнение моделей учитывает регистр: Audi идёт раньше audi");
        assertTrue(comparator.compare(lowercase, uppercase) > 0,
                "Сравнение моделей учитывает регистр: audi идёт позже Audi");
    }

    @Test
    void comparesCyrillicModels() {
        Car lada = car(100, "Лада", 2020);
        Car moskvich = car(100, "Москвич", 2020);

        assertTrue(comparator.compare(lada, moskvich) < 0,
                "Сравниваются модели на кириллице: Лада идёт раньше Москвич");
        assertTrue(comparator.compare(moskvich, lada) > 0,
                "Сравниваются модели на кириллице: Москвич идёт позже Лада");
    }

    @Test
    void comparesMinimumAndMaximumPower() {
        Car minimumPower = car(1, "Ford", LocalDate.now().getYear());
        Car maximumPower = car(Integer.MAX_VALUE, "Ford", 1900);

        assertTrue(comparator.compare(minimumPower, maximumPower) < 0,
                "Минимальная допустимая мощность меньше максимальной");
        assertTrue(comparator.compare(maximumPower, minimumPower) > 0,
                "Максимальная допустимая мощность больше минимальной");
    }

    @Test
    void comparesMinimumAndCurrentYear() {
        Car minimumYear = car(100, "Ford", 1900);
        Car maximumYear = car(100, "Ford", LocalDate.now().getYear());

        assertTrue(comparator.compare(minimumYear, maximumYear) < 0,
                "Первый допустимый год идёт раньше текущего");
        assertTrue(comparator.compare(maximumYear, minimumYear) > 0,
                "Текущий год идёт позже первого допустимого");
    }

    @Test
    void rejectsNullFirstCar() {
        Car car = car(100, "Audi", 2018);

        assertThrows(NullPointerException.class, () -> comparator.compare(null, car),
                "Первый автомобиль null");
    }

    @Test
    void rejectsNullSecondCar() {
        Car car = car(100, "Audi", 2018);

        assertThrows(NullPointerException.class, () -> comparator.compare(car, null),
                "Второй автомобиль null");
    }

    @Test
    void rejectsBothNullCars() {
        assertThrows(NullPointerException.class, () -> comparator.compare(null, null),
                "Оба автомобиля null");
    }

    private static Car car(int power, String model, int productionYear) {
        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(productionYear)
                .build();
    }
}
