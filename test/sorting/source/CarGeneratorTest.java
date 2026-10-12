package sorting.source;

import sorting.model.Car;

import java.util.Arrays;
import java.util.function.Supplier;

public class CarGeneratorTest {
    private static int checks;

    public static void main(String[] args) {
        check(CarGeneratorTest::testAllowedModels,
                "В генераторе не подразумевалась модель, которая в итоге была прописана");
        check(CarGeneratorTest::testAllowedPower,
                "Разрешенная мощность от " + CarGenerator.POWER_MIN + " до " + CarGenerator.POWER_MAX);
        check(CarGeneratorTest::testAllowedYear,
                "Год возможен от " + CarGenerator.YEAR_MIN + " до " + CarGenerator.YEAR_MAX);

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void check(Supplier<Boolean> condition, String errorMessage) {
        if (condition.get()) {
            checks++;
        } else {
            throw new AssertionError(errorMessage);
        }
    }

    private static boolean testAllowedModels() {
        CarGenerator generator = new CarGenerator();
        Car car = generator.get();
        return Arrays.asList(CarGenerator.MODELS).contains(car.getModel());
    }

    private static boolean testAllowedPower() {
        CarGenerator generator = new CarGenerator();
        Car car = generator.get();
        return CarGenerator.POWER_MIN <= car.getPower() && CarGenerator.POWER_MAX >= car.getPower();
    }

    private static boolean testAllowedYear() {
        CarGenerator generator = new CarGenerator();
        Car car = generator.get();
        return CarGenerator.YEAR_MIN <= car.getProductionYear() && CarGenerator.YEAR_MAX >= car.getProductionYear();
    }
}
