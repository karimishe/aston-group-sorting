package sorting.contract;

import java.util.Comparator;
import sorting.collection.MyArrayList;

public interface SortStrategy<T> {
    /**
     * Переставляет элементы по переданному правилу сравнения.
     * Размер коллекции и сами элементы сохраняются. Пустая коллекция допустима.
     */
    void sort(MyArrayList<T> items, Comparator<T> comparator);
}
