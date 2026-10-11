package sorting.compare;

import sorting.model.Car;

import java.util.function.Supplier;

public class CarProductionYearComparatorTest {
    private static int checks;
    private static final CarProductionYearComparator comparator = new CarProductionYearComparator();

    public static void main(String[] args) {
        check(CarProductionYearComparatorTest::testLt,
                "При сравнении получилось, что 2007 не идет до 2018");
        check(CarProductionYearComparatorTest::testEq,
                "При сравнении получилось, что 2018 не равен 2018");
        check(CarProductionYearComparatorTest::testGt,
                "При сравнении получилось, что 2025 не идет после 2018");

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void check(Supplier<Boolean> condition, String errorMessage) {
        if (condition.get()) {
            checks++;
        } else {
            throw new AssertionError(errorMessage);
        }
    }

    private static Car makeCar(int year) {
        return new Car.Builder()
                .power(150)
                .model("Car")
                .productionYear(year)
                .build();
    }

    private static boolean testLt() {
        int result = comparator.compare(makeCar(2007), makeCar(2018));
        return result < 0;
    }

    private static boolean testEq() {
        int result = comparator.compare(makeCar(2018), makeCar(2018));
        return result == 0;
    }

    private static boolean testGt() {
        int result = comparator.compare(makeCar(2025), makeCar(2018));
        return result > 0;
    }
}
