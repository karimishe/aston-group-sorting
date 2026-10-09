package sorting.app;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Comparator;
import sorting.algorithm.InsertionSort;
import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.contract.SortStrategy;
import sorting.input.ConsoleInput;
import sorting.model.Car;

public class ModuleIntegrationTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        checkSources();
        checkAlgorithmsAndComparators();
        checkSourceFailures();
        checkSortFailure();
        checkManualEndOfInput();
        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkSources() throws IOException {
        for (int choice = 2; choice <= 3; choice++) {
            TestModules modules = new TestModules();
            modules.manual.readMarker = true;
            String commands = "1\n" + choice + "\nошибка\n0\n2\n"
                    + (choice == 2 ? "ввод источника\n" : "") + "3\n0\n";
            String text = run(commands, modules);
            TestSource selected = choice == 2 ? modules.manual : modules.random;
            TestSource other = choice == 2 ? modules.random : modules.manual;

            check(!text.contains("Вручную (не реализовано)")
                    && !text.contains("Случайные данные (не реализовано)"),
                    "Подключённые источники становятся доступными без изменения меню");
            check(selected.calls == 1 && other.calls == 0,
                    "Меню вызывает только выбранный источник");
            check(selected.length == 2, "Источник получает проверенную длину");
            check(text.contains("Введите целое число.")
                    && text.contains("Введите число от 1 до 2147483647."),
                    "Для нового источника проверяются число и нижняя граница длины");
            check(hasCollection(text, "Исходная коллекция", "100;Audi;2018", "200;Kia;2020"),
                    "Показана коллекция, возвращённая выбранным источником");
            check(modules.factoryCalls[0] == 1 && modules.factoryCalls[1] == 1,
                    "Показ меню и загрузка используют один экземпляр каждого источника");
            check(!text.contains("Путь к файлу:"), "Новые источники не запрашивают путь к файлу");
            if (choice == 2) {
                check("ввод источника".equals(modules.manual.marker),
                        "Ручной источник получает общий ConsoleInput и читает после длины");
            }
        }
    }

    private static void checkAlgorithmsAndComparators() throws IOException {
        for (int algorithm = 2; algorithm <= 3; algorithm++) {
            for (int comparison = 2; comparison <= 4; comparison++) {
                TestModules modules = new TestModules();
                String text = run("1\n3\n3\n2\n" + algorithm + "\n" + comparison
                        + "\n4\n3\n0\n", modules);
                TestSort selected = algorithm == 2 ? modules.selection : modules.bubble;
                TestSort other = algorithm == 2 ? modules.bubble : modules.selection;
                Comparator<Car> expected = comparison == 2 ? modules.power
                        : comparison == 3 ? modules.model : modules.year;

                check(!text.contains("(не реализовано)"),
                        "Доступность алгоритмов и компараторов определяется возвращёнными модулями");
                check(selected.calls == 1 && other.calls == 0,
                        "Выполняется только выбранный алгоритм");
                check(selected.comparator == expected,
                        "Алгоритм получает именно выбранный экземпляр компаратора");
                check(selected.items != modules.random.result,
                        "Алгоритм получает копию исходной коллекции");
                check(hasCollection(text, "Результат сортировки",
                        "200;Kia;2020", "150;Honda;2010", "100;Audi;2018"),
                        "Приложение использует результат подключённого алгоритма и компаратора");
                check(hasCollection(text, "Исходная коллекция",
                        "100;Audi;2018", "200;Kia;2020", "150;Honda;2010"),
                        "Подключённый алгоритм не меняет исходный порядок");
                check(countLines(text, "Отсортировано автомобилей: 3") == 1,
                        "Подключённая сортировка завершается сообщением");
                for (int calls : modules.factoryCalls) {
                    check(calls == 1,
                            "Фабрика вызывается один раз для показа соответствующего меню");
                }
            }
        }
    }

    private static void checkSourceFailures() throws IOException {
        for (boolean invalidData : new boolean[] {false, true}) {
            TestModules modules = new TestModules();
            modules.manual.invalidData = invalidData;
            if (!invalidData) {
                modules.manual.failure = new IOException("Ошибка источника");
            }
            String text = run("1\n3\n3\n2\n1\n1\n1\n2\n1\n3\n4\n0\n", modules);
            check(text.contains("Не удалось загрузить автомобили: Ошибка источника"),
                    "Ошибки чтения и валидации нового источника обрабатываются приложением");
            check(hasCollection(text, "Исходная коллекция",
                    "100;Audi;2018", "200;Kia;2020", "150;Honda;2010"),
                    "Ошибка нового источника сохраняет исходные данные");
            check(hasCollection(text, "Результат сортировки",
                    "100;Audi;2018", "150;Honda;2010", "200;Kia;2020"),
                    "Ошибка нового источника сохраняет предыдущую сортировку");
            check(countLines(text, "Работа программы завершена.") == 1,
                    "После ошибки нового источника можно продолжить работу и выйти");
        }
    }

    private static void checkSortFailure() throws IOException {
        TestModules modules = new TestModules();
        modules.selection.fail = true;
        String text = run("1\n3\n3\n2\n1\n1\n2\n2\n1\n3\n4\n0\n", modules);
        check(text.contains("Не удалось отсортировать автомобили: Ошибка алгоритма"),
                "Приложение сообщает об ошибке подключённого алгоритма");
        check(modules.selection.calls == 1, "Неисправный алгоритм действительно был вызван");
        check(hasCollection(text, "Исходная коллекция",
                "100;Audi;2018", "200;Kia;2020", "150;Honda;2010"),
                "Частичная перестановка до ошибки не меняет исходные данные");
        check(hasCollection(text, "Результат сортировки",
                "100;Audi;2018", "150;Honda;2010", "200;Kia;2020"),
                "Ошибка новой сортировки сохраняет предыдущий результат");
        check(countLines(text, "Отсортировано автомобилей: 3") == 1,
                "Неудачная сортировка не выдаётся за успешную");
    }

    private static void checkManualEndOfInput() throws IOException {
        TestModules modules = new TestModules();
        modules.manual.readMarker = true;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader("1\n2\n1\n"), stream);
        try {
            new ConsoleApplication(input, stream, modules).run();
            throw new AssertionError("Ожидалась ошибка окончания ручного ввода");
        } catch (EOFException exception) {
            checks++;
        }
        String text = output.toString("UTF-8");
        check(!text.contains("Работа программы завершена.")
                && !text.contains("Не удалось загрузить автомобили:"),
                "Окончание ручного ввода передаётся вызывающему коду без продолжения меню");
        check(modules.manual.calls == 1, "Ввод завершился внутри ручного источника");
    }

    private static String run(String commands, Modules modules) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output, true, "UTF-8");
        ConsoleInput input = new ConsoleInput(new StringReader(commands), stream);
        new ConsoleApplication(input, stream, modules).run();
        return output.toString("UTF-8");
    }

    private static boolean hasCollection(String text, String title, String... records) {
        StringBuilder block = new StringBuilder(title).append(" (").append(records.length)
                .append("):").append(System.lineSeparator());
        for (int i = 0; i < records.length; i++) {
            block.append(i + 1).append(". ").append(records[i]).append(System.lineSeparator());
        }
        return text.contains(block.toString());
    }

    private static int countLines(String text, String ending) {
        int count = 0;
        for (String line : text.split("\\r?\\n")) {
            if (line.endsWith(ending)) {
                count++;
            }
        }
        return count;
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }

    private static Car car(int power, String model, int year) {
        return new Car.Builder().power(power).model(model).productionYear(year).build();
    }

    private static Comparator<Car> descendingPower() {
        return new Comparator<Car>() {
            @Override
            public int compare(Car first, Car second) {
                return Integer.compare(second.getPower(), first.getPower());
            }
        };
    }

    // Контрольные модули проверяют выбор и подключение, а не отдельные способы сортировки.
    private static class TestModules extends Modules {
        private final TestSource manual = new TestSource();
        private final TestSource random = new TestSource();
        private final TestSort selection = new TestSort();
        private final TestSort bubble = new TestSort();
        private final Comparator<Car> power = descendingPower();
        private final Comparator<Car> model = descendingPower();
        private final Comparator<Car> year = descendingPower();
        private final int[] factoryCalls = new int[7];

        @Override
        public DataSource<Car> manualSource(ConsoleInput input) {
            factoryCalls[0]++;
            manual.input = input;
            return manual;
        }

        @Override
        public DataSource<Car> randomSource() {
            factoryCalls[1]++;
            return random;
        }

        @Override
        public SortStrategy<Car> selectionSort() {
            factoryCalls[2]++;
            return selection;
        }

        @Override
        public SortStrategy<Car> bubbleSort() {
            factoryCalls[3]++;
            return bubble;
        }

        @Override
        public Comparator<Car> powerComparator() {
            factoryCalls[4]++;
            return power;
        }

        @Override
        public Comparator<Car> modelComparator() {
            factoryCalls[5]++;
            return model;
        }

        @Override
        public Comparator<Car> productionYearComparator() {
            factoryCalls[6]++;
            return year;
        }
    }

    private static class TestSource implements DataSource<Car> {
        private ConsoleInput input;
        private boolean readMarker;
        private String marker;
        private int calls;
        private int length;
        private IOException failure;
        private boolean invalidData;
        private MyArrayList<Car> result;

        @Override
        public MyArrayList<Car> load(int length) throws IOException {
            calls++;
            this.length = length;
            if (failure != null) {
                throw failure;
            }
            if (invalidData) {
                throw new IllegalArgumentException("Ошибка источника");
            }
            if (readMarker) {
                marker = input.readLine("Данные источника: ");
            }
            Car[] data = {car(100, "Audi", 2018), car(200, "Kia", 2020), car(150, "Honda", 2010)};
            result = new MyArrayList<>();
            for (int i = 0; i < length; i++) {
                result.add(data[i]);
            }
            return result;
        }
    }

    private static class TestSort implements SortStrategy<Car> {
        private int calls;
        private boolean fail;
        private MyArrayList<Car> items;
        private Comparator<Car> comparator;

        @Override
        public void sort(MyArrayList<Car> items, Comparator<Car> comparator) {
            calls++;
            this.items = items;
            this.comparator = comparator;
            if (fail) {
                Car first = items.get(0);
                items.set(0, items.get(1));
                items.set(1, first);
                throw new IllegalStateException("Ошибка алгоритма");
            }
            new InsertionSort<Car>().sort(items, comparator);
        }
    }
}
