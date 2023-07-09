package Sim_Actors;

public abstract class Actor {

    protected int id;

    public Actor(int id) {
        this.id = id;
    }

    public abstract String getName();
    public abstract String getStateDesc();
    public abstract String getCarDesc();

    public int getId() {
        return id;
    }
}
