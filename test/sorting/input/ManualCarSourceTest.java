package sorting.input;
import sorting.collection.MyArrayList;
import sorting.model.Car;
import sorting.source.ManualCarSource;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.io.ByteArrayInputStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class ManualCarSourceTest {

    @Test
    @DisplayName("Корректный ввод одного автомобиля")
    void testCorrectInput() {
        String input = "150\nToyota Camry\n2020\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("150\nToyota Camry\n2020\n"), System.out));

        MyArrayList<Car> cars;
        try {
            cars = source.load(1);
            assertEquals(1, cars.size());
            Car car = cars.get(0);
            assertEquals(150, car.getPower());
            assertEquals("Toyota Camry", car.getModel());
            assertEquals(2020, car.getProductionYear());
        } catch (IOException e) {
            System.out.println("its over");
        }
    }

    @Test
    @DisplayName("Корректный ввод нескольких автомобилей")
    void testMultipleCarsInput() {
        String input = "BMW X5\n300\n2021\nAudi A4\n200\n2019\nвыход\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("300\nBMW X5\n2021\n200\nAudi A4\n2019\n"), System.out));

        MyArrayList<Car> cars;
        try {
            cars = source.load(2);
            assertEquals(2, cars.size());

            assertEquals(300, cars.get(0).getPower());
            assertEquals("BMW X5", cars.get(0).getModel());
            assertEquals(2021, cars.get(0).getProductionYear());

            assertEquals(200, cars.get(1).getPower());
            assertEquals("Audi A4", cars.get(1).getModel());
            assertEquals(2019, cars.get(1).getProductionYear());
        } catch (IOException e) {
            System.out.println("its over");
        }
    }
    @Test
    @DisplayName("Повтор после ошибки ввода мощности")
    void testRetryAfterInvalidPower() {
        String input = "abc\n120\nHonda Civic\n2018\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("abc\n120\nHonda Civic\n2018\n"), System.out));

        MyArrayList<Car> cars;
        try {
            cars = source.load(1);
            assertEquals(1, cars.size());
            Car car = cars.get(0);
            assertEquals(120, car.getPower());
            assertEquals("Honda Civic", car.getModel());
            assertEquals(2018, car.getProductionYear());
        } catch (IOException e) {
            System.out.println("its over");
        }
    }
    @Test
    @DisplayName("Повтор после ошибки ввода года")
    void testRetryAfterInvalidYear() {
        String input = "100\nFord Focus\nxyz\n2015\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("100\nFord Focus\nxyz\n2015\n"), System.out));

        MyArrayList<Car> cars;
        try {
        cars = source.load(1);
        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(100, car.getPower());
        assertEquals("Ford Focus", car.getModel());
        assertEquals(2015, car.getProductionYear());
    } catch (IOException e) {
        System.out.println("its over");
    }
}
    @Test
    @DisplayName("Пустое поле модели")
    void testEmptyModel() {
        String input = "140\n\nMazda 3\n2017\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("140\n\n\nMazda 3\n2017\n"), System.out));

        MyArrayList<Car> cars;
        try {
        cars = source.load(1);
        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(140, car.getPower());
        assertEquals("Mazda 3", car.getModel());
        assertEquals(2017, car.getProductionYear());
        } catch (IOException e) {
            System.out.println("its over");
        }
    }

    @Test
    @DisplayName("Неверные числа для мощности и года")
    void testInvalidNumbers() {
        String input = "-50\n150\nNissan Altima\nabc\n2020\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ManualCarSource source = new ManualCarSource(
                new ConsoleInput(new StringReader("-50\n150\nNissan Altima\nabc\n2020\n"), System.out));

        MyArrayList<Car> cars;
        try {
        cars = source.load(1);
        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(150, car.getPower());
        assertEquals("Nissan Altima", car.getModel());
        assertEquals(2020, car.getProductionYear());
        } catch (IOException e) {
            System.out.println("its over");
        }
    }
}
