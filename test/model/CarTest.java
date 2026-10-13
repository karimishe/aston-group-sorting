package model;

import java.time.LocalDate;

import model.Car;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {
    @Test
    void builderStoresAllFields() {
        Car car = new Car.Builder()
                .power(150).model("Toyota Corolla").productionYear(2020).build();

        assertAll(
                () -> assertEquals(150, car.getPower()),
                () -> assertEquals("Toyota Corolla", car.getModel()),
                () -> assertEquals(2020, car.getProductionYear()));
    }

    @Test
    void acceptsMinimumValuesAndTrimsModel() {
        Car car = new Car.Builder()
                .power(1).model("  Ford Model T  ").productionYear(1900).build();

        assertAll(
                () -> assertEquals(1, car.getPower()),
                () -> assertEquals("Ford Model T", car.getModel()),
                () -> assertEquals(1900, car.getProductionYear()));
    }

    @Test
    void acceptsMaximumPowerAndCurrentYear() {
        int currentYear = LocalDate.now().getYear();
        Car car = new Car.Builder()
                .power(Integer.MAX_VALUE).model("Test").productionYear(currentYear).build();

        assertEquals(Integer.MAX_VALUE, car.getPower());
        assertEquals(currentYear, car.getProductionYear());
    }

    @Test
    void rejectsMissingFields() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Car.Builder().build()),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Car.Builder().model("Ford").productionYear(2020).build()),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Car.Builder().power(100).productionYear(2020).build()),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Car.Builder().power(100).model("Ford").build()));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void rejectsNonPositivePower(int power) {
        assertThrows(IllegalArgumentException.class,
                () -> new Car.Builder().power(power).model("Ford").productionYear(2020).build());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t ", "Ford;Focus", "Ford\rFocus", "Ford\nFocus", "\rFord", "Ford\n"})
    void rejectsInvalidModel(String model) {
        assertThrows(IllegalArgumentException.class,
                () -> new Car.Builder().power(100).model(model).productionYear(2020).build());
    }

    @Test
    void rejectsYearOutsideAllowedRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new Car.Builder().power(100).model("Ford").productionYear(1899).build());
        assertThrows(IllegalArgumentException.class,
                () -> new Car.Builder().power(100).model("Ford")
                        .productionYear(LocalDate.now().getYear() + 1).build());
    }

    @Test
    void reusingBuilderDoesNotChangePreviousCar() {
        Car.Builder builder = new Car.Builder().power(100).model("Ford").productionYear(2000);
        Car first = builder.build();
        Car second = builder.power(200).model("Volvo").productionYear(2020).build();

        assertAll(
                () -> assertEquals(100, first.getPower()),
                () -> assertEquals("Ford", first.getModel()),
                () -> assertEquals(2000, first.getProductionYear()),
                () -> assertEquals(200, second.getPower()),
                () -> assertEquals("Volvo", second.getModel()),
                () -> assertEquals(2020, second.getProductionYear()),
                () -> assertNotSame(first, second));
    }
}
