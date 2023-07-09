package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateTechnician;

public class Customer_Start_Payment_Event extends Event_STK {

    public Customer_Start_Payment_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        this.customer.setWantsToPay(true);
            this.customer.setEndTimeInWaitingCheck(myCore.getCurrentTime());
            myCore.getAvgCustomerTimeWaitingCheck().add(this.customer.getWaitingTimeInWaitingCheck());
            this.customer.setStartTimeInQueuePay(myCore.getCurrentTime());
        if (myCore.isTechnicianAvailable()) {
            this.myCore.getAvgTechnicianFreeCount().delete(1);
            myCore.getTechnicianFromReception().setState(StateTechnician.SERVING_PAY, this.customer);
            this.customer.setState(StateCustomer.PAYING);
            Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore, customer);
            myCore.addEvent(processingEvent);
        } else {
            this.customer.setState(StateCustomer.QUEUE_PAY);
//            this.customer.setStartTimeInPayQueue(myCore.getCurrentTime());
            this.customer.setStartTimeInQueuePay(myCore.getCurrentTime());
            myCore.addCustomerToQueue(this.customer);
        }
    }
}
