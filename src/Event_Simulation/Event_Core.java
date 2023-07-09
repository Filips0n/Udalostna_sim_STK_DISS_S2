package Event_Simulation;

import Events.General.Event_Base;
import Events.General.System_Event;

import java.util.PriorityQueue;
import java.util.Random;

import static java.lang.Thread.sleep;

public abstract class Event_Core extends Sim_Core{
    protected final double endTime;
    protected final double startTime;
    protected PriorityQueue<Event_Base> timeLine;
    protected double currentTime;

    protected boolean isSimStopped;

    private System_Event sysEvent;
    public Event_Core(Random seedGenerator, double startTime, double endTime) {
        super(seedGenerator);
        timeLine = new PriorityQueue<>();
        isSimStopped = false;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addEvent(Event_Base e) {
        if (e.getTime() < currentTime) {throw new RuntimeException("Event time is lower than current simulation time");}
        this.timeLine.add(e);
    }

    @Override
    protected void oneReplication(double maxTime) {
        if (simMode == SimulationMode.NORMAL) {createSystemEvent();}
        Event_Base event;
        while (!timeLine.isEmpty()) {
            event = timeLine.poll();
            currentTime = event.getTime();
            if (currentTime > maxTime) {
                break;
            }
            event.execute();
            if (simMode == SimulationMode.NORMAL) {refreshGui();}
            if (!isRunning) {break;}
            while (isSimStopped) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {throw new RuntimeException(e);}
            }
        }
        currentTime = maxTime;
    }

    private void createSystemEvent() {
        sysEvent = new System_Event(startTime, this);
        this.addEvent(sysEvent);
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setSimStopped(boolean simStopped) {isSimStopped = simStopped;}

    public void setSimulationSpeed(double value) {
        sysEvent.setDuration(value);
    }

    public double getStartTime() {
        return startTime;
    }
}
