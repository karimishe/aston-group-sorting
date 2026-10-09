package sorting.app;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;

public final class ConsoleApplication {
    private final ConsoleInput input;
    private final PrintStream output;
    private final Modules modules;
    private MyArrayList<Car> cars;
    private MyArrayList<Car> sortedCars;

    public ConsoleApplication(ConsoleInput input, PrintStream output, Modules modules) {
        this.input = Objects.requireNonNull(input, "Читатель консоли не должен быть null.");
        this.output = Objects.requireNonNull(output, "Поток вывода не должен быть null.");
        this.modules = Objects.requireNonNull(modules, "Модули не должны быть null.");
    }

    public void run() throws IOException {
        output.println("Приложение сортировки автомобилей");

        while (true) {
            printMenu();
            int command = input.readInt("Ваш выбор: ", 0, 4);
            switch (command) {
                case 1:
                    loadFromFile();
                    break;
                case 2:
                    sortCars();
                    break;
                case 3:
                    showCars();
                    break;
                case 4:
                    showSortedCars();
                    break;
                case 0:
                    output.println("Работа программы завершена.");
                    return;
            }
        }
    }

    private void loadFromFile() throws IOException {
        String pathText = input.readLine("Путь к файлу: ").trim();
        if (pathText.isEmpty()) {
            output.println("Путь к файлу не должен быть пустым.");
            return;
        }

        Path path;
        try {
            path = Paths.get(pathText);
        } catch (InvalidPathException exception) {
            output.println("Некорректный путь к файлу.");
            return;
        }
        int length = input.readInt("Количество автомобилей: ", 1, Integer.MAX_VALUE);

        try {
            DataSource<Car> source = modules.fileSource(path);
            MyArrayList<Car> loadedCars = source.load(length);
            cars = loadedCars;
            sortedCars = null;
            output.println("Загружено автомобилей: " + cars.size());
        } catch (IOException | IllegalArgumentException exception) {
            output.println("Не удалось загрузить файл: " + exception.getMessage());
        }
    }

    private void sortCars() {
        if (cars == null) {
            output.println("Сначала загрузите автомобили для сортировки.");
            return;
        }

        try {
            MyArrayList<Car> result = cars.copy();
            SortStrategy<Car> strategy = modules.sortStrategy();
            Comparator<Car> comparator = modules.carComparator();
            strategy.sort(result, comparator);
            sortedCars = result;
            output.println("Отсортировано автомобилей: " + sortedCars.size());
        } catch (RuntimeException exception) {
            output.println("Не удалось отсортировать автомобили: " + exception.getMessage());
        }
    }

    private void showCars() {
        if (cars == null) {
            output.println("Исходная коллекция пока не загружена.");
            return;
        }
        printCars("Исходная коллекция", cars);
    }

    private void showSortedCars() {
        if (sortedCars == null) {
            output.println("Результата сортировки пока нет.");
            return;
        }
        printCars("Результат сортировки", sortedCars);
    }

    private void printCars(String title, MyArrayList<Car> items) {
        output.println(title + " (" + items.size() + "):");
        for (int i = 0; i < items.size(); i++) {
            Car car = items.get(i);
            output.println((i + 1) + ". " + car.getPower() + ";"
                    + car.getModel() + ";" + car.getProductionYear());
        }
    }

    private void printMenu() {
        output.println();
        output.println("Меню:");
        output.println("1. Загрузить автомобили из файла");
        output.println("2. Отсортировать автомобили");
        output.println("3. Показать исходную коллекцию");
        output.println("4. Показать результат сортировки");
        output.println("0. Выход");
    }
}
