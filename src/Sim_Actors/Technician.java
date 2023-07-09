package Sim_Actors;

import Sim_Actors.States.StateTechnician;

public class Technician extends Actor {

    private StateTechnician state;
    private Customer currentCustomer;

    public Technician(int id) {
        super(id);
        state = StateTechnician.FREE;
    }

    @Override
    public String getName() {
        return "Technik " + id;
    }

    @Override
    public String getStateDesc() {
        String description = "";
        switch (state){
            case FREE:
                description = "Volny";
                break;
            case SERVING_PAY:
                description = "Platba";
                break;
            case TRANSPORTING_CAR:
                description = "Transportuje";
                break;
        }
        return description;
    }

    @Override
    public String getCarDesc() {
        return currentCustomer == null ? "" : currentCustomer.getCarDesc() + "-Zk." + currentCustomer.getId();
    }

    public void setState(StateTechnician state, Customer currentCustomer) {
        this.state = state;
        this.currentCustomer = currentCustomer;
    }

    public StateTechnician getState() {
        return state;
    }

    public Customer getServingCustomer() {
        return currentCustomer;
    }
}
