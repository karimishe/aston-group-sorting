package sorting.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import sorting.collection.MyArrayList;
import sorting.model.Car;

class EvenValueSorterJUnitTest {

    @Test
    void emptyList() {
        MyArrayList<Integer> list = new MyArrayList<>();

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new InsertionSort<>());

        assertEquals(0, list.size());
    }

    @Test
    void exampleFromTask() {
        MyArrayList<Integer> list = numbers(8, 3, 2, 5, 4);

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new InsertionSort<>());

        assertEquals(Arrays.asList(2, 3, 4, 5, 8), toList(list));
    }

    @Test
    void onlyEvenNumbers() {
        MyArrayList<Integer> list = numbers(8, 6, 4, 2);

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new InsertionSort<>());

        assertEquals(Arrays.asList(2, 4, 6, 8), toList(list));
    }

    @Test
    void onlyOddNumbersStayTheSame() {
        MyArrayList<Integer> list = numbers(9, 3, 7, 1);

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new InsertionSort<>());

        assertEquals(Arrays.asList(9, 3, 7, 1), toList(list));
    }

    @Test
    void duplicatesAreKept() {
        MyArrayList<Integer> list = numbers(4, 2, 4, 3, 2);

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new InsertionSort<>());

        assertEquals(Arrays.asList(2, 2, 4, 3, 4), toList(list));
    }

    @Test
    void oddCarsStayOnTheirPlaces() {
        Car car1 = car(6, "Volvo");
        Car car2 = car(9, "Audi");
        Car car3 = car(4, "Kia");
        Car car4 = car(7, "Lada");
        MyArrayList<Car> cars = new MyArrayList<>();
        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);

        new EvenValueSorter<Car>().sort(cars, Car::getPower, new InsertionSort<>());

        assertEquals(4, cars.size());
        assertSame(car3, cars.get(0));
        assertSame(car2, cars.get(1));
        assertSame(car1, cars.get(2));
        assertSame(car4, cars.get(3));
    }

    @Test
    void worksWithBubbleSortToo() {
        MyArrayList<Integer> list = numbers(8, 3, 2, 5, 4);

        new EvenValueSorter<Integer>().sort(list, Integer::intValue, new BubbleSort<>());

        assertEquals(Arrays.asList(2, 3, 4, 5, 8), toList(list));
    }

    @Test
    void nullParametersThrowException() {
        EvenValueSorter<Integer> sorter = new EvenValueSorter<>();
        MyArrayList<Integer> list = numbers(2, 1);

        assertThrows(NullPointerException.class,
                () -> sorter.sort(null, Integer::intValue, new InsertionSort<>()));
        assertThrows(NullPointerException.class,
                () -> sorter.sort(list, null, new InsertionSort<>()));
        assertThrows(NullPointerException.class,
                () -> sorter.sort(list, Integer::intValue, null));
    }

    private static MyArrayList<Integer> numbers(int... values) {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int value : values) {
            list.add(value);
        }
        return list;
    }

    private static List<Integer> toList(MyArrayList<Integer> list) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i));
        }
        return result;
    }

    private static Car car(int power, String model) {
        return new Car.Builder().power(power).model(model).productionYear(2020).build();
    }
}
