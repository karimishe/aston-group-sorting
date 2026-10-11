package sorting.source;

import sorting.collection.MyArrayList;
import sorting.model.Car;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RandomCarSourceTest {
    private static int checks;

    public static void main(String[] args) {
        check(RandomCarSourceTest::testWithLengthNegative,
                "Ожидалось исключение IllegalArgumentException для отрицательного числа");
        check(RandomCarSourceTest::testWithLength0,
                "Ожидалось исключение IllegalArgumentException для нуля");
        check(RandomCarSourceTest::testWithLength5,
                "Размер коллекции должен был составить 5 машин");
        check(RandomCarSourceTest::testWithLength256,
                "Размер коллекции должен был составить 256 машин");

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void check(Supplier<Boolean> condition, String errorMessage) {
        if (condition.get()) {
            checks++;
        } else {
            throw new AssertionError(errorMessage);
        }
    }

    private static boolean testWithLengthNegative() {
        RandomCarSource generator = new RandomCarSource();
        try {
            generator.load(-1);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Количество автомобилей должно быть положительным.")) {
                return true;
            }
        }
        return false;
    }

    private static boolean testWithLength0() {
        RandomCarSource generator = new RandomCarSource();
        try {
            generator.load(0);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Количество автомобилей должно быть положительным.")) {
                return true;
            }
        }
        return false;
    }

    private static boolean testWithLength5() {
        RandomCarSource generator = new RandomCarSource();
        try {
            if (generator.load(5).size() == 5) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private static boolean testWithLength256() {
        RandomCarSource generator = new RandomCarSource();
        try {
            if (generator.load(256).size() == 256) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }
}
