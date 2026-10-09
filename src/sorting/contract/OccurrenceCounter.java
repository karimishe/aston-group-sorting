package sorting.contract;

import sorting.collection.MyArrayList;

public interface OccurrenceCounter<T> {
    /**
     * Считает совпадения с target через equals, используя не более threadCount потоков.
     * Коллекцию не изменяет. При прерывании не возвращает частичный результат.
     *
     * @throws IllegalArgumentException если threadCount не положителен
     * @throws InterruptedException если выполнение было прервано
     */
    int count(MyArrayList<T> items, T target, int threadCount) throws InterruptedException;
}
