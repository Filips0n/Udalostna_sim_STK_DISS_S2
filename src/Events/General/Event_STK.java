package Events.General;

import Event_Simulation.STK;
import Sim_Actors.Customer;

public abstract class Event_STK extends Event_Base<STK> {
    protected Customer customer;

    public Event_STK(double time, STK myCore, Customer customer) {
        super(time, myCore); this.customer = customer;
    }

}
