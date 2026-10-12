package sorting.app;

import java.nio.file.Path;
import java.util.Comparator;
import sorting.algorithm.InsertionSort;
import sorting.algorithm.SelectionSort;
import sorting.compare.CarAllFieldsComparator;
import sorting.compare.CarModelComparator;
import sorting.compare.CarPowerComparator;
import sorting.compare.CarProductionYearComparator;
import sorting.contract.DataSource;
import sorting.contract.OccurrenceCounter;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;
import sorting.source.CarCounter;
import sorting.source.FileCarSource;
import sorting.source.ManualCarSource;
import sorting.source.RandomCarSource;

/**
 * Создаёт модули приложения. null в необязательной фабрике означает,
 * что реализация ещё не подключена. Фабрики не читают данные и не выводят сообщения.
 */
public class Modules {
    public DataSource<Car> fileSource(Path path) {
        return new FileCarSource(path);
    }

    public DataSource<Car> manualSource(ConsoleInput input) {
        return new ManualCarSource(input);
    }

    public DataSource<Car> randomSource() {
        return new RandomCarSource();
    }

    public SortStrategy<Car> sortStrategy() {
        return new InsertionSort<>();
    }

    public SortStrategy<Car> selectionSort() {
        return new SelectionSort<>();
    }

    public SortStrategy<Car> bubbleSort() {
        return null;
    }

    public Comparator<Car> carComparator() {
        return new CarAllFieldsComparator();
    }

    public Comparator<Car> powerComparator() {
        return new CarPowerComparator();
    }

    public Comparator<Car> modelComparator() {
        return new CarModelComparator();
    }

    public Comparator<Car> productionYearComparator() {
        return new CarProductionYearComparator();
    }

    public OccurrenceCounter<Car> carCounter() {
        return new CarCounter();
    }
}
