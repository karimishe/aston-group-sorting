package sorting.source;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sorting.collection.MyArrayList;
import sorting.model.Car;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomCarSourceTest {

    private static final String EXPECTED_MESSAGE = "Количество автомобилей должно быть положительным.";

    @ParameterizedTest(name = "length = {0}")
    @ValueSource(ints = {-1, 0})
    @DisplayName("IllegalArgumentException с правильным сообщением для некорректной длины")
    void testLoadWithInvalidLength(int length) {
        RandomCarSource source = new RandomCarSource();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> source.load(length));

        assertTrue(
                e.getMessage().contains(EXPECTED_MESSAGE),
                "Ожидалось сообщение '" + EXPECTED_MESSAGE + "', получено: '" + e.getMessage() + "'"
        );
    }

    @Test
    @DisplayName("Размер коллекции должен составить 256 машин")
    void testWithLength256() {
        RandomCarSource source = new RandomCarSource();
        assertEquals(256, source.load(256).size(), "Размер коллекции должен был составить 256 машин");
    }

    @Test
    @DisplayName("Все автомобили в коллекции содержат допустимые значения")
    void testLoadReturnsValidCars() {
        RandomCarSource source = new RandomCarSource();
        MyArrayList<Car> cars = source.load(256);

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            assertNotNull(car, "В коллекции не должно быть null");
            assertTrue(car.getPower() >= CarGenerator.POWER_MIN
                    && car.getPower() <= CarGenerator.POWER_MAX,
                    "Мощность вне допустимого диапазона: " + car.getPower());
            assertTrue(Arrays.asList(CarGenerator.MODELS).contains(car.getModel()),
                    "Недопустимая модель: " + car.getModel());
            assertTrue(car.getProductionYear() >= CarGenerator.YEAR_MIN
                    && car.getProductionYear() <= CarGenerator.YEAR_MAX,
                    "Год выпуска вне допустимого диапазона: " + car.getProductionYear());
        }
    }
}
