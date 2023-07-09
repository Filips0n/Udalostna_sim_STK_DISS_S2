import Event_Simulation.STK;
import Generators.Test.WriteToTxt;
import Gui.Gui;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Random seedGenerator = new Random();
        new Gui(new STK(seedGenerator));

//        WriteToTxt wrt = new WriteToTxt(new Exponential_Gen(seedGenerator, (double)33));
    }
}