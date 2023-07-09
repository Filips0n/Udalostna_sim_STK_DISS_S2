package Events;

import Event_Simulation.STK;
import Events.General.Event_STK;
import Sim_Actors.Car;
import Sim_Actors.Customer;
import Sim_Actors.Mechanic;
import Sim_Actors.States.StateCustomer;
import Sim_Actors.States.StateMechanic;
import Sim_Actors.States.StateTechnician;

public class End_Check_Event extends Event_STK {
    public End_Check_Event(double time, STK myCore, Customer customer) {
        super(time, myCore, customer);
    }

    @Override
    public void execute() {
        this.myCore.incCarsAfterCheckQuantity();
        this.myCore.decCarWaitingCustomersQuantity();
        Customer_Start_Payment_Event customerStartPaymentEvent = new Customer_Start_Payment_Event(myCore.getCurrentTime(), myCore, customer);
        myCore.addEvent(customerStartPaymentEvent);

        Mechanic mechanic = myCore.getMechanicByCustomer(this.customer);
        if (myCore.isCarWaiting()) {//New car for mechanic
            Car car = myCore.getCarFromQueue();
            mechanic.setState(StateMechanic.CHECKING_CAR, car.getOwner());
            Start_Check_Event startCheckEvent = new Start_Check_Event(myCore.getCurrentTime(), myCore, car.getOwner());
            myCore.addEvent(startCheckEvent);
        } else {//empty parking
            this.myCore.getAvgMechanicFreeCount().add(1);
            this.myCore.returnMechanic(mechanic);
        }

        if (myCore.isCustomerInQueue() && myCore.getAvailableReceptionTech() > 1) {
            if (myCore.showCustomerFromQueue().wantToPay()) {
                this.myCore.getAvgTechnicianFreeCount().delete(1);//nikdy nenastane
                Customer customer = myCore.getCustomerFromQueue();
                myCore.getTechnicianFromReception().setState(StateTechnician.SERVING_PAY, customer);
                customer.setState(StateCustomer.PAYING);
                Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore, customer);
                myCore.addEvent(processingEvent);
            } else if (myCore.isParkingPossible()){
                this.myCore.getAvgTechnicianFreeCount().delete(1);
                myCore.getAvgCustomerCountQueueCar().delete(1);
                Customer customer = myCore.getCustomerFromQueue();
                customer.setStartTimeWaitingCheck(myCore.getCurrentTime());
                myCore.getTechnicianFromReception().setState(StateTechnician.TRANSPORTING_CAR, customer);
                customer.setState(StateCustomer.WAITING_CAR);
                Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore, customer);
                myCore.addEvent(processingEvent);
            }
        }

        //todo uprava
//        if (myCore.isCustomerInQueue() && myCore.getAvailableReceptionTech() > 1 && myCore.isParkingPossible() && !myCore.showCustomerFromQueue().wantToPay()) {
//            this.myCore.getAvgTechnicianFreeCount().delete(1);
//            myCore.getAvgCustomerCountQueueCar().delete(1);
//            Customer customer = myCore.getCustomerFromQueue();
//            myCore.getTechnicianFromReception().setState(StateTechnician.TRANSPORTING_CAR, customer);
//            customer.setState(StateCustomer.WAITING_CAR);
//            Processing_Customer_Event processingEvent = new Processing_Customer_Event(myCore.getCurrentTime(), myCore ,customer);
//            myCore.addEvent(processingEvent);
//        }
    }
}
