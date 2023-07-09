package Generators;

import java.util.Random;
public class Continuous_Gen {

    private Random generator;
    private double min;
    private double max;

    public Continuous_Gen(Random seedGenerator, double min, double max) {
        this.generator = new Random(seedGenerator.nextInt());
        this.min = min;
        this.max = max;
    }

    public double getSample() {
        return min + (max - min) * generator.nextDouble();
    }
}
