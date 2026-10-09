package sorting.source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import sorting.collection.MyArrayList;
import sorting.model.Car;

public class FileCarSourceTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        Path directory = Files.createTempDirectory("car-source-test-");
        Path file = directory.resolve("cars.txt");
        try {
            String content = "150;Лада Веста;2020\n100;Ford;2000\n200;Volvo;2021\n";
            write(file, content);
            FileCarSource source = new FileCarSource(file);
            MyArrayList<Car> cars = source.load(3);
            check(cars.size() == 3, "Загружается запрошенное количество автомобилей");
            check(cars.get(0).getPower() == 150, "Прочитана мощность первого автомобиля");
            check("Лада Веста".equals(cars.get(0).getModel()), "UTF-8 сохраняет кириллицу");
            check(cars.get(0).getProductionYear() == 2020, "Прочитан год первого автомобиля");
            check("Ford".equals(cars.get(1).getModel()), "Сохранён второй элемент файла");
            check("Volvo".equals(cars.get(2).getModel()), "Сохранён третий элемент файла");

            MyArrayList<Car> prefix = source.load(2);
            check(prefix.size() == 2, "Лишние корректные строки не попадают в результат");
            check("Лада Веста".equals(prefix.get(0).getModel()), "Выбирается начало файла");
            check("Ford".equals(prefix.get(1).getModel()), "В начале файла сохраняется порядок");

            MyArrayList<Car> repeated = source.load(3);
            check(cars != repeated, "Повторная загрузка создаёт отдельную коллекцию");
            for (int i = 0; i < cars.size(); i++) {
                check(cars.get(i) != repeated.get(i), "Повторная загрузка создаёт новый Car " + i);
            }
            cars.set(0, cars.get(1));
            check("Лада Веста".equals(repeated.get(0).getModel()),
                    "Изменение одной коллекции не меняет другую");
            check(content.equals(new String(Files.readAllBytes(file), StandardCharsets.UTF_8)),
                    "Загрузка не изменяет исходный файл");

            expectInvalid(source, 0, null, "Нулевая длина");
            expectInvalid(source, -1, null, "Отрицательная длина");
            expectInvalid(source, 4, null, "В файле меньше строк, чем запрошено");

            write(file, "150;Kia;2020\r\n100;Ford;2000");
            MyArrayList<Car> windowsLines = source.load(2);
            check(windowsLines.size() == 2, "Допускаются CRLF и отсутствие последнего перевода строки");
            check("Ford".equals(windowsLines.get(1).getModel()), "Последняя строка прочитана полностью");

            write(file, "150;Kia;2020\n100;Ford;2000\n-1;Volvo;2021\n");
            expectInvalid(source, 1, "Строка 3: ", "Проверяются строки после запрошенного количества");
            write(file, "150;Kia;2020\n\n100;Ford;2000\n");
            expectInvalid(source, 1, "Строка 2: ", "Пустая строка не пропускается");
            write(file, "150;Kia;2020\n   \n");
            expectInvalid(source, 1, "Строка 2: ", "Строка из пробелов не пропускается");
            write(file, "bad;Kia;2020\n");
            expectInvalid(source, 1, "Строка 1: ", "Ошибка первой строки содержит её номер");
            write(file, "");
            expectInvalid(source, 1, null, "Пустой файл не даёт нужного количества элементов");

            Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});
            expectIOException(file, "Некорректная кодировка UTF-8");
            expectIOException(directory.resolve("missing.txt"), "Несуществующий файл");
            expectIOException(directory, "Каталог вместо файла");
            try {
                new FileCarSource(null);
                throw new AssertionError("Ожидалась ошибка для пути null");
            } catch (NullPointerException exception) {
                checks++;
            }
            System.out.println("Проверок пройдено: " + checks);
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }

    private static void write(Path file, String content) throws IOException {
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void expectInvalid(FileCarSource source, int length, String prefix,
                                      String description) throws IOException {
        try {
            source.load(length);
        } catch (IllegalArgumentException exception) {
            check(prefix == null || exception.getMessage().startsWith(prefix),
                    "Неверный номер строки: " + description);
            return;
        }
        throw new AssertionError("Ожидалась ошибка данных: " + description);
    }

    private static void expectIOException(Path path, String description) {
        try {
            new FileCarSource(path).load(1);
        } catch (IOException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидалась ошибка чтения: " + description);
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
