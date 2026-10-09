package sorting.app;

import java.nio.file.Path;
import java.util.Comparator;
import sorting.algorithm.InsertionSort;
import sorting.compare.CarAllFieldsComparator;
import sorting.contract.DataSource;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;
import sorting.source.FileCarSource;

/**
 * Создаёт модули приложения. null в необязательной фабрике означает,
 * что реализация ещё не подключена. Фабрики не читают данные и не выводят сообщения.
 */
public class Modules {
    public DataSource<Car> fileSource(Path path) {
        return new FileCarSource(path);
    }

    public DataSource<Car> manualSource(ConsoleInput input) {
        return null;
    }

    public DataSource<Car> randomSource() {
        return null;
    }

    public SortStrategy<Car> sortStrategy() {
        return new InsertionSort<>();
    }

    public SortStrategy<Car> selectionSort() {
        return null;
    }

    public SortStrategy<Car> bubbleSort() {
        return null;
    }

    public Comparator<Car> carComparator() {
        return new CarAllFieldsComparator();
    }

    public Comparator<Car> powerComparator() {
        return null;
    }

    public Comparator<Car> modelComparator() {
        return null;
    }

    public Comparator<Car> productionYearComparator() {
        return null;
    }
}
