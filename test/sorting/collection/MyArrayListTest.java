package sorting.collection;

public class MyArrayListTest {
    private static int checks;

    public static void main(String[] args) {
        checkBasicOperations();
        checkInvalidArguments();
        checkGrowth();
        checkCopy();

        System.out.println("Проверок пройдено: " + checks);
    }

    private static void checkBasicOperations() {
        MyArrayList<String> values = new MyArrayList<>();
        check(values.size() == 0, "Новая коллекция имеет размер 0");
        check(values.isEmpty(), "Новая коллекция пуста");

        values.add("Ford");
        values.add("Kia");
        values.add("Ford");
        check(values.size() == 3, "Каждое добавление увеличивает размер, включая повторения");
        check(!values.isEmpty(), "После добавления коллекция не пуста");
        check("Ford".equals(values.get(0)) && "Kia".equals(values.get(1))
                && "Ford".equals(values.get(2)), "Сохраняются значения и порядок добавления");
        check(values.size() == 3, "Чтение элементов не меняет размер");

        values.set(0, "Volvo");
        values.set(2, "Toyota");
        check("Volvo".equals(values.get(0)) && "Kia".equals(values.get(1))
                && "Toyota".equals(values.get(2)), "Замена первого и последнего элементов сохраняет соседей");
        check(values.size() == 3, "Замена элементов не меняет размер");
    }

    private static void checkInvalidArguments() {
        MyArrayList<String> values = new MyArrayList<>();
        expectIndexError(() -> values.get(0), "Чтение из пустой коллекции");
        expectIndexError(() -> values.set(0, "Ford"), "Замена в пустой коллекции");
        expectNullError(() -> values.add(null), "Добавление null в пустую коллекцию");
        check(values.isEmpty() && values.size() == 0, "Ошибки не изменяют пустую коллекцию");

        values.add("Ford");
        values.add("Kia");
        expectIndexError(() -> values.get(-1), "Отрицательный индекс чтения");
        expectIndexError(() -> values.get(values.size()), "Чтение по индексу, равному размеру");
        expectIndexError(() -> values.get(Integer.MAX_VALUE), "Слишком большой индекс чтения");
        expectIndexError(() -> values.set(-1, "Volvo"), "Отрицательный индекс замены");
        expectIndexError(() -> values.set(values.size(), "Volvo"), "Замена по индексу, равному размеру");
        expectIndexError(() -> values.set(Integer.MAX_VALUE, "Volvo"), "Слишком большой индекс замены");
        expectNullError(() -> values.add(null), "Добавление null в непустую коллекцию");
        expectNullError(() -> values.set(0, null), "Замена элемента на null");
        check(values.size() == 2 && !values.isEmpty(), "После ошибок сохраняется размер коллекции");
        check("Ford".equals(values.get(0)) && "Kia".equals(values.get(1)),
                "После ошибок сохраняются элементы и их порядок");
    }

    private static void checkGrowth() {
        MyArrayList<Integer> numbers = new MyArrayList<>();
        for (int i = 0; i < 40; i++) {
            numbers.add(i);
        }
        check(numbers.size() == 40, "Добавлены все 40 элементов");

        boolean correctOrder = true;
        for (int i = 0; i < numbers.size(); i++) {
            if (!Integer.valueOf(i).equals(numbers.get(i))) {
                correctOrder = false;
                break;
            }
        }
        check(correctOrder, "При расширении сохраняются все элементы и их порядок");
        numbers.set(39, 100);
        check(numbers.get(39) == 100 && numbers.get(38) == 38 && numbers.size() == 40,
                "После расширения последний элемент можно заменить без изменения размера");
    }

    private static void checkCopy() {
        Object first = new Object();
        Object second = new Object();
        MyArrayList<Object> original = new MyArrayList<>();
        original.add(first);
        original.add(second);

        MyArrayList<Object> copy = original.copy();
        check(copy != original, "Копия является отдельной коллекцией");
        check(copy.size() == 2 && !copy.isEmpty(), "Копия сохраняет размер исходной коллекции");
        check(copy.get(0) == first && copy.get(1) == second,
                "Копируются ссылки на те же элементы с сохранением порядка");

        Object replacement = new Object();
        copy.set(0, replacement);
        check(original.get(0) == first && copy.get(0) == replacement,
                "Замена элемента в копии не меняет оригинал");
        original.set(1, replacement);
        check(copy.get(1) == second && original.get(1) == replacement,
                "Замена элемента в оригинале не меняет копию");

        copy.add(second);
        check(copy.size() == 3 && original.size() == 2 && copy.get(2) == second,
                "Добавление в копию не меняет оригинал");
        original.add(first);
        check(original.size() == 3 && copy.size() == 3
                && original.get(2) == first && copy.get(2) == second,
                "Добавление в оригинал не меняет копию");

        MyArrayList<String> empty = new MyArrayList<>();
        MyArrayList<String> emptyCopy = empty.copy();
        check(emptyCopy != empty && emptyCopy.isEmpty() && emptyCopy.size() == 0,
                "Копия пустой коллекции тоже пуста и является отдельным объектом");
        emptyCopy.add("Ford");
        check(empty.isEmpty() && emptyCopy.size() == 1 && "Ford".equals(emptyCopy.get(0)),
                "В копию пустой коллекции можно добавлять элементы независимо от оригинала");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }

    private static void expectIndexError(Runnable action, String description) {
        try {
            action.run();
        } catch (IndexOutOfBoundsException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидалась ошибка индекса: " + description);
    }

    private static void expectNullError(Runnable action, String description) {
        try {
            action.run();
        } catch (NullPointerException exception) {
            checks++;
            return;
        }
        throw new AssertionError("Ожидалась ошибка null: " + description);
    }
}
