package sorting.source;
import sorting.collection.MyArrayList;
import sorting.contract.DataSource;
import sorting.model.Car;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class ManualCarSource implements DataSource<Car> {
    private Scanner scanner;

    public ManualCarSource(){
        this.scanner = new Scanner(System.in);
    }
    public ManualCarSource(Scanner scanner){
        this.scanner = scanner;
    }
    @Override
    public MyArrayList<Car> load(int length) throws IOException {
        System.out.println("Ввод характеристик автомобилей");
        MyArrayList<Car> cars = new MyArrayList<>();
        CarConsoleReader carConsoleReader = new CarConsoleReader(scanner);
        for (int i = 0; i < length; i++)
        {
            cars.add(carConsoleReader.readCar());
        }
        return cars;
    }
}
