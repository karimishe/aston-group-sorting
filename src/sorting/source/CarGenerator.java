package sorting.source;

import sorting.model.Car;

import java.time.LocalDate;
import java.util.Random;
import java.util.function.Supplier;

public class CarGenerator implements Supplier<Car> {
    private final Random random = new Random();
    public static final String[] MODELS = {"Audi", "Лада", "Honda", "Hyundai", "Kia", "Toyota", "Ford", "Москвич"};
    public static final int POWER_MIN = 50;
    public static final int POWER_MAX = 500;
    public static final int YEAR_MIN = 1900;
    public static final int YEAR_MAX = LocalDate.now().getYear();

    @Override
    public Car get() {
        return new Car.Builder()
            .power(randomPower())
            .model(randomModel())
            .productionYear(randomYear())
            .build();
    }

    private int randomPower() {
        return POWER_MIN + random.nextInt(POWER_MAX - POWER_MIN + 1);
    }

    private String randomModel() {
        return MODELS[random.nextInt(MODELS.length)];
    }

    private int randomYear() {
        return YEAR_MIN + random.nextInt(YEAR_MAX - YEAR_MIN + 1);
    }
}
