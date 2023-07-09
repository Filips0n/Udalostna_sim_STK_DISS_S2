package Generators;

import Sim_Actors.CarType;

import java.util.Random;

public class CarType_Gen {
    private Random generator;

    public CarType_Gen(Random seedGenerator) {
        this.generator = new Random(seedGenerator.nextInt());
    }

    public CarType getSample() {
        double value = generator.nextDouble();
        if (value < 0.65) {
            return CarType.PERSONAL;
        } else if (value < (0.65+0.21)) {
            return CarType.DELIVERY;
        } else {
            return  CarType.TRUCK;
        }
    }
}
