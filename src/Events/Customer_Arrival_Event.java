package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateTechnician;

public class Customer_Arrival_Event extends Event_STK {


    public Customer_Arrival_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        if (myCore.getCurrentTime() > STK.getLatestArrivalTime()) {return;}
        //start timer
        //New Arrival Event
        this.time = myCore.getCurrentTime() + myCore.getCustomerArrivalGenerator().getSample();
        myCore.addEvent(this);
        //Generate new customer
        this.myCore.incTotalCustomers();
        this.customer = new Customer(myCore.getCustomerId(), myCore.getCarTypeGenerator().getSample(), StateCustomer.QUEUE_CAR);
        this.customer.setStartTimeInQueue(myCore.getCurrentTime());
        //
        myCore.addCustomerToAllCustomers(this.customer);
        //
        myCore.getAvgCustomerCountSystem().add(1);
        if (myCore.isTechnicianAvailable() && myCore.isParkingPossible() && !myCore.isCustomerInQueue()) {
            this.myCore.getAvgTechnicianFreeCount().delete(1);
            this.customer.setStartTimeWaitingCheck(myCore.getCurrentTime());
            myCore.getTechnicianFromReception().setState(StateTechnician.TRANSPORTING_CAR, this.customer);
            this.customer.setState(StateCustomer.WAITING_CAR);
            Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore ,customer);
            myCore.addEvent(processingEvent);
        } else {
            myCore.getAvgCustomerCountQueueCar().add(1);
            myCore.addCustomerToQueue(this.customer);
            this.customer.setState(StateCustomer.QUEUE_CAR);
        }
    }
}
