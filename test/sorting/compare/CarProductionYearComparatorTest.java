package sorting.compare;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sorting.model.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarProductionYearComparatorTest {

    private static final CarProductionYearComparator COMPARATOR = new CarProductionYearComparator();

    @Test
    @DisplayName("2007 идёт до 2018")
    void testLt() {
        int result = COMPARATOR.compare(makeCar(2007), makeCar(2018));
        assertTrue(result < 0, "При сравнении получилось, что 2007 не идет до 2018");
    }

    @Test
    @DisplayName("2018 равен 2018")
    void testEq() {
        int result = COMPARATOR.compare(makeCar(2018), makeCar(2018));
        assertEquals(0, result, "При сравнении получилось, что 2018 не равен 2018");
    }

    @Test
    @DisplayName("2025 идёт после 2018")
    void testGt() {
        int result = COMPARATOR.compare(makeCar(2025), makeCar(2018));
        assertTrue(result > 0, "При сравнении получилось, что 2025 не идет после 2018");
    }

    @Test
    @DisplayName("NullPointerException, если первый элемент null")
    void testNullFirst() {
        assertThrows(NullPointerException.class,
                () -> COMPARATOR.compare(null, makeCar(2018)),
                "Ожидалось исключение NullPointerException, т.к. первый элемент передан как null");
    }

    @Test
    @DisplayName("NullPointerException, если второй элемент null")
    void testNullSecond() {
        assertThrows(NullPointerException.class,
                () -> COMPARATOR.compare(makeCar(2018), null),
                "Ожидалось исключение NullPointerException, т.к. второй элемент передан как null");
    }

    @Test
    @DisplayName("NullPointerException, если оба элемента null")
    void testNullTwo() {
        assertThrows(NullPointerException.class,
                () -> COMPARATOR.compare(null, null),
                "Ожидалось исключение NullPointerException, т.к. оба элемента переданы как null");
    }

    private static Car makeCar(int year) {
        return new Car.Builder()
                .power(150)
                .model("Car")
                .productionYear(year)
                .build();
    }
}
