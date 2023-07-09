package Events.General;

import Event_Simulation.Event_Core;

import static java.lang.Thread.sleep;

public class System_Event extends Event_Base<Event_Core> {

    private double speed = 1;
    private int sleepValue = 1000;

    public System_Event(double time, Event_Core myCore) {
        super(time, myCore);
    }

    @Override
    public void execute() {
        this.time = myCore.getCurrentTime() + 1;
        myCore.addEvent(this);
        try {
            //Faster/Slower replication
            sleep(Math.round(sleepValue*(1/speed)) );
        } catch (InterruptedException e) {throw new RuntimeException(e);}
    }

    public void setDuration(double duration) {
        this.speed = duration;
    }

    public void setSleepValue(int sleepValue) {this.sleepValue = sleepValue;}
}
