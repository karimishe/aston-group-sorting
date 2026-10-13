package collection;

import collection.MyArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyArrayListTest {
    @Test
    void newCollectionIsEmpty() {
        MyArrayList<String> values = new MyArrayList<>();
        assertEquals(0, values.size());
        assertTrue(values.isEmpty());
    }

    @Test
    void addPreservesOrderAndDuplicates() {
        MyArrayList<String> values = new MyArrayList<>();
        values.add("Ford");
        values.add("Kia");
        values.add("Ford");

        assertEquals(3, values.size());
        assertFalse(values.isEmpty());
        assertEquals("Ford", values.get(0));
        assertEquals("Kia", values.get(1));
        assertEquals("Ford", values.get(2));
        assertEquals(3, values.size(), "Чтение не меняет размер коллекции");
    }

    @Test
    void setPreservesNeighboursAndSize() {
        MyArrayList<String> values = new MyArrayList<>();
        values.add("Ford");
        values.add("Kia");
        values.add("Ford");

        values.set(0, "Volvo");
        values.set(2, "Toyota");

        assertEquals("Volvo", values.get(0));
        assertEquals("Kia", values.get(1));
        assertEquals("Toyota", values.get(2));
        assertEquals(3, values.size());
    }

    @Test
    void invalidOperationsPreserveEmptyCollection() {
        MyArrayList<String> values = new MyArrayList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> values.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> values.set(0, "Ford"));
        assertThrows(NullPointerException.class, () -> values.add(null));

        assertTrue(values.isEmpty());
        assertEquals(0, values.size());
    }

    @Test
    void invalidOperationsPreserveExistingElements() {
        MyArrayList<String> values = new MyArrayList<>();
        values.add("Ford");
        values.add("Kia");

        for (int index : new int[] {-1, values.size(), Integer.MAX_VALUE}) {
            assertThrows(IndexOutOfBoundsException.class, () -> values.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> values.set(index, "Volvo"));
        }
        assertThrows(NullPointerException.class, () -> values.add(null));
        assertThrows(NullPointerException.class, () -> values.set(0, null));

        assertEquals(2, values.size());
        assertFalse(values.isEmpty());
        assertEquals("Ford", values.get(0));
        assertEquals("Kia", values.get(1));
    }

    @Test
    void growingCollectionPreservesAllElements() {
        MyArrayList<Integer> numbers = new MyArrayList<>();
        for (int i = 0; i < 40; i++) {
            numbers.add(i);
        }

        assertEquals(40, numbers.size());
        for (int i = 0; i < numbers.size(); i++) {
            assertEquals(i, numbers.get(i).intValue(), "Элемент с индексом " + i);
        }

        numbers.set(39, 100);
        assertEquals(100, numbers.get(39).intValue());
        assertEquals(38, numbers.get(38).intValue());
        assertEquals(40, numbers.size());
    }

    @Test
    void copyHasIndependentStorageAndSameElements() {
        Object first = new Object();
        Object second = new Object();
        MyArrayList<Object> original = new MyArrayList<>();
        original.add(first);
        original.add(second);

        MyArrayList<Object> copy = original.copy();
        assertNotSame(original, copy);
        assertEquals(2, copy.size());
        assertFalse(copy.isEmpty());
        assertSame(first, copy.get(0));
        assertSame(second, copy.get(1));

        Object replacement = new Object();
        copy.set(0, replacement);
        assertSame(first, original.get(0));
        assertSame(replacement, copy.get(0));
        original.set(1, replacement);
        assertSame(second, copy.get(1));
        assertSame(replacement, original.get(1));

        copy.add(second);
        assertEquals(3, copy.size());
        assertEquals(2, original.size());
        assertSame(second, copy.get(2));
        original.add(first);
        assertEquals(3, original.size());
        assertEquals(3, copy.size());
        assertSame(first, original.get(2));
        assertSame(second, copy.get(2));
    }

    @Test
    void copyOfEmptyCollectionCanBeChangedIndependently() {
        MyArrayList<String> original = new MyArrayList<>();
        MyArrayList<String> copy = original.copy();

        assertNotSame(original, copy);
        assertTrue(copy.isEmpty());
        assertEquals(0, copy.size());
        copy.add("Ford");
        assertTrue(original.isEmpty());
        assertEquals(1, copy.size());
        assertEquals("Ford", copy.get(0));
    }

    @Test
    void addAllAppendsElementsWithoutChangingSource() {
        MyArrayList<String> target = new MyArrayList<>();
        target.add("Ford");
        MyArrayList<String> source = new MyArrayList<>();
        source.add("Kia");
        source.add("Ford");

        target.addAll(source);

        assertEquals(3, target.size());
        assertEquals("Ford", target.get(0));
        assertEquals("Kia", target.get(1));
        assertEquals("Ford", target.get(2));
        assertEquals(2, source.size());
        assertEquals("Kia", source.get(0));
        assertEquals("Ford", source.get(1));

        target.set(1, "Volvo");
        assertEquals("Kia", source.get(0));
    }

    @Test
    void addAllToItselfCopiesOriginalElementsOnce() {
        MyArrayList<Integer> values = new MyArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(i);
        }

        values.addAll(values);

        assertEquals(16, values.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(i, values.get(i).intValue());
            assertEquals(i, values.get(i + 8).intValue());
        }
    }

    @Test
    void addAllWithEmptyCollectionPreservesElements() {
        MyArrayList<String> values = new MyArrayList<>();
        values.add("Ford");
        MyArrayList<String> empty = new MyArrayList<>();

        values.addAll(empty);
        empty.addAll(empty);

        assertEquals(1, values.size());
        assertEquals("Ford", values.get(0));
        assertTrue(empty.isEmpty());
    }

    @Test
    void addAllRejectsNullWithoutChangingCollection() {
        MyArrayList<String> values = new MyArrayList<>();
        values.add("Ford");

        assertThrows(NullPointerException.class, () -> values.addAll(null));

        assertEquals(1, values.size());
        assertEquals("Ford", values.get(0));
    }
}
