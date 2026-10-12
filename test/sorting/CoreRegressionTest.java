package sorting;

import org.junit.jupiter.api.Test;
import sorting.algorithm.InsertionSortTest;
import sorting.app.ConsoleApplicationTest;
import sorting.app.ModuleIntegrationTest;
import sorting.collection.MyArrayListTest;
import sorting.input.ConsoleInputTest;
import sorting.model.CarTest;
import sorting.source.CarLineParserTest;
import sorting.source.FileCarSourceTest;

/**
 * Запускает существующие проверки через JUnit до их переноса в отдельные тестовые методы.
 */
public class CoreRegressionTest {
    @Test
    void checksCar() {
        CarTest.main(new String[0]);
    }

    @Test
    void checksCollection() {
        MyArrayListTest.main(new String[0]);
    }

    @Test
    void checksConsoleInput() throws Exception {
        ConsoleInputTest.main(new String[0]);
    }

    @Test
    void checksConsoleApplication() throws Exception {
        ConsoleApplicationTest.main(new String[0]);
    }

    @Test
    void checksModuleIntegration() throws Exception {
        ModuleIntegrationTest.main(new String[0]);
    }

    @Test
    void checksCarLineParser() {
        CarLineParserTest.main(new String[0]);
    }

    @Test
    void checksFileSource() throws Exception {
        FileCarSourceTest.main(new String[0]);
    }

    @Test
    void checksInsertionSort() {
        InsertionSortTest.main(new String[0]);
    }
}
