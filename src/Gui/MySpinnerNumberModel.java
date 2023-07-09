package Gui;

import javax.swing.*;

public class MySpinnerNumberModel extends SpinnerNumberModel {

    public MySpinnerNumberModel(double value, double minimum, double maximum) {
        super(value, minimum, maximum, 1);
    }

    @Override
    public Object getNextValue() {
        double currentValue = (Double) getValue();
        double nextValue = newValue(currentValue, true);
        if (nextValue > (double) getMaximum()) {return null;}
        return nextValue;
    }

    @Override
    public Object getPreviousValue() {
        double currentValue = (Double) getValue();
        double prevValue = newValue(currentValue, false);
        if (prevValue < (double) getMinimum()) {return null;}
        return prevValue;
    }

    private double newValue(double value, boolean increase) {
        if (increase) {
            if (value < 1) {
                return value * 2;
            } else {
                return value * 10;
            }
        } else {//decrease
            if (value <= 1) {
                return value * 0.5;
            } else {
                return value * 0.1;
            }
        }
    }
}

