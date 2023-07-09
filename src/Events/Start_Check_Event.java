package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateTechnician;

public class Start_Check_Event extends Event_STK {
    public Start_Check_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        End_Check_Event endCheckEvent = new End_Check_Event(myCore.getCurrentTime()+((double) myCore.getSampleByCarType(customer.getCar().getCarType())), myCore, customer);
        myCore.addEvent(endCheckEvent);
    }
}
