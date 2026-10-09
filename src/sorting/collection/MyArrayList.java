package sorting.collection;

public final class MyArrayList<T> {
    private Object[] items;
    private int size;

    public MyArrayList() {
        items = new Object[10];
    }

    public void add(T item) {
        if (item == null) {
            throw new NullPointerException("Элемент не должен быть null.");
        }
        if (size == items.length) {
            grow();
        }
        items[size] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) items[index];
    }

    public void set(int index, T item) {
        checkIndex(index);
        if (item == null) {
            throw new NullPointerException("Элемент не должен быть null.");
        }
        items[index] = item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public MyArrayList<T> copy() {
        MyArrayList<T> result = new MyArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(get(i));
        }
        return result;
    }

    private void grow() {
        Object[] expandedItems = new Object[items.length * 2];
        for (int i = 0; i < size; i++) {
            expandedItems[i] = items[i];
        }
        items = expandedItems;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс " + index + " вне списка размером " + size + ".");
        }
    }
}
