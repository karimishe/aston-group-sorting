package sorting.compare;

import java.time.LocalDate;
import java.util.Comparator;
import sorting.model.Car;

public class CarAllFieldsComparatorTest {
    private static int checks;

    public static void main(String[] args) {
        Comparator<Car> comparator = new CarAllFieldsComparator();
        int currentYear = LocalDate.now().getYear();

        Car lowerPower = car(100, "Volvo", 2020);
        Car higherPower = car(200, "Audi", 1900);
        check(comparator.compare(lowerPower, higherPower) < 0,
                "Мощность важнее модели и года: меньшая мощность идёт раньше");
        check(comparator.compare(higherPower, lowerPower) > 0,
                "Мощность важнее модели и года: большая мощность идёт позже");

        Car earlierModel = car(100, "Audi", 2020);
        Car laterModel = car(100, "Volvo", 1900);
        check(comparator.compare(earlierModel, laterModel) < 0,
                "При равной мощности модель важнее года: Audi идёт раньше Volvo");
        check(comparator.compare(laterModel, earlierModel) > 0,
                "При равной мощности модель важнее года: Volvo идёт позже Audi");

        Car older = car(100, "Audi", 2018);
        Car newer = car(100, "Audi", 2020);
        check(comparator.compare(older, newer) < 0,
                "При равных мощности и модели меньший год идёт раньше");
        check(comparator.compare(newer, older) > 0,
                "При равных мощности и модели больший год идёт позже");

        Car sameFields = car(100, "Audi", 2018);
        check(comparator.compare(older, sameFields) == 0,
                "Разные объекты с одинаковыми полями равны по порядку");
        check(comparator.compare(older, older) == 0,
                "Автомобиль равен самому себе по порядку");

        Car uppercase = car(100, "Audi", 2020);
        Car lowercase = car(100, "audi", 2020);
        check(comparator.compare(uppercase, lowercase) < 0,
                "Сравнение моделей учитывает регистр: Audi идёт раньше audi");
        check(comparator.compare(lowercase, uppercase) > 0,
                "Сравнение моделей учитывает регистр: audi идёт позже Audi");

        Car lada = car(100, "Лада", 2020);
        Car moskvich = car(100, "Москвич", 2020);
        check(comparator.compare(lada, moskvich) < 0,
                "Сравниваются модели на кириллице: Лада идёт раньше Москвич");
        check(comparator.compare(moskvich, lada) > 0,
                "Сравниваются модели на кириллице: Москвич идёт позже Лада");

        Car minimumPower = car(1, "Ford", currentYear);
        Car maximumPower = car(Integer.MAX_VALUE, "Ford", 1900);
        check(comparator.compare(minimumPower, maximumPower) < 0,
                "Минимальная допустимая мощность меньше максимальной");
        check(comparator.compare(maximumPower, minimumPower) > 0,
                "Максимальная допустимая мощность больше минимальной");

        Car minimumYear = car(100, "Ford", 1900);
        Car maximumYear = car(100, "Ford", currentYear);
        check(comparator.compare(minimumYear, maximumYear) < 0,
                "Первый допустимый год идёт раньше текущего");
        check(comparator.compare(maximumYear, minimumYear) > 0,
                "Текущий год идёт позже первого допустимого");

        expectNull(comparator, null, older, "Первый автомобиль null");
        expectNull(comparator, older, null, "Второй автомобиль null");
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
