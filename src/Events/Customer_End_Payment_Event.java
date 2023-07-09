package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateTechnician;

public class Customer_End_Payment_Event extends Event_STK {

    public Customer_End_Payment_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        this.myCore.decCarsAfterCheckQuantity();
        this.myCore.decPayingCustomersQuantity();
        this.myCore.incTotalLeftCustomers();

        myCore.getAvgCustomerCountSystem().delete(1);
        if (myCore.isCustomerInQueue() && (myCore.showCustomerFromQueue().wantToPay() || myCore.isParkingPossible()) ) {
            if (!myCore.showCustomerFromQueue().wantToPay()) {myCore.getAvgCustomerCountQueueCar().delete(1);}
            Customer customer = myCore.getCustomerFromQueue();
            if (customer.wantToPay()) {
                myCore.getTechnicianByCustomer(this.customer).setState(StateTechnician.SERVING_PAY, customer);
                customer.setState(StateCustomer.PAYING);
            } else {
//                myCore.getAvgCustomerCountQueueCar().delete(1);
                customer.setStartTimeWaitingCheck(myCore.getCurrentTime());
                myCore.getTechnicianByCustomer(this.customer).setState(StateTechnician.TRANSPORTING_CAR, customer);
                customer.setState(StateCustomer.WAITING_CAR);
            }
            Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore ,customer);
            myCore.addEvent(processingEvent);
        } else {
            this.myCore.getAvgTechnicianFreeCount().add(1);
            this.myCore.returnTechnicianToReception(myCore.getTechnicianByCustomer(this.customer));
        }
        this.customer.setState(StateCustomer.LEFT);
        //end timer
        this.customer.setEndTimeInSystem(myCore.getCurrentTime());
        myCore.getAvgCustomerTimeInSystem().add(this.customer.getWaitingTimeInSystem());

//        myCore.removeCustomerFromAllCustomers(this.customer);
    }
}
