package sorting.source;

import sorting.model.Car;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDate;

public class CarConsoleReader {
    private Scanner scanner;
    public CarConsoleReader(){
    this.scanner = new Scanner(System.in);
}
    public CarConsoleReader(Scanner scanner){
    this.scanner = scanner;
    }
    public Car readCar() throws IOException {
        int parsedPower = 0;
        while (parsedPower <= 0) {
            System.out.println("Введите мощность: ");
            String power = scanner.nextLine().trim();
            try {
                parsedPower = Integer.parseInt(power);
                if (parsedPower <= 0) {
                    System.out.println("Мощность должна быть положительной");
                }
            } catch (NumberFormatException e) {
                System.out.println("Мощность должна быть числом");
            }
        }
        String model = "";
        while (model == "") {
            System.out.println("Введите модель: ");
                model = scanner.nextLine().trim();
                if (model == "") {
                    System.out.println("Строка пустая или содержит только пробелы");
                }
        }
        int parsedYear = 0;
        int currentYear = LocalDate.now().getYear();
        while (1900 >= parsedYear || parsedYear >= currentYear) {
            System.out.println("Введите год выпуска: ");
            String year = scanner.nextLine().trim();
            try {
            parsedYear = Integer.parseInt(year);
            if (1900 >= parsedYear || parsedYear >= currentYear){
                System.out.println("Год выпуска должен быть от 1900 до " + currentYear);
            }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите целое число");
            }
        }
        Car car = new Car.Builder()
                .power(parsedPower)
                .model(model)
                .productionYear(parsedYear)
                .build();
        System.out.println("Автомобиль добавлен");
    return car;
}
}