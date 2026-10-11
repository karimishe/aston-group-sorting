package sorting.input;
import sorting.collection.MyArrayList;
import sorting.model.Car;
import sorting.source.ManualCarSource;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.Scanner;

public class ManualCarSourceTest {
    private static int checks;
    private static PrintStream originalOut;
    public static void main(String[] args) throws IOException {
        originalOut = System.out;
        System.out.println("Запуск тестов ручного ввода");

        testCorrectInput();
        testMultipleCarsInput();
        testRetryAfterInvalidPower();
        testRetryAfterInvalidYear();
        testEmptyModel();
        testInvalidNumbers();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
    private static void runTest(String testName, String input, Runnable testLogic) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            ManualCarSource source = new ManualCarSource();
            testLogic.run();
            System.setOut(originalOut);
            System.out.println(testName + ": пройден");
        } finally {
            System.setOut(originalOut);
        }
    }
    private static void testCorrectInput() {
        runTest("Корректный ввод одного автомобиля",
                "150\nToyota Camry\n2020\nвыход\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("150\nToyota Camry\n2020\n".getBytes())));
                    MyArrayList<Car> cars;
                    try{
                        cars = source.load(1);
                        check(cars.size() == 1, "Добавлен один автомобиль");
                        check("Toyota Camry".equals(cars.get(0).getModel()), "Модель корректна");
                        check(cars.get(0).getPower() == 150, "Мощность корректна");
                        check(cars.get(0).getProductionYear() == 2020, "Год корректен");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
    }
    private static void testMultipleCarsInput() {
        runTest("Корректный ввод нескольких автомобилей",
                "BMW X5\n300\n2021\nAudi A4\n200\n2019\nвыход\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("300\nBMW X5\n2021\n200\nAudi A4\n2019\n".getBytes())));
                    MyArrayList<Car> cars;
                    try {
                        cars = source.load(2);
                        check(cars.size() == 2, "Добавлено два автомобиля");
                        check("BMW X5".equals(cars.get(0).getModel()), "Первая модель корректна");
                        check(cars.get(0).getPower() == 300, "Первая мощность корректна");
                        check("Audi A4".equals(cars.get(1).getModel()), "Вторая модель корректна");
                        check(cars.get(1).getProductionYear() == 2019, "Второй год корректен");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
}

    private static void testRetryAfterInvalidPower() {
        runTest( "Повтор после ошибки ввода мощности",
                "abc\n120\nHonda Civic\n2018\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("abc\n120\nHonda Civic\n2018\n".getBytes())));
                    MyArrayList<Car> cars;
                    try {
                        cars = source.load(1);
                        check(cars.size() == 1, "Автомобиль добавлен после повторной попытки");
                        check(cars.get(0).getPower() == 120, "Мощность корректна после повтора");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
    }

    private static void testRetryAfterInvalidYear() {
        runTest("Повтор после ошибки ввода года",
                "100\nFord Focus\nxyz\n2015\nвыход\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("100\nFord Focus\nxyz\n2015\nвыход\n".getBytes())));
                    MyArrayList<Car> cars;
                    try {
                        cars = source.load(1);
                        check(cars.size() == 1, "Автомобиль добавлен после повторной попытки");
                        check(cars.get(0).getProductionYear() == 2015, "Год корректен после повтора");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
    }

    private static void testEmptyModel() {
        runTest("Пустое поле модели",
                "\n\nMazda 3\n140\n2017\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("140\n\n\nMazda 3\n2017\n".getBytes())));
                    MyArrayList<Car> cars;
                    try {
                        cars = source.load(1);
                        check(cars.size() == 1, "Автомобиль добавлен после пустых моделей");
                        check("Mazda 3".equals(cars.get(0).getModel()), "Модель корректна");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
    }

    private static void testInvalidNumbers() {
        runTest("Неверные числа и пробелы для мощности и года",
                "-50\n   \n150\nNissan Altima\nabc\n  \n2020\n",
                () -> {
                    ManualCarSource source = new ManualCarSource(
                            new Scanner(new ByteArrayInputStream("-50\n   \n150\nNissan Altima\nabc\n  \n2020\n".getBytes())));
                    MyArrayList<Car> cars;
                    try {
                        cars = source.load(1);
                        check(cars.size() == 1, "Автомобиль добавлен после неверных чисел");
                        check(cars.get(0).getPower() == 150, "Мощность корректна");
                        check(cars.get(0).getProductionYear() == 2020, "Год корректен");
                    } catch (IOException e) {
                        System.out.println("its over");
                    }
                });
    }

}