package contract;

import java.util.function.ToIntFunction;
import collection.MyArrayList;

public interface EvenSorter<T> {
    /**
     * Сортирует только элементы с чётным значением field по возрастанию этого значения.
     * Использует переданную стратегию. Нечётные элементы сохраняют свои индексы.
     */
    void sort(MyArrayList<T> items, ToIntFunction<T> field, SortStrategy<T> strategy);
}
