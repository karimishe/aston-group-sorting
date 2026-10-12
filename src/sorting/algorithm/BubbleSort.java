package sorting.algorithm;

import java.util.Comparator;
import java.util.Objects;
import sorting.collection.MyArrayList;
import sorting.contract.SortStrategy;

public final class BubbleSort<T> implements SortStrategy<T> {
    @Override
    public void sort(MyArrayList<T> items, Comparator<T> comparator) {
        Objects.requireNonNull(items, "Коллекция не должна быть null.");
        Objects.requireNonNull(comparator, "Компаратор не должен быть null.");

        int unsortedEnd = items.size() - 1;
        while (unsortedEnd > 0) {
            int lastSwapIndex = 0;
            for (int i = 0; i < unsortedEnd; i++) {
                T left = items.get(i);
                T right = items.get(i + 1);
                if (comparator.compare(left, right) > 0) {
                    items.set(i, right);
                    items.set(i + 1, left);
                    lastSwapIndex = i;
                }
            }
            unsortedEnd = lastSwapIndex;
        }
    }
}
