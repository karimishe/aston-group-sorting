package algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import algorithm.BubbleSort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import collection.MyArrayList;
import compare.CarAllFieldsComparator;
import contract.SortStrategy;
import model.Car;

class BubbleSortJUnitTest {

    // ---------- Integer ----------

    @Test
    @DisplayName("Пустая коллекция остаётся пустой")
    void emptyCollection() {
        assertEquals(Collections.emptyList(), sortIntegers());
    }

    @Test
    @DisplayName("Один элемент остаётся на месте")
    void singleElement() {
        assertEquals(List.of(7), sortIntegers(7));
    }

    @Test
    @DisplayName("Прямой порядок: отсортированная коллекция не меняется")
    void alreadySorted() {
        assertEquals(Arrays.asList(1, 2, 3, 4), sortIntegers(1, 2, 3, 4));
    }

    @Test
    @DisplayName("Обратный порядок")
    void reverseOrder() {
        assertEquals(Arrays.asList(1, 2, 3, 4), sortIntegers(4, 3, 2, 1));
    }

    @Test
    @DisplayName("Повторяющиеся значения сохраняются")
    void duplicatesAreKept() {
        assertEquals(Arrays.asList(-1, -1, 0, 2, 3, 3), sortIntegers(3, -1, 0, 3, -1, 2));
    }

    @Test
    @DisplayName("Другой компаратор: обратный порядок")
    void descendingComparator() {
        MyArrayList<Integer> values = numbers(1, 3, 2, 3, -1);
        Comparator<Integer> descending = (first, second) -> Integer.compare(second, first);

        new BubbleSort<Integer>().sort(values, descending);

        assertEquals(Arrays.asList(3, 3, 2, 1, -1), toList(values));
    }

    // ---------- Car ----------

    @Test
    @DisplayName("Car: мощность важнее модели, модель важнее года")
    void carsAreOrderedByAllFields() {
        Fixture fixture = new Fixture();
        MyArrayList<Car> result = fixture.original.copy();

        new BubbleSort<Car>().sort(result, new CarAllFieldsComparator());

        assertEquals(fixture.initial.length, result.size(), "Размер коллекции сохранился");
        assertSame(fixture.lowerPower, result.get(0), "Наименьшая мощность первой");
        assertSame(fixture.earlierModel, result.get(1), "При равной мощности решает модель");
        assertSame(fixture.newer, result.get(5), "При равной мощности и модели решает год");
        assertSame(fixture.higherPower, result.get(6), "Наибольшая мощность последней");
    }

    @Test
    @DisplayName("Car: сохраняются повторные ссылки и объект с такими же полями")
    void carsKeepDuplicates() {
        Fixture fixture = new Fixture();
        MyArrayList<Car> result = fixture.original.copy();

        new BubbleSort<Car>().sort(result, new CarAllFieldsComparator());

        int olderReferences = 0;
        int sameFieldsReferences = 0;
        for (int i = 2; i <= 4; i++) {
            if (result.get(i) == fixture.older) {
                olderReferences++;
            }
            if (result.get(i) == fixture.sameFields) {
                sameFieldsReferences++;
            }
        }
        assertEquals(2, olderReferences, "Обе ссылки на один и тот же объект");
        assertEquals(1, sameFieldsReferences, "Отдельный объект с такими же полями");
    }

    @Test
    @DisplayName("Car: другой компаратор (по году) даёт другой порядок")
    void carsSortedByYearOnly() {
        Fixture fixture = new Fixture();
        Comparator<Car> byYear = Comparator.comparingInt(Car::getProductionYear);
        MyArrayList<Car> result = fixture.original.copy();

        new BubbleSort<Car>().sort(result, byYear);

        assertSame(fixture.higherPower, result.get(0), "Год 1900 первым");
        for (int i = 1; i < result.size(); i++) {
            assertTrue(byYear.compare(result.get(i - 1), result.get(i)) <= 0,
                    "Годы не убывают на позиции " + i);
        }
    }

    @Test
    @DisplayName("Коллекция null → NullPointerException")
    void nullCollection() {
        SortStrategy<Integer> strategy = new BubbleSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(null, Integer::compare));
    }

    @Test
    void rejectsNullComparatorForEmptySingleAndMultipleElements() {
        SortStrategy<Integer> strategy = new BubbleSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(numbers(), null));
        assertThrows(NullPointerException.class, () -> strategy.sort(numbers(1), null));
        assertThrows(NullPointerException.class, () -> strategy.sort(numbers(2, 1), null));
    }

    // ---------- Вспомогательные методы ----------

    private static List<Integer> sortIntegers(int... initial) {
        MyArrayList<Integer> values = numbers(initial);
        SortStrategy<Integer> strategy = new BubbleSort<>();
        strategy.sort(values, Integer::compare);
        return toList(values);
    }

    private static MyArrayList<Integer> numbers(int... initial) {
        MyArrayList<Integer> values = new MyArrayList<>();
        for (int value : initial) {
            values.add(value);
        }
        return values;
    }

    private static <T> List<T> toList(MyArrayList<T> values) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i));
        }
        return result;
    }

    private static Car car(int power, String model, int productionYear) {
        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(productionYear)
                .build();
    }

    private static final class Fixture {
        final Car lowerPower = car(100, "Volvo", 2022);
        final Car higherPower = car(200, "Audi", 1900);
        final Car earlierModel = car(150, "Audi", 2022);
        final Car older = car(150, "Kia", 2018);
        final Car newer = car(150, "Kia", 2022);
        final Car sameFields = car(150, "Kia", 2018);
        final Car[] initial = {higherPower, newer, earlierModel, older, sameFields, older, lowerPower};
        final MyArrayList<Car> original = new MyArrayList<>();

        Fixture() {
            for (Car car : initial) {
                original.add(car);
            }
        }
    }
}
