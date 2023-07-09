package Events;

import Event_Simulation.Event_Core;
import Event_Simulation.STK;
import Events.General.Event_Base;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;

import java.util.ArrayList;

public class Leave_STK_Event extends Event_Base<Event_Core> {

    public Leave_STK_Event(double time, Event_Core myCore) {
        super(time, myCore);
    }

    @Override
    public void execute() {
        //naplanujem na dalsich 5 minut
        myCore.addEvent( new Leave_STK_Event(myCore.getCurrentTime()+(60*5), myCore));

        ArrayList<Customer> allQueueCarWaiting = ((STK)myCore).getAllQueueCarWaiting();
        if (!allQueueCarWaiting.isEmpty()) {
            for (Customer customer : allQueueCarWaiting) {
                if (myCore.getCurrentTime() - customer.getStartTimeInQueue() > 180) {
                    ((STK)myCore).removeCustomerFromQueue(customer);
                    //Statistiky a state
                    ((STK)myCore).getAvgCustomerCountSystem().delete(1);
                    customer.setState(StateCustomer.LEFT_QUEUE);
                    customer.setEndTimeInSystem(myCore.getCurrentTime());
                    customer.setEndTimeInQueue(myCore.getCurrentTime());
                    ((STK)myCore).getAvgCustomerTimeInQueueCar().add(customer.getWaitingTimeInQueue());
                    ((STK)myCore).getAvgCustomerTimeInSystem().add(customer.getWaitingTimeInSystem());
                }
            }
        }
    }
}
