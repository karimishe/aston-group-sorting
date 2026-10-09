package sorting.contract;

import sorting.collection.MyArrayList;

public interface OccurrenceCounter<T> {
    /**
     * Считает совпадения с target, используя не более threadCount потоков.
     * Правило совпадения задаётся реализацией. Для Car совпадают все три поля.
     * Коллекцию не изменяет. При прерывании не возвращает частичный результат.
     *
     * @throws IllegalArgumentException если threadCount не положителен
     * @throws InterruptedException если выполнение было прервано
     */
    int count(MyArrayList<T> items, T target, int threadCount) throws InterruptedException;
}
