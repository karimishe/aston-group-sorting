package sorting.compare;

import java.util.Comparator;
import sorting.model.Car;

public class CarPowerComparatorTest {
    private static int checks;

    public static void main(String[] args) {
        Comparator<Car> comparator = new CarPowerComparator();

        Car lowerPower = car(100, "Volvo", 2020);
        Car mediumPower = car(150, "Audi", 2018);
        Car higherPower = car(200, "Kia", 2022);

        check(comparator.compare(lowerPower, mediumPower) < 0,
                "Меньшая мощность идёт раньше большей");
        check(comparator.compare(mediumPower, higherPower) < 0,
                "Средняя мощность идёт раньше большей");
        check(comparator.compare(higherPower, lowerPower) > 0,
                "Большая мощность идёт позже меньшей");

        Car samePowerDifferentFields = car(100, "Audi", 1900);
        check(comparator.compare(lowerPower, samePowerDifferentFields) == 0,
                "Одинаковая мощность даёт 0 независимо от модели и года");
        check(comparator.compare(lowerPower, lowerPower) == 0,
                "Автомобиль равен самому себе по порядку");

        Car minimumPower = car(1, "Ford", 2020);
        Car maximumPower = car(Integer.MAX_VALUE, "Ford", 1900);
        check(comparator.compare(minimumPower, maximumPower) < 0,
                "Минимальная мощность меньше максимальной");
        check(comparator.compare(maximumPower, minimumPower) > 0,
                "Максимальная мощность больше минимальной");

        expectNull(comparator, null, lowerPower, "Первый автомобиль null");
        expectNull(comparator, lowerPower, null, "Второй автомобиль null");
        expectNull(comparator, null, null, "Оба автомобиля null");

        System.out.println("Проверок пройдено: " + checks);
    }

    private static Car car(int power, String model, int productionYear) {
        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(productionYear)
                .build();
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }

    private static void expectNull(Comparator<Car> comparator, Car first, Car second,
                                   String description) {
        try {
            comparator.compare(first, second);
        } catch (NullPointerException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидался NullPointerException: " + description);
    }
}
