package sorting.source;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sorting.collection.MyArrayList;
import sorting.model.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("Генерируемые коллекции не могут быть одинаковыми всегда")
    void testNotEqualLists() {
        RandomCarSource source = new RandomCarSource();

        boolean foundDifference = false;
        for (int i = 0; i < 10 && !foundDifference; i++) {
            MyArrayList<Car> cars1 = source.load(1);
            MyArrayList<Car> cars2 = source.load(1);

            if (
                    cars1.get(0).getPower() == cars2.get(0).getPower() &&
                    cars1.get(0).getModel().equals(cars2.get(0).getModel()) &&
                    cars1.get(0).getProductionYear() == cars2.get(0).getProductionYear()
            ) {
                foundDifference = true;
            }
        }

        assertTrue(!foundDifference, "Генерируемые коллекции не могут быть одинаковыми всегда");
    }
}
