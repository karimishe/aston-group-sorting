package sorting.app;

import java.nio.file.Path;
import sorting.contract.DataSource;
import sorting.model.Car;
import sorting.source.FileCarSource;

public final class Modules {
    public DataSource<Car> fileSource(Path path) {
        return new FileCarSource(path);
    }
}
