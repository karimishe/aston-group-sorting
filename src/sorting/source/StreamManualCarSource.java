package sorting.source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.stream.IntStream;
import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.input.ConsoleInput;
import sorting.model.Car;

/** Заполняет MyArrayList автомобилями, считываемыми внутри последовательного Stream. */
public final class StreamManualCarSource implements DataSource<Car> {
    private final CarConsoleReader carReader;

    public StreamManualCarSource(ConsoleInput input) {
        this.carReader = new CarConsoleReader(Objects.requireNonNull(
                input, "Читатель консоли не должен быть null."));
    }

    @Override
    public MyArrayList<Car> load(int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        try {
            return IntStream.range(0, length)
                    .sequential()
                    .mapToObj(ignored -> readCarUnchecked())
                    .collect(MyArrayList::new, MyArrayList::add, MyArrayList::addAll);
        } catch (UncheckedIOException exception) {
            // В том числе возвращает вызывающему коду исходный EOFException.
            throw exception.getCause();
        }
    }

    private Car readCarUnchecked() {
        try {
            return carReader.readCar();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
