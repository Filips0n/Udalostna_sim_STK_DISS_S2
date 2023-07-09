package Generators;

import java.util.Random;

public class Exponential_Gen {

    private Random generator;
    private double lambda;

    public Exponential_Gen(Random seedGenerator, double mean) {
        this.generator = new Random(seedGenerator.nextInt());
        this.lambda = 1/mean;
    }

    public double getSample() {
        //https://en.wikipedia.org/wiki/Exponential_distribution#Random_variate_generation
        return -Math.log(1-generator.nextDouble())/lambda;
    }
}
