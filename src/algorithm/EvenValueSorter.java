package algorithm;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;
import collection.MyArrayList;
import contract.EvenSorter;
import contract.SortStrategy;

public final class EvenValueSorter<T> implements EvenSorter<T> {
    @Override
    public void sort(MyArrayList<T> items, ToIntFunction<T> field, SortStrategy<T> strategy) {
        Objects.requireNonNull(items, "Коллекция не должна быть null.");
        Objects.requireNonNull(field, "Функция получения числового поля не должна быть null.");
        Objects.requireNonNull(strategy, "Стратегия сортировки не должна быть null.");

        int size = items.size();
        boolean[] evenPositions = new boolean[size];
        MyArrayList<T> evenItems = new MyArrayList<>();
        for (int i = 0; i < size; i++) {
            T item = items.get(i);
            if (field.applyAsInt(item) % 2 == 0) {
                evenPositions[i] = true;
                evenItems.add(item);
            }
        }

        strategy.sort(evenItems, Comparator.comparingInt(field));

        int nextEven = 0;
        for (int i = 0; i < size; i++) {
            if (evenPositions[i]) {
                items.set(i, evenItems.get(nextEven));
                nextEven++;
            }
        }
    }
}
