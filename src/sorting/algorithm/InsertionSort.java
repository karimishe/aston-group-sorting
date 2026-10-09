package sorting.algorithm;

import java.util.Comparator;
import java.util.Objects;
import sorting.collection.MyArrayList;
import sorting.contract.SortStrategy;

public final class InsertionSort<T> implements SortStrategy<T> {
    @Override
    public void sort(MyArrayList<T> items, Comparator<T> comparator) {
        Objects.requireNonNull(items, "Коллекция не должна быть null.");
        Objects.requireNonNull(comparator, "Компаратор не должен быть null.");

        for (int i = 1; i < items.size(); i++) {
            T current = items.get(i);
            int previousIndex = i - 1;

            while (previousIndex >= 0
                    && comparator.compare(items.get(previousIndex), current) > 0) {
                items.set(previousIndex + 1, items.get(previousIndex));
                previousIndex--;
            }
            items.set(previousIndex + 1, current);
        }
    }
}
