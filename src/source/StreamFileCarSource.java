package source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import collection.MyArrayList;
import contract.DataSource;
import model.Car;

/** Загружает автомобили через последовательный Stream, сохраняя только нужный префикс. */
public final class StreamFileCarSource implements DataSource<Car> {
    private final Path path;
    private final CarLineParser parser;

    public StreamFileCarSource(Path path) {
        this.path = Objects.requireNonNull(path, "Путь к файлу не должен быть null.");
        this.parser = new CarLineParser();
    }

    @Override
    public MyArrayList<Car> load(int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException("Количество автомобилей должно быть положительным.");
        }

        AtomicInteger lineNumber = new AtomicInteger();
        MyArrayList<Car> cars;
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8).sequential()) {
            cars = lines
                    .map(line -> parseLine(line, lineNumber.incrementAndGet()))
                    .collect(
                            MyArrayList::new,
                            (result, car) -> {
                                if (result.size() < length) {
                                    result.add(car);
                                }
                            },
                            MyArrayList::addAll);
        } catch (NoSuchFileException exception) {
            throw new IOException("Файл не найден: " + path, exception);
        } catch (UncheckedIOException exception) {
            IOException cause = exception.getCause();
            if (cause instanceof MalformedInputException) {
                throw new IOException("Файл должен быть в кодировке UTF-8.", cause);
            }
            throw new IOException("Ошибка чтения файла: " + path, cause);
        } catch (MalformedInputException exception) {
            throw new IOException("Файл должен быть в кодировке UTF-8.", exception);
        } catch (IOException exception) {
            throw new IOException("Ошибка чтения файла: " + path, exception);
        }

        if (cars.size() < length) {
            throw new IllegalArgumentException("В файле недостаточно автомобилей: требуется "
                    + length + ", найдено " + cars.size() + ".");
        }
        return cars;
    }

    private Car parseLine(String line, int lineNumber) {
        try {
            return parser.parse(line);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Строка " + lineNumber + ": "
                    + exception.getMessage(), exception);
        }
    }
}
