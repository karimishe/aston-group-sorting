package sorting.algorithm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import org.junit.jupiter.api.Test;
import sorting.collection.MyArrayList;
import sorting.compare.CarAllFieldsComparator;
import sorting.contract.SortStrategy;
import sorting.model.Car;

public class InsertionSortTest {
    @Test
    void keepsEmptyCollection() {
        assertSorted(new int[] {}, new int[] {});
    }

    @Test
    void keepsSingleElement() {
        assertSorted(new int[] {7}, new int[] {7});
    }

    @Test
    void keepsAlreadySortedValues() {
        assertSorted(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4});
    }

    @Test
    void sortsReverseOrder() {
        assertSorted(new int[] {4, 3, 2, 1}, new int[] {1, 2, 3, 4});
    }

    @Test
    void sortsNegativeValuesAndKeepsDuplicates() {
        assertSorted(new int[] {3, -1, 0, 3, -1, 2}, new int[] {-1, -1, 0, 2, 3, 3});
    }

    @Test
    void keepsEqualValues() {
        assertSorted(new int[] {5, 5, 5}, new int[] {5, 5, 5});
    }

    @Test
    void sortsIntegerBoundaries() {
        assertSorted(new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE},
                new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
    }

    @Test
    void sortsCollectionLargerThanInitialCapacity() {
        assertSorted(new int[] {12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
    }

    @Test
    void usesDescendingComparator() {
        MyArrayList<Integer> values = numbers(1, 3, 2, 3, -1);
        Comparator<Integer> descending = (first, second) -> Integer.compare(second, first);

        new InsertionSort<Integer>().sort(values, descending);

        assertNumbers(values, 3, 3, 2, 1, -1);
    }

    @Test
    void sortsStringsWithTheirComparator() {
        MyArrayList<String> words = new MyArrayList<>();
        words.add("Volvo");
        words.add("Audi");
        words.add("Kia");

        new InsertionSort<String>().sort(words, String::compareTo);

        assertEquals(3, words.size());
        assertEquals("Audi", words.get(0));
        assertEquals("Kia", words.get(1));
        assertEquals("Volvo", words.get(2));
    }

    @Test
    void sortsCarsByAllFieldsAndPreservesReferencesAndOriginal() {
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

        assertEquals(initial.length, result.size(), "Сортировка сохраняет размер коллекции");
        assertSame(lowerPower, result.get(0), "Сначала сравнивается мощность");
        assertSame(earlierModel, result.get(1), "При одинаковой мощности решает модель");
        assertSame(newer, result.get(5), "При одинаковой мощности и модели решает год");
        assertSame(higherPower, result.get(6), "Максимальная мощность идёт последней");

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
        assertEquals(2, olderReferences, "Сохраняются обе ссылки на один объект");
        assertEquals(1, sameFieldsReferences, "Сохраняется отдельный объект с такими же полями");
        assertEquals(initial.length, original.size());
        for (int i = 0; i < initial.length; i++) {
            assertSame(initial[i], original.get(i), "Исходная позиция " + i + " не изменилась");
        }
    }

    @Test
    void rejectsNullCollection() {
        SortStrategy<Integer> strategy = new InsertionSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(null, Integer::compare));
    }

    @Test
    void rejectsBothNullArguments() {
        SortStrategy<Integer> strategy = new InsertionSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(null, null));
    }

    @Test
    void rejectsNullComparatorForEmptyCollection() {
        SortStrategy<Integer> strategy = new InsertionSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(new MyArrayList<>(), null));
    }

    @Test
    void rejectsNullComparatorForSingleElement() {
        SortStrategy<Integer> strategy = new InsertionSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(numbers(1), null));
    }

    @Test
    void rejectsNullComparatorWithoutChangingCollection() {
        MyArrayList<Integer> values = numbers(2, 1);
        SortStrategy<Integer> strategy = new InsertionSort<>();

        assertThrows(NullPointerException.class, () -> strategy.sort(values, null));
        assertNumbers(values, 2, 1);
    }

    private static void assertSorted(int[] initial, int[] expected) {
        MyArrayList<Integer> values = numbers(initial);

        new InsertionSort<Integer>().sort(values, Integer::compare);

        assertNumbers(values, expected);
    }

    private static void assertNumbers(MyArrayList<Integer> values, int... expected) {
        int[] actual = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            actual[i] = values.get(i);
        }
        assertArrayEquals(expected, actual);
    }

    private static MyArrayList<Integer> numbers(int... initial) {
        MyArrayList<Integer> values = new MyArrayList<>();
        for (int value : initial) {
            values.add(value);
        }
        return values;
    }

    private static Car car(int power, String model, int productionYear) {
        return new Car.Builder()
                .power(power)
                .model(model)
                .productionYear(productionYear)
                .build();
    }
}
