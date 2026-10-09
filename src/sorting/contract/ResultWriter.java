package sorting.contract;

import java.io.IOException;
import java.nio.file.Path;
import sorting.collection.MyArrayList;

public interface ResultWriter<T> {
    /**
     * Добавляет элементы в конец файла, сохраняя прежние записи.
     * При отсутствии файла создаёт его. Коллекцию не изменяет.
     *
     * @throws IOException если произошла ошибка записи
     */
    void append(MyArrayList<T> items, Path path) throws IOException;
}
