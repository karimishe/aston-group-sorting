package source;

import java.util.Objects;
import java.util.stream.Stream;
import collection.MyArrayList;
import contract.DataSource;
import model.Car;

/** Создаёт ровно length автомобилей с помощью общего CarGenerator внутри Stream. */
public final class StreamRandomCarSource implements DataSource<Car> {
    private final CarGenerator generator;

    public StreamRandomCarSource() {
        this(new CarGenerator());
    }

    /** Конструктор для внедрения общего генератора, в том числе в тестах. */
    public StreamRandomCarSource(CarGenerator generator) {
        this.generator = Objects.requireNonNull(generator, "Генератор автомобилей не должен быть null.");
    }

    @Override
    public MyArrayList<Car> load(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        return Stream.generate(generator)
                .sequential()
                .limit(length)
                .collect(MyArrayList::new, MyArrayList::add, MyArrayList::addAll);
    }
}
