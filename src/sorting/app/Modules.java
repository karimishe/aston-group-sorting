package sorting.app;

import java.nio.file.Path;
import java.util.Comparator;
import sorting.algorithm.InsertionSort;
import sorting.compare.CarAllFieldsComparator;
import sorting.contract.DataSource;
import sorting.contract.SortStrategy;
import sorting.model.Car;
import sorting.source.FileCarSource;

public final class Modules {
    public DataSource<Car> fileSource(Path path) {
        return new FileCarSource(path);
    }

    public SortStrategy<Car> sortStrategy() {
        return new InsertionSort<>();
    }

    public Comparator<Car> carComparator() {
        return new CarAllFieldsComparator();
    }
}
