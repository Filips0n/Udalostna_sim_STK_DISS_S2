package Event_Simulation;

import Gui.ISimDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Sim_Core {
    protected final Random seedGenerator;
    protected int completedReplications;
    protected int replications;
    protected boolean isRunning = false;
    protected List<ISimDelegate> delegates = new ArrayList<>();
    protected SimulationMode simMode;
    public Sim_Core(Random seedGenerator) {
        this.seedGenerator = seedGenerator;
    }

    public void setSimMode(SimulationMode simMode) {
        this.simMode = simMode;
    }

    public void simulate(int replications, double maxTime) {
        beforeSimulation();
        isRunning = true;
        this.replications = replications;
        this.completedReplications = 0;
        for (int i = 0; i < replications; i++) {
            beforeReplication();
            oneReplication(maxTime);
            if (!isRunning) {
                break;
            } else {
                this.completedReplications++;
            }
            afterReplication(completedReplications);
        }
        afterSimulation();
    }

    public void registerDelegate(ISimDelegate delegate) {
        delegates.add(delegate);
    }

    protected void refreshGui() {
        for (ISimDelegate delegate : delegates) {
            delegate.refresh(this);
        }
    }

    protected abstract void afterSimulation();

    protected abstract void afterReplication(int replicationNumber);

    protected abstract void beforeSimulation();

    protected abstract void oneReplication(double maxTime);

    protected abstract void beforeReplication();

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getCompletedReplications() {
        return completedReplications;
    }
}
