package sorting.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.model.Car;

public final class FileCarSource implements DataSource<Car> {
    private final Path path;
    private final CarLineParser parser;

    public FileCarSource(Path path) {
        this.path = Objects.requireNonNull(path, "Путь к файлу не должен быть null.");
        this.parser = new CarLineParser();
    }

    @Override
    public MyArrayList<Car> load(int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        MyArrayList<Car> cars = new MyArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Car car;
                try {
                    car = parser.parse(line);
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("Строка " + lineNumber + ": "
                            + exception.getMessage(), exception);
                }
                if (cars.size() < length) {
                    cars.add(car);
                }
            }
        } catch (NoSuchFileException exception) {
            throw new IOException("Файл не найден: " + path, exception);
        } catch (MalformedInputException exception) {
            throw new IOException("Файл должен быть в кодировке UTF-8.", exception);
        }

        if (cars.size() < length) {
            throw new IllegalArgumentException("В файле недостаточно автомобилей: требуется "
                    + length + ", найдено " + cars.size() + ".");
        }
        return cars;
    }
}
