package sorting.source;

import sorting.collection.MyArrayList;
import sorting.contract.OccurrenceCounter;
import sorting.model.Car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarCounter implements OccurrenceCounter<Car> {
    @Override
    public int count(MyArrayList<Car> items, Car target, int threadCount) throws InterruptedException {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Количество потоков должно быть положительным.");
        }
        if (items == null) {
            throw new IllegalArgumentException("Список автомобилей не может быть null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Автомобиль не может быть null.");
        }

        List<Thread> threads = new ArrayList<>();
        int[] counts = new int[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;
            Thread thread = new Thread(() -> counts[threadIndex] = countInThread(items, target, threadCount, threadIndex));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return Arrays.stream(counts).sum();
    }

    private int countInThread(MyArrayList<Car> items, Car target, int threadCount, int threadIndex) {
        int count = 0;
        for (int i = threadIndex; i < items.size(); i += threadCount) {
            Car car = items.get(i);
            if (
                    car.getPower() == target.getPower() &&
                    car.getModel().equals(target.getModel()) &&
                    car.getProductionYear() == target.getProductionYear()
            ) {
                count++;
            }
        }
        return count;
    }
}
