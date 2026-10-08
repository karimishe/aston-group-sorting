package sorting.model;

import java.time.LocalDate;

public final class Car {
    private final int power;
    private final String model;
    private final int productionYear;

    private Car(int power, String model, int productionYear) {
        this.power = power;
        this.model = model;
        this.productionYear = productionYear;
    }

    public int getPower() {
        return power;
    }

    public String getModel() {
        return model;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public static class Builder {
        private int power;
        private String model;
        private int productionYear;

        public Builder power(int power) {
            this.power = power;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder productionYear(int productionYear) {
            this.productionYear = productionYear;
            return this;
        }

        public Car build() {
            if (power <= 0) {
                throw new IllegalArgumentException("Мощность должна быть положительным числом.");
            }
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Модель должна быть непустой строкой.");
            }
            for (int i = 0; i < model.length(); i++) {
                char character = model.charAt(i);
                if (character == ';' || character == '\n' || character == '\r') {
                    throw new IllegalArgumentException("Модель не должна содержать ';' или переводы строк.");
                }
            }

            int currentYear = LocalDate.now().getYear();
            if (productionYear < 1900 || productionYear > currentYear) {
                throw new IllegalArgumentException("Год выпуска должен быть от 1900 до " + currentYear + ".");
            }

            return new Car(power, model.trim(), productionYear);
        }
    }
}
