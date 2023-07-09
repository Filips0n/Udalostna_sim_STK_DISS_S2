package Statistics;

import Event_Simulation.Event_Core;

public class Weighted_Arithmetic_Mean {

    private final Event_Core myCore;
    private double lastTimeChanged;
    double sumValuesCount;
    double sumTotal;
    int currentCount;

    public Weighted_Arithmetic_Mean(Event_Core myCore) {
        this.myCore = myCore;
        this.lastTimeChanged = myCore.getStartTime();
        this.currentCount = 0;
    }

    public void add(int count) {
        update();
        this.currentCount += count;
    }

    public void delete(int count) {
        update();
        this.currentCount -= count;
        if (currentCount < 0) {
            currentCount = 0;
        }
    }

    private void update() {
        double time = myCore.getCurrentTime() - lastTimeChanged;
        sumValuesCount += time*currentCount;
        sumTotal += time;
        if (myCore.getCurrentTime() > 60000) {
            System.out.println();
        }
        lastTimeChanged = myCore.getCurrentTime();
    }

    public double getWeightedMean() {
        double value = sumValuesCount/sumTotal;
        if (Double.isNaN(value)) {
            return 0;
        }
        return value;
    }

    public void reset() {
        lastTimeChanged = myCore.getStartTime();
        currentCount = 0;
        sumValuesCount = 0;
        sumTotal = 0;
    }
}
