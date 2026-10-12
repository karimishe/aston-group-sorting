package sorting.source;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.PrintStream;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import sorting.collection.MyArrayList;
import sorting.input.ConsoleInput;
import sorting.model.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreamManualCarSourceTest {
    @Test
    void returnsRequestedNumberAndRetriesInvalidValues() throws Exception {
        ConsoleInput input = input("oops\n150\n\nBad;Model\nFord\nbad\n2020\n200\nToyota\n2022\n");
        StreamManualCarSource source = new StreamManualCarSource(input);

        MyArrayList<Car> cars = source.load(2);

        assertEquals(2, cars.size());
        assertEquals(150, cars.get(0).getPower());
        assertEquals("Ford", cars.get(0).getModel());
        assertEquals(2020, cars.get(0).getProductionYear());
        assertEquals("Toyota", cars.get(1).getModel());
        assertEquals(2022, cars.get(1).getProductionYear());
    }

    @Test
    void propagatesEofExceptionInsteadOfWrappingIt() {
        StreamManualCarSource source = new StreamManualCarSource(input(""));

        assertThrows(EOFException.class, () -> source.load(1));
    }

    @Test
    void propagatesEofAfterSomeCarsHaveBeenRead() {
        StreamManualCarSource source = new StreamManualCarSource(input("150\nFord\n2020\n"));

        assertThrows(EOFException.class, () -> source.load(2));
    }

    @Test
    void rejectsNonPositiveLengthWithoutReadingInput() {
        StreamManualCarSource source = new StreamManualCarSource(input(""));

        assertThrows(IllegalArgumentException.class, () -> source.load(0));
        assertThrows(IllegalArgumentException.class, () -> source.load(-1));
    }

    private ConsoleInput input(String value) {
        return new ConsoleInput(new StringReader(value), new PrintStream(new ByteArrayOutputStream()));
    }
}
