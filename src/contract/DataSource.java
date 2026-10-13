package contract;

import java.io.IOException;
import collection.MyArrayList;

public interface DataSource<T> {
    /**
     * Возвращает новую коллекцию ровно из length допустимых элементов.
     *
     * @throws IllegalArgumentException если length не положителен
     * @throws IOException если произошла ошибка чтения
     */
    MyArrayList<T> load(int length) throws IOException;
}
