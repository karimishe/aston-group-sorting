package sorting.model;

import java.time.LocalDate;

public class CarTest {
    private static int checks;

    public static void main(String[] args) {
        int currentYear = LocalDate.now().getYear();

        Car car = new Car.Builder()
                .power(150)
                .model("Toyota Corolla")
                .productionYear(2020)
                .build();

        check(car.getPower() == 150, "Сохраняется мощность");
        check("Toyota Corolla".equals(car.getModel()), "Сохраняется модель");
        check(car.getProductionYear() == 2020, "Сохраняется год выпуска");

        Car minimum = new Car.Builder()
                .power(1)
                .model("  Ford Model T  ")
                .productionYear(1900)
                .build();
        check(minimum.getPower() == 1, "Допускается мощность 1");
        check("Ford Model T".equals(minimum.getModel()), "Удаляются пробелы по краям модели");
        check(minimum.getProductionYear() == 1900, "Допускается год 1900");

        Car maximum = new Car.Builder()
                .power(Integer.MAX_VALUE)
                .model("Test")
                .productionYear(currentYear)
                .build();
        check(maximum.getPower() == Integer.MAX_VALUE, "Допускается максимальная мощность int");
        check(maximum.getProductionYear() == currentYear, "Допускается текущий год");

        expectInvalid(new Car.Builder(), "Все поля отсутствуют");
        expectInvalid(new Car.Builder().model("Ford").productionYear(2020),
                "Отсутствует мощность");
        expectInvalid(new Car.Builder().power(100).productionYear(2020),
                "Отсутствует модель");
        expectInvalid(new Car.Builder().power(100).model("Ford"),
                "Отсутствует год");
        expectInvalid(new Car.Builder().power(0).model("Ford").productionYear(2020),
                "Нулевая мощность");
        expectInvalid(new Car.Builder().power(-1).model("Ford").productionYear(2020),
                "Отрицательная мощность");
        expectInvalid(new Car.Builder().power(100).model(null).productionYear(2020),
                "Модель null");
        expectInvalid(new Car.Builder().power(100).model("").productionYear(2020),
                "Пустая модель");
        expectInvalid(new Car.Builder().power(100).model(" \t ").productionYear(2020),
                "Модель из пробельных символов");
        expectInvalid(new Car.Builder().power(100).model("Ford;Focus").productionYear(2020),
                "Точка с запятой в модели");
        expectInvalid(new Car.Builder().power(100).model("Ford\rFocus").productionYear(2020),
                "Возврат каретки внутри модели");
        expectInvalid(new Car.Builder().power(100).model("Ford\nFocus").productionYear(2020),
                "Перевод строки внутри модели");
        expectInvalid(new Car.Builder().power(100).model("\rFord").productionYear(2020),
                "Возврат каретки в начале модели");
        expectInvalid(new Car.Builder().power(100).model("Ford\n").productionYear(2020),
                "Перевод строки в конце модели");
        expectInvalid(new Car.Builder().power(100).model("Ford").productionYear(1899),
                "Год меньше 1900");
        expectInvalid(new Car.Builder().power(100).model("Ford").productionYear(currentYear + 1),
                "Год больше текущего");

        Car.Builder reusableBuilder = new Car.Builder()
                .power(100)
                .model("Ford")
                .productionYear(2000);
        Car first = reusableBuilder.build();
        Car second = reusableBuilder.power(200).model("Volvo").productionYear(2020).build();

        check(first.getPower() == 100, "Изменение Builder не меняет мощность первого автомобиля");
        check("Ford".equals(first.getModel()), "Изменение Builder не меняет модель первого автомобиля");
        check(first.getProductionYear() == 2000, "Изменение Builder не меняет год первого автомобиля");
        check(second.getPower() == 200, "Второй автомобиль получает новую мощность");
        check("Volvo".equals(second.getModel()), "Второй автомобиль получает новую модель");
        check(second.getProductionYear() == 2020, "Второй автомобиль получает новый год");
        check(first != second, "Каждый build создаёт отдельный объект");

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }

    private static void expectInvalid(Car.Builder builder, String description) {
        try {
            builder.build();
        } catch (IllegalArgumentException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидалась ошибка валидации: " + description);
    }
}
