package sorting.source;

import sorting.collection.MyArrayList;
import sorting.model.Car;

public class CarCounterTest {
    private static int checks;

    public static void main(String[] args) {
        CarCounter counter = new CarCounter();
        MyArrayList<Car> cars = new MyArrayList<>();

        // кол-во одинаковых автомобилей
        int cars1 = 7;
        int cars2 = 15;
        int cars3 = 1;
        int cars4 = 10;
        int cars5 = 0;

        for (int i = 0; i < cars1; i++) {
            cars.add(makeCar1());
        }
        for (int i = 0; i < cars2; i++) {
            cars.add(makeCar2());
        }
        for (int i = 0; i < cars3; i++) {
            cars.add(makeCar3());
        }
        for (int i = 0; i < cars4; i++) {
            cars.add(makeCar4());
        }

        Car car1 = makeCar1();
        Car car2 = makeCar2();
        Car car3 = makeCar3();
        Car car4 = makeCar4();
        Car car5 = makeCar5();

        // проверки на корректность подсчетов
        try {
            check(counter.count(cars, car1, 1) == cars1, "Неверный подсчёт. потоков: 1, авто: 1");
            check(counter.count(cars, car2, 1) == cars2, "Неверный подсчёт. потоков: 1, авто: 2");
            check(counter.count(cars, car3, 1) == cars3, "Неверный подсчёт. потоков: 1, авто: 3");
            check(counter.count(cars, car4, 1) == cars4, "Неверный подсчёт. потоков: 1, авто: 4");
            check(counter.count(cars, car5, 1) == cars5, "Неверный подсчёт. потоков: 1, авто: 5");

            check(counter.count(cars, car1, 2) == cars1, "Неверный подсчёт. потоков: 2, авто: 1");
            check(counter.count(cars, car2, 2) == cars2, "Неверный подсчёт. потоков: 2, авто: 2");
            check(counter.count(cars, car3, 2) == cars3, "Неверный подсчёт. потоков: 2, авто: 3");
            check(counter.count(cars, car4, 2) == cars4, "Неверный подсчёт. потоков: 2, авто: 4");
            check(counter.count(cars, car5, 2) == cars5, "Неверный подсчёт. потоков: 2, авто: 5");

            check(counter.count(cars, car1, 3) == cars1, "Неверный подсчёт. потоков: 3, авто: 1");
            check(counter.count(cars, car2, 3) == cars2, "Неверный подсчёт. потоков: 3, авто: 2");
            check(counter.count(cars, car3, 3) == cars3, "Неверный подсчёт. потоков: 3, авто: 3");
            check(counter.count(cars, car4, 3) == cars4, "Неверный подсчёт. потоков: 3, авто: 4");
            check(counter.count(cars, car5, 3) == cars5, "Неверный подсчёт. потоков: 3, авто: 5");

            check(counter.count(cars, car1, 4) == cars1, "Неверный подсчёт. потоков: 4, авто: 1");
            check(counter.count(cars, car2, 4) == cars2, "Неверный подсчёт. потоков: 4, авто: 2");
            check(counter.count(cars, car3, 4) == cars3, "Неверный подсчёт. потоков: 4, авто: 3");
            check(counter.count(cars, car4, 4) == cars4, "Неверный подсчёт. потоков: 4, авто: 4");
            check(counter.count(cars, car5, 4) == cars5, "Неверный подсчёт. потоков: 4, авто: 5");

            check(counter.count(cars, car1, 7) == cars1, "Неверный подсчёт. потоков: 7, авто: 1");
            check(counter.count(cars, car2, 7) == cars2, "Неверный подсчёт. потоков: 7, авто: 2");
            check(counter.count(cars, car3, 7) == cars3, "Неверный подсчёт. потоков: 7, авто: 3");
            check(counter.count(cars, car4, 7) == cars4, "Неверный подсчёт. потоков: 7, авто: 4");
            check(counter.count(cars, car5, 7) == cars5, "Неверный подсчёт. потоков: 7, авто: 5");

        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }

        // проверка на нулевое кол-во потоков
        try {
            counter.count(cars, car1, 0);
            throw new AssertionError("Ожидалось IllegalArgumentException для кол-ва потоков = 0");
        } catch (IllegalArgumentException e) {
            checks++;
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }

        // проверка на отрицательное кол-во потоков
        try {
            counter.count(cars, car1, -1);
            throw new AssertionError("Ожидалось IllegalArgumentException для кол-ва потоков = -1");
        } catch (IllegalArgumentException e) {
            checks++;
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }

        // проверка на прерывание потоков
        Thread.currentThread().interrupt();
        try {
            counter.count(cars, car1, 1);
            throw new AssertionError("Ожидалось InterruptedException");
        } catch (InterruptedException e) {
            checks++;
        } finally {
            Thread.interrupted();
        }

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void check(boolean success, String errorMessage) {
        if (success) {
            checks++;
        } else {
            throw new AssertionError(errorMessage);
        }
    }

    private static Car makeCar1() {
        return new Car.Builder().power(11).model("Car").productionYear(2010).build();
    }

    private static Car makeCar2() {
        return new Car.Builder().power(22).model("Car").productionYear(2010).build();
    }

    private static Car makeCar3() {
        return new Car.Builder().power(11).model("raC").productionYear(2010).build();
    }

    private static Car makeCar4() {
        return new Car.Builder().power(11).model("Car").productionYear(1999).build();
    }

    private static Car makeCar5() {
        return new Car.Builder().power(22).model("raC").productionYear(1999).build();
    }
}
