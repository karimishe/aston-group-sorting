package sorting.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import sorting.input.ConsoleInput;

public class Main {
    public static void main(String[] args) {
        ConsoleInput input = new ConsoleInput(
                new InputStreamReader(System.in, StandardCharsets.UTF_8), System.out);
        Modules modules = new Modules();
        ConsoleApplication application = new ConsoleApplication(input, System.out, modules);
        try {
            application.run();
        } catch (IOException exception) {
            System.err.println("Ошибка ввода: " + exception.getMessage());
            System.exit(1);
        }
    }
}
