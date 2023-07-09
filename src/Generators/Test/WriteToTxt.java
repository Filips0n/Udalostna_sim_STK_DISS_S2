package Generators.Test;

import Generators.Exponential_Gen;

import java.io.FileWriter;
import java.io.IOException;

public class WriteToTxt {

    public WriteToTxt(Exponential_Gen gen) {
        try {
            FileWriter writer = new FileWriter("expoTest.txt");
            for (int i = 0; i < 1000000; i++) {
                double randomValue = gen.getSample();
                writer.write(String.valueOf(randomValue));
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
