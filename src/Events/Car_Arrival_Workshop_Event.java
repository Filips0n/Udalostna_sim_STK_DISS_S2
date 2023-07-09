package Events;

import Event_Simulation.STK;
import Events.General.Event_Base;
import Events.General.Event_STK;
import Sim_Actors.Customer;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateMechanic;
import Sim_Actors.States.StateTechnician;
import Sim_Actors.Technician;

public class Car_Arrival_Workshop_Event extends Event_STK {
    public Car_Arrival_Workshop_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        this.myCore.descCarsTransportingQuantity();
        if (myCore.isMechanicAvailable()) {//empty parking
            this.myCore.getAvgMechanicFreeCount().delete(1);
            myCore.getMechanic().setState(StateMechanic.CHECKING_CAR, this.customer);
            Start_Check_Event startCheckEvent = new Start_Check_Event(myCore.getCurrentTime(), myCore, customer);
            myCore.addEvent(startCheckEvent);
        } else {
            this.myCore.addCarToWorkShopParking(customer.getCar());
        }

        if (myCore.isCustomerInQueue() && (myCore.showCustomerFromQueue().wantToPay() || myCore.isParkingPossible())) { //New customer for technician
            if (!myCore.showCustomerFromQueue().wantToPay()) {myCore.getAvgCustomerCountQueueCar().delete(1);}
            Customer customer = myCore.getCustomerFromQueue();
            Technician technician = myCore.getTechnicianByCustomer(this.customer);
            if (customer.wantToPay()) {
                    customer.setEndTimeInQueuePay(myCore.getCurrentTime());//-----------//todo new
                technician.setState(StateTechnician.SERVING_PAY, customer);
                customer.setState(StateCustomer.PAYING);
            } else {
//                myCore.getAvgCustomerCountQueueCar().delete(1);
                customer.setStartTimeWaitingCheck(myCore.getCurrentTime());
                technician.setState(StateTechnician.TRANSPORTING_CAR, customer);
                customer.setState(StateCustomer.WAITING_CAR);
            }
            Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore ,customer);
            myCore.addEvent(processingEvent);
        } else {//empty queue
            this.myCore.getAvgTechnicianFreeCount().add(1);
            this.myCore.returnTechnicianToReception(myCore.getTechnicianByCustomer(this.customer));
        }
    }
}
