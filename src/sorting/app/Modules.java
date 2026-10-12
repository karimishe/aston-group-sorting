package sorting.app;

import java.nio.file.Path;
import java.util.Comparator;

import sorting.algorithm.BubbleSort;
import sorting.algorithm.InsertionSort;
import sorting.algorithm.SelectionSort;
import sorting.compare.CarAllFieldsComparator;
import sorting.compare.CarModelComparator;
import sorting.compare.CarPowerComparator;
import sorting.compare.CarProductionYearComparator;
import sorting.contract.DataSource;
import sorting.contract.OccurrenceCounter;
import sorting.contract.ResultWriter;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;
import sorting.output.CarFileWriter;
import sorting.source.CarCounter;
import sorting.source.StreamFileCarSource;
import sorting.source.StreamManualCarSource;
import sorting.source.StreamRandomCarSource;

/**
 * Создаёт модули приложения. null в необязательной фабрике означает,
 * что реализация ещё не подключена. Фабрики не читают данные и не выводят сообщения.
 */
public class Modules {
    public DataSource<Car> fileSource(Path path) {
        return new StreamFileCarSource(path);
    }

    public DataSource<Car> manualSource(ConsoleInput input) {
        return new StreamManualCarSource(input);
    }

    public DataSource<Car> randomSource() {
        return new StreamRandomCarSource();
    }

    public SortStrategy<Car> sortStrategy() {
        return new InsertionSort<>();
    }

    public SortStrategy<Car> selectionSort() {
        return new SelectionSort<>();
    }

    public SortStrategy<Car> bubbleSort() {
        return new BubbleSort<>();
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

    public ResultWriter<Car> carResultWriter() {
        return new CarFileWriter();
    }
}
