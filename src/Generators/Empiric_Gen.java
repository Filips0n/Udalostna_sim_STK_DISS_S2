package Generators;

import java.util.ArrayList;
import java.util.Random;

public class Empiric_Gen {
private Random classGen;
    private ArrayList<Random> generators;
    private double[][] empiricDist;

    public Empiric_Gen(Random seedGenerator, double[][] empiricDist) {
        generators = new ArrayList<>();
        classGen = new Random(seedGenerator.nextInt());
        this.empiricDist = empiricDist;
        for (int i = 0; i < empiricDist.length; i++) {
            generators.add(new Random(seedGenerator.nextInt()));
        }
    }

    public int getSample() {
        double selectedClassValue = classGen.nextDouble();
        double currentProbabilityValue = 0.0;
        int selectedClass = 0;
        while (currentProbabilityValue < selectedClassValue) {
            currentProbabilityValue += empiricDist[selectedClass][2];
          if (currentProbabilityValue < selectedClassValue) {selectedClass++;}
        }
        int min = (int)empiricDist[selectedClass][0];
        int max = (int)empiricDist[selectedClass][1];
        return generators.get(selectedClass).nextInt(max-min+1)+min;
    }
}
