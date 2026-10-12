package sorting.source;

import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.model.Car;

import java.util.stream.Stream;

public class RandomCarSource implements DataSource<Car> {
    @Override
    public MyArrayList<Car> load(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        return Stream.generate(new CarGenerator())
                .limit(length)
                .collect(MyArrayList::new, MyArrayList::add, MyArrayList::addAll);
    }
}
