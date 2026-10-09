package sorting.app;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import sorting.input.ConsoleInput;

public final class ConsoleApplication {
    private final ConsoleInput input;
    private final PrintStream output;

    public ConsoleApplication(ConsoleInput input, PrintStream output) {
        this.input = Objects.requireNonNull(input, "Читатель консоли не должен быть null.");
        this.output = Objects.requireNonNull(output, "Поток вывода не должен быть null.");
    }

    public void run() throws IOException {
        output.println("Приложение сортировки автомобилей");

        while (true) {
            printMenu();
            int command = input.readInt("Ваш выбор: ", 0, 4);
            switch (command) {
                case 1:
                    output.println("Загрузка из файла пока не реализована.");
                    break;
                case 2:
                    output.println("Сортировка пока не реализована.");
                    break;
                case 3:
                    output.println("Исходная коллекция пока не загружена.");
                    break;
                case 4:
                    output.println("Результата сортировки пока нет.");
                    break;
                case 0:
                    output.println("Работа программы завершена.");
                    return;
            }
        }
    }

    private void printMenu() {
        output.println();
        output.println("Меню:");
        output.println("1. Загрузить автомобили из файла");
        output.println("2. Отсортировать автомобили");
        output.println("3. Показать исходную коллекцию");
        output.println("4. Показать результат сортировки");
        output.println("0. Выход");
    }
}
