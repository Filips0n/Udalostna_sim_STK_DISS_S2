package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Car;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;

public class Processing_Customer_Event extends Event_STK {

    public Processing_Customer_Event(double currentTime, STK myCore, Customer customer) {
        super(currentTime, myCore, customer);
    }

    @Override
    public void execute() {
        if (customer.wantToPay()) {
            myCore.incPayingCustomersQuantity();
            this.customer.setEndTimeInQueuePay(myCore.getCurrentTime());
            myCore.getAvgCustomerTimeInPayQueue().add(this.customer.getWaitingTimeInQueuePay());
            this.customer.setState(StateCustomer.PAYING);
            Customer_End_Payment_Event endPayment = new Customer_End_Payment_Event(myCore.getCurrentTime()+myCore.getPayment_Gen().getSample(), myCore, customer);
            myCore.addEvent(endPayment);
        } else {
            this.myCore.incCarsTransportingQuantity();
            this.myCore.incCarWaitingCustomersQuantity();
            this.customer.setEndTimeInQueue(myCore.getCurrentTime());
            this.customer.setStartTimeWaitingCheck(myCore.getCurrentTime());
            this.myCore.getAvgCustomerTimeInQueueCar().add(this.customer.getWaitingTimeInQueue());
            this.customer.setState(StateCustomer.WAITING_CAR);
            Car_Arrival_Workshop_Event carArrival = new Car_Arrival_Workshop_Event(myCore.getCurrentTime()+myCore.getCarToWorkShopParking_Gen().getSample(), myCore, customer);
            myCore.addEvent(carArrival);
        }
    }
}
