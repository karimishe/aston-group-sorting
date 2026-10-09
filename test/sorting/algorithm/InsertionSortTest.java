package sorting.algorithm;

import java.util.Comparator;
import sorting.collection.MyArrayList;
import sorting.compare.CarAllFieldsComparator;
import sorting.contract.SortStrategy;
import sorting.model.Car;

public class InsertionSortTest {
    private static int checks;

    public static void main(String[] args) {
        checkIntegerCases();
        checkOtherComparators();
        checkCarsAndCopy();
        checkNullArguments();

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkIntegerCases() {
        sortAndCheck(new int[] {}, new int[] {}, "Пустая коллекция");
        sortAndCheck(new int[] {7}, new int[] {7}, "Один элемент");
        sortAndCheck(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4},
                "Уже отсортированная коллекция");
        sortAndCheck(new int[] {4, 3, 2, 1}, new int[] {1, 2, 3, 4},
                "Обратный порядок");
        sortAndCheck(new int[] {3, -1, 0, 3, -1, 2}, new int[] {-1, -1, 0, 2, 3, 3},
                "Смешанные значения с повторениями");
        sortAndCheck(new int[] {5, 5, 5}, new int[] {5, 5, 5}, "Все значения одинаковые");
        sortAndCheck(new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE},
                new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, "Границы int");
        sortAndCheck(new int[] {12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
                "Коллекция больше начальной вместимости");
    }

    private static void sortAndCheck(int[] initial, int[] expected, String description) {
        MyArrayList<Integer> values = numbers(initial);
        SortStrategy<Integer> strategy = new InsertionSort<>();
        strategy.sort(values, Integer::compare);
        checkNumbers(values, expected, description);
    }

    private static void checkOtherComparators() {
        MyArrayList<Integer> values = numbers(1, 3, 2, 3, -1);
        Comparator<Integer> descending = (first, second) -> Integer.compare(second, first);
        new InsertionSort<Integer>().sort(values, descending);
        checkNumbers(values, new int[] {3, 3, 2, 1, -1}, "Компаратор задаёт обратный порядок");

        MyArrayList<String> words = new MyArrayList<>();
        words.add("Volvo");
        words.add("Audi");
        words.add("Kia");
        new InsertionSort<String>().sort(words, String::compareTo);
        check(words.size() == 3 && "Audi".equals(words.get(0))
                && "Kia".equals(words.get(1)) && "Volvo".equals(words.get(2)),
                "Тот же алгоритм сортирует строки с помощью другого компаратора");
    }

    private static void checkCarsAndCopy() {
        Car lowerPower = car(100, "Volvo", 2022);
        Car higherPower = car(200, "Audi", 1900);
        Car earlierModel = car(150, "Audi", 2022);
        Car older = car(150, "Kia", 2018);
        Car newer = car(150, "Kia", 2022);
        Car sameFields = car(150, "Kia", 2018);
        Car[] initial = {higherPower, newer, earlierModel, older, sameFields, older, lowerPower};
        MyArrayList<Car> original = new MyArrayList<>();
        for (Car car : initial) {
            original.add(car);
        }

        MyArrayList<Car> result = original.copy();
        new InsertionSort<Car>().sort(result, new CarAllFieldsComparator());

        check(result.size() == initial.length, "Сортировка сохраняет размер коллекции");
        check(result.get(0) == lowerPower && result.get(6) == higherPower,
                "Мощность имеет приоритет перед моделью и годом");
        check(result.get(1) == earlierModel, "При одинаковой мощности решает модель");
        check(result.get(5) == newer, "При одинаковой мощности и модели решает год");

        int olderReferences = 0;
        int sameFieldsReferences = 0;
        for (int i = 2; i <= 4; i++) {
            if (result.get(i) == older) {
                olderReferences++;
            }
            if (result.get(i) == sameFields) {
                sameFieldsReferences++;
            }
        }
        check(olderReferences == 2 && sameFieldsReferences == 1,
                "Сохраняются обе повторные ссылки и отдельный объект с такими же полями");

        boolean originalUnchanged = original.size() == initial.length;
        for (int i = 0; i < initial.length; i++) {
            if (original.get(i) != initial[i]) {
                originalUnchanged = false;
            }
        }
        check(originalUnchanged, "Сортировка копии сохраняет исходную коллекцию и её порядок");
    }

    private static void checkNullArguments() {
        SortStrategy<Integer> strategy = new InsertionSort<>();
        expectNull(() -> strategy.sort(null, Integer::compare), "Коллекция null");
        expectNull(() -> strategy.sort(null, null), "Оба аргумента null");
        expectNull(() -> strategy.sort(new MyArrayList<>(), null),
                "Компаратор null для пустой коллекции");
        expectNull(() -> strategy.sort(numbers(1), null),
                "Компаратор null для одного элемента");
        MyArrayList<Integer> values = numbers(2, 1);
        expectNull(() -> strategy.sort(values, null), "Компаратор null для двух элементов");
        checkNumbers(values, new int[] {2, 1}, "Ошибка аргумента не меняет коллекцию");
    }

    private static MyArrayList<Integer> numbers(int... initial) {
        MyArrayList<Integer> values = new MyArrayList<>();
        for (int value : initial) {
            values.add(value);
        }
        return values;
    }

    private static void checkNumbers(MyArrayList<Integer> values, int[] expected,
                                     String description) {
        check(values.size() == expected.length, description + ": размер");
        for (int i = 0; i < expected.length; i++) {
            if (values.get(i) != expected[i]) {
                throw new AssertionError(description + ": неверное значение на позиции " + i);
            }
        }
        check(true, description + ": порядок и значения");
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

    private static void expectNull(Runnable action, String description) {
        try {
            action.run();
        } catch (NullPointerException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидался NullPointerException: " + description);
    }
}
