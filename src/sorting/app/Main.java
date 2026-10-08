package sorting.app;

import sorting.model.Car;

public class Main {
    public static void main(String[] args) {
        System.out.println("Приложение сортировки автомобилей");

        Car car = new Car.Builder()
                .power(150)
                .model("Kia")
                .productionYear(2020)
                .build();

        System.out.println("Модель: " + car.getModel());
        System.out.println("Мощность: " + car.getPower());
        System.out.println("Год выпуска: " + car.getProductionYear());
    }
}
