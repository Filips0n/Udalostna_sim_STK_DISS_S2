package Sim_Actors;

import Sim_Actors.States.StateMechanic;

public class Mechanic extends Actor {
    private StateMechanic state;
    private Customer currentCustomer;

    public Mechanic(int id) {
        super(id);
        state = StateMechanic.FREE;
    }

    @Override
    public String getName() {
        return "Mechanik " + id;
    }

    @Override
    public String getStateDesc() {
        String description = "";
        switch (state){
            case FREE:
                description = "Volny";
                break;
            case CHECKING_CAR:
                description = "Kontrola auta";
                break;
        }
        return description;
    }

    @Override
    public String getCarDesc() {
        return currentCustomer == null ? "" : currentCustomer.getCarDesc() + "-Zk." + currentCustomer.getId();
    }

    public void setState(StateMechanic state, Customer currentCustomer) {
        this.state = state;
        this.currentCustomer = currentCustomer;
    }

    public StateMechanic getState() {
        return state;
    }

    public Customer getServingCustomer() {
        return currentCustomer;
    }
}
