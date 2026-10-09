package sorting.app;

import java.io.EOFException;
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
                    chooseSource();
                    break;
                case 2:
                    chooseAlgorithm();
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

    private void chooseSource() throws IOException {
        DataSource<Car> manualSource = modules.manualSource(input);
        DataSource<Car> randomSource = modules.randomSource();
        output.println();
        output.println("Источник данных:");
        output.println("1. Из файла");
        printOption(2, "Вручную", manualSource != null);
        printOption(3, "Случайные данные", randomSource != null);
        output.println("0. Назад в главное меню");

        int choice = input.readInt("Выберите источник: ", 0, 3);
        switch (choice) {
            case 1:
                loadFromFile();
                return;
            case 2:
                if (manualSource == null) {
                    output.println("Ручной ввод пока не реализован.");
                    return;
                }
                loadCars(manualSource, "Не удалось загрузить автомобили: ");
                return;
            case 3:
                if (randomSource == null) {
                    output.println("Случайное заполнение пока не реализовано.");
                    return;
                }
                loadCars(randomSource, "Не удалось загрузить автомобили: ");
                return;
            case 0:
                return;
        }
    }

    private void chooseAlgorithm() throws IOException {
        SortStrategy<Car> insertionSort = modules.sortStrategy();
        SortStrategy<Car> selectionSort = modules.selectionSort();
        SortStrategy<Car> bubbleSort = modules.bubbleSort();
        output.println();
        output.println("Алгоритм сортировки:");
        printOption(1, "Вставками", insertionSort != null);
        printOption(2, "Выбором", selectionSort != null);
        printOption(3, "Пузырьком", bubbleSort != null);
        output.println("0. Назад в главное меню");

        int choice = input.readInt("Выберите алгоритм: ", 0, 3);
        switch (choice) {
            case 1:
                prepareSorting(insertionSort, "Сортировка вставками пока не реализована.");
                return;
            case 2:
                prepareSorting(selectionSort, "Сортировка выбором пока не реализована.");
                return;
            case 3:
                prepareSorting(bubbleSort, "Пузырьковая сортировка пока не реализована.");
                return;
            case 0:
                return;
        }
    }

    private void prepareSorting(SortStrategy<Car> strategy, String unavailableMessage)
            throws IOException {
        if (strategy == null) {
            output.println(unavailableMessage);
            return;
        }
        if (cars == null) {
            output.println("Сначала загрузите автомобили для сортировки.");
            return;
        }

        Comparator<Car> comparator = chooseComparator();
        if (comparator != null) {
            sortCars(strategy, comparator);
        }
    }

    private Comparator<Car> chooseComparator() throws IOException {
        Comparator<Car> allFields = modules.carComparator();
        Comparator<Car> power = modules.powerComparator();
        Comparator<Car> model = modules.modelComparator();
        Comparator<Car> productionYear = modules.productionYearComparator();
        output.println();
        output.println("Способ сравнения:");
        printOption(1, "По всем трём полям", allFields != null);
        printOption(2, "По мощности", power != null);
        printOption(3, "По модели", model != null);
        printOption(4, "По году выпуска", productionYear != null);
        output.println("0. Назад в главное меню");

        int choice = input.readInt("Выберите способ сравнения: ", 0, 4);
        Comparator<Car> selected;
        switch (choice) {
            case 1:
                selected = allFields;
                break;
            case 2:
                selected = power;
                break;
            case 3:
                selected = model;
                break;
            case 4:
                selected = productionYear;
                break;
            default:
                return null;
        }
        if (selected == null) {
            output.println("Этот способ сравнения пока не реализован.");
        }
        return selected;
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
        loadCars(modules.fileSource(path), "Не удалось загрузить файл: ");
    }

    private void loadCars(DataSource<Car> source, String errorMessage) throws IOException {
        int length = input.readInt("Количество автомобилей: ", 1, Integer.MAX_VALUE);
        try {
            MyArrayList<Car> loadedCars = source.load(length);
            cars = loadedCars;
            sortedCars = null;
            output.println("Загружено автомобилей: " + cars.size());
        } catch (EOFException exception) {
            throw exception;
        } catch (IOException | IllegalArgumentException exception) {
            output.println(errorMessage + exception.getMessage());
        }
    }

    private void sortCars(SortStrategy<Car> strategy, Comparator<Car> comparator) {
        try {
            MyArrayList<Car> result = cars.copy();
            strategy.sort(result, comparator);
            sortedCars = result;
            output.println("Отсортировано автомобилей: " + sortedCars.size());
        } catch (RuntimeException exception) {
            output.println("Не удалось отсортировать автомобили: " + exception.getMessage());
        }
    }

    private void printOption(int number, String title, boolean available) {
        output.println(number + ". " + title + (available ? "" : " (не реализовано)"));
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
        output.println("1. Загрузить автомобили");
        output.println("2. Отсортировать автомобили");
        output.println("3. Показать исходную коллекцию");
        output.println("4. Показать результат сортировки");
        output.println("0. Выход");
    }
}
