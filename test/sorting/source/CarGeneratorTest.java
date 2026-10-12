package sorting.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import sorting.model.Car;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CarGeneratorTest {

    private CarGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CarGenerator();
    }

    @RepeatedTest(value = 10, name = "Проверка моделей ({currentRepetition}/{totalRepetitions})")
    @DisplayName("Сгенерированная модель должна быть из списка разрешённых")
    void testAllowedModels() {
        Car car = generator.get();
        assertTrue(
                Arrays.asList(CarGenerator.MODELS).contains(car.getModel()),
                "В генераторе не подразумевалась модель, которая в итоге была прописана: " + car.getModel()
        );
    }

    @RepeatedTest(value = 10, name = "Проверка мощности ({currentRepetition}/{totalRepetitions})")
    @DisplayName("Мощность должна быть в разрешённом диапазоне")
    void testAllowedPower() {
        Car car = generator.get();
        boolean inRange = CarGenerator.POWER_MIN <= car.getPower() && car.getPower() <= CarGenerator.POWER_MAX;
        assertTrue(
                inRange,
                "Разрешенная мощность от " + CarGenerator.POWER_MIN + " до " + CarGenerator.POWER_MAX
                + ", получено: " + car.getPower()
        );
    }

    @RepeatedTest(value = 10, name = "Проверка года ({currentRepetition}/{totalRepetitions})")
    @DisplayName("Год выпуска должен быть в разрешённом диапазоне")
    void testAllowedYear() {
        Car car = generator.get();
        boolean inRange = CarGenerator.YEAR_MIN <= car.getProductionYear() && car.getProductionYear() <= CarGenerator.YEAR_MAX;
        assertTrue(
                inRange,
                "Год возможен от " + CarGenerator.YEAR_MIN + " до " + CarGenerator.YEAR_MAX
                + ", получено: " + car.getProductionYear()
        );
    }
}
