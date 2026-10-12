package sorting.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sorting.collection.MyArrayList;
import sorting.model.Car;
import sorting.source.ManualCarSource;

class ManualCarSourceTest {
    @Test
    @DisplayName("Корректный ввод одного автомобиля")
    void testCorrectInput() throws IOException {
        ManualCarSource source = source("150\nToyota Camry\n2020\n");

        MyArrayList<Car> cars = source.load(1);

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(150, car.getPower());
        assertEquals("Toyota Camry", car.getModel());
        assertEquals(2020, car.getProductionYear());
    }

    @Test
    @DisplayName("Корректный ввод нескольких автомобилей")
    void testMultipleCarsInput() throws IOException {
        ManualCarSource source = source("300\nBMW X5\n2021\n200\nAudi A4\n2019\n");

        MyArrayList<Car> cars = source.load(2);

        assertEquals(2, cars.size());
        assertEquals(300, cars.get(0).getPower());
        assertEquals("BMW X5", cars.get(0).getModel());
        assertEquals(2021, cars.get(0).getProductionYear());
        assertEquals(200, cars.get(1).getPower());
        assertEquals("Audi A4", cars.get(1).getModel());
        assertEquals(2019, cars.get(1).getProductionYear());
    }

    @Test
    @DisplayName("Повтор после ошибки ввода мощности")
    void testRetryAfterInvalidPower() throws IOException {
        ManualCarSource source = source("abc\n120\nHonda Civic\n2018\n");

        MyArrayList<Car> cars = source.load(1);

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(120, car.getPower());
        assertEquals("Honda Civic", car.getModel());
        assertEquals(2018, car.getProductionYear());
    }

    @Test
    @DisplayName("Повтор после ошибки ввода года")
    void testRetryAfterInvalidYear() throws IOException {
        ManualCarSource source = source("100\nFord Focus\nxyz\n2015\n");

        MyArrayList<Car> cars = source.load(1);

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(100, car.getPower());
        assertEquals("Ford Focus", car.getModel());
        assertEquals(2015, car.getProductionYear());
    }

    @Test
    @DisplayName("Пустое поле модели")
    void testEmptyModel() throws IOException {
        ManualCarSource source = source("140\n\n\nMazda 3\n2017\n");

        MyArrayList<Car> cars = source.load(1);

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(140, car.getPower());
        assertEquals("Mazda 3", car.getModel());
        assertEquals(2017, car.getProductionYear());
    }

    @Test
    @DisplayName("Неверные числа для мощности и года")
    void testInvalidNumbers() throws IOException {
        ManualCarSource source = source("-50\n150\nNissan Altima\nabc\n2020\n");

        MyArrayList<Car> cars = source.load(1);

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals(150, car.getPower());
        assertEquals("Nissan Altima", car.getModel());
        assertEquals(2020, car.getProductionYear());
    }

    private ManualCarSource source(String input) {
        ConsoleInput consoleInput = new ConsoleInput(new StringReader(input),
                new PrintStream(new ByteArrayOutputStream()));
        return new ManualCarSource(consoleInput);
    }
}
