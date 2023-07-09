package Generators;

import java.util.Random;

public class Discrete_Gen {
    private Random generator;
    private int min;
    private int max;

    public Discrete_Gen(Random seedGenerator, int min, int max) {
        this.generator = new Random(seedGenerator.nextInt());
        this.min = min;
        this.max = max;
    }

    public int getSample() {
        return generator.nextInt(max-min+1)+min;
    }
}
