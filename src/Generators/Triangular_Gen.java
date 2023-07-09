package Generators;

import java.util.Random;

public class Triangular_Gen {
    double min;
    double max;
    double mode;
    private Random generator;

    public Triangular_Gen(Random seedGenerator,double min, double max, double mode) {
        this.generator = new Random(seedGenerator.nextInt());
        this.min = min;
        this.max = max;
        this.mode = mode;
    }

    public double getSample(){
        //https://en.wikipedia.org/wiki/Triangular_distribution#Generating_triangular-distributed_random_variates
        double range = max - min;
        double leftSlope = (mode - min) / range;
        double value = generator.nextDouble();

        if (value < leftSlope) {
            return min + Math.sqrt(value * range * (mode - min));
        } else {
            return max - Math.sqrt((1.0 - value) * range * (max - mode));
        }
    }
}
