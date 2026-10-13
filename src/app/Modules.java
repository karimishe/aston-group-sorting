package app;

import java.nio.file.Path;
import java.util.Comparator;

import algorithm.BubbleSort;
import algorithm.EvenValueSorter;
import algorithm.InsertionSort;
import algorithm.SelectionSort;
import compare.CarAllFieldsComparator;
import compare.CarModelComparator;
import compare.CarPowerComparator;
import compare.CarProductionYearComparator;
import contract.DataSource;
import contract.EvenSorter;
import contract.OccurrenceCounter;
import contract.ResultWriter;
import contract.SortStrategy;
import input.ConsoleInput;
import model.Car;
import output.CarFileWriter;
import source.CarCounter;
import source.StreamFileCarSource;
import source.StreamManualCarSource;
import source.StreamRandomCarSource;

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

    public EvenSorter<Car> evenSorter() {
        return new EvenValueSorter<>();
    }
}
