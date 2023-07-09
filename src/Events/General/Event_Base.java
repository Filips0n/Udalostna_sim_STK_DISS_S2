package Events.General;

public abstract class Event_Base<T> implements Comparable<Event_Base> {
    protected double time;
    protected T myCore;

    public Event_Base(double time, T myCore) {
        this.time = time;
        this.myCore = myCore;
    }

    public abstract void execute();

    @Override
    public int compareTo(Event_Base event) {
        if(this.time==event.getTime())
            return 0;
        else if(this.time> event.getTime())
            return 1;
        else
            return -1;
    }

    public double getTime() {
        return time;
    }

    public T getMyCore() {
        return myCore;
    }
}
