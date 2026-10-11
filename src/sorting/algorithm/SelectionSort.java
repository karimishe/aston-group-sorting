package sorting.algorithm;

import java.util.Comparator;
import java.util.Objects;
import sorting.collection.MyArrayList;
import sorting.contract.SortStrategy;

public final class SelectionSort<T> implements SortStrategy<T> {
    @Override
    public void sort(MyArrayList<T> items, Comparator<T> comparator) {
        Objects.requireNonNull(items, "Коллекция не должна быть null.");
        Objects.requireNonNull(comparator, "Компаратор не должен быть null.");

        for (int i = 0; i < items.size() - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < items.size(); j++) {
                if (comparator.compare(items.get(j), items.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                T temp = items.get(i);
                items.set(i, items.get(minIndex));
                items.set(minIndex, temp);
            }
        }
    }
}