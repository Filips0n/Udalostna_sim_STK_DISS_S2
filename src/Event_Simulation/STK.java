package Event_Simulation;

import Events.Customer_Arrival_Event;
import Events.Leave_STK_Event;
import Generators.*;
import Sim_Actors.*;
import Sim_Actors.States.StateMechanic;
import Sim_Actors.States.StateTechnician;
import Statistics.Arithmetic_Mean;
import Statistics.Weighted_Arithmetic_Mean;
import Structures.BoundedQueue;

import java.util.*;

public class STK extends Event_Core {

    private static double[][] DELIVERY_DISTRIBUTION = {
            {2100, 2220, 0.2},
            {2280, 2400, 0.35},
            {2460, 2820, 0.3},
            {2880, 3120, 0.15}
    };

    private static double[][] TRUCK_DISTRIBUTION = {
            {2220, 2520, 0.05},
            {2580, 2700, 0.1},
            {2760, 2820, 0.15},
            {2880, 3060, 0.4},
            {3120, 3300, 0.25},
            {3360, 3900, 0.05}
    };

    private PriorityQueue<Customer> waitingQueue;

    //Generators
    private CarType_Gen carTypeGen;
    private Empiric_Gen deliveryCarTime_Gen;
    private Empiric_Gen truckTime_Gen;
    private Exponential_Gen arrival_Gen;
    private Continuous_Gen payment_Gen;
    private Triangular_Gen carToWorkShopParking_Gen;
    private Discrete_Gen personalCarTime_Gen;
    //----

    //

    private static int maxParkingCapacity = 5;
    private static double startTimeSTK = 32400;//seconds
    private static double endTimeSTK = 61200;//seconds
    private static double latestArrivalTime = 56700;//seconds
    private BoundedQueue<Car> workShopParking;
    private int availableReceptionTech;
    private int availableMechanics;
    private int carsQuantityTransporting;
    private int maxAvailableMechanics;
    private int maxAvailableReceptionTech;
    private int carsAfterCheckQuantity;
    private int payingCustomersQuantity;
    private int carWaitingCustomersQuantity;
    private int totalCustomers;
    private int totalLeftCustomers;
    //

    //Statistics
    private Arithmetic_Mean avgCustomerTimeInSystem;
    private Arithmetic_Mean avgCustomerTimeInQueueCar;
    private Weighted_Arithmetic_Mean avgCustomerCountQueueCar;
    private Weighted_Arithmetic_Mean avgCustomerCountSystem;
    private Weighted_Arithmetic_Mean avgTechnicianFreeCount;
    private Weighted_Arithmetic_Mean avgMechanicFreeCount;

    private Arithmetic_Mean avgCustomerTimeInPayQueue;
    private Arithmetic_Mean avgCustomerTimeWaitingCheck;
    //

    //Simulation statistics
    private Arithmetic_Mean avgSimCustomerTimeInSystem;
    private Arithmetic_Mean avgSimCustomerTimeInQueueCar;
    private Arithmetic_Mean avgSimCustomerCountQueueCar;
    private Arithmetic_Mean avgSimCustomerCountSystem;
    private Arithmetic_Mean avgSimTechnicianFreeCount;
    private Arithmetic_Mean avgSimMechanicFreeCount;
    private Arithmetic_Mean avgSimCarInSystemCount;
    private Arithmetic_Mean avgSimCustomersInSystemCount;

    private Arithmetic_Mean avgSimCustomerTimeInPayQueue;
    private Arithmetic_Mean avgSimCustomerTimeWaitingCheck;
    //

    //Lists
    int newCustomerID = 1;
    private ArrayList<Actor> allCustomers;
    private ArrayList<Actor> allTechnicians;
    private ArrayList<Actor> allMechanics;
    //

    public STK(Random seedGenerator) {
        super(seedGenerator, startTimeSTK, endTimeSTK);
    }
    @Override
    protected void afterSimulation() {
        isRunning = false;
        if (simMode == SimulationMode.TURBO) refreshGui();
    }

    @Override
    protected void afterReplication(int i) {
        if (isRunning) {
            //statitiky iba ak sa predcasne neukonci replikacia
            avgCustomerCountSystem.delete(0);
            this.avgSimCustomerCountSystem.add(this.avgCustomerCountSystem.getWeightedMean());
            avgMechanicFreeCount.delete(0);
            this.avgSimMechanicFreeCount.add(this.avgMechanicFreeCount.getWeightedMean());
            avgTechnicianFreeCount.delete(0);
            this.avgSimTechnicianFreeCount.add(this.avgTechnicianFreeCount.getWeightedMean());
            this.avgCustomerCountQueueCar.delete(getCountQueueCar());
            this.avgSimCustomerCountQueueCar.add(this.avgCustomerCountQueueCar.getWeightedMean());
//            endTimeInSystemToCustomers();//todo nemal by som volat
            this.avgSimCustomerTimeInSystem.add(this.avgCustomerTimeInSystem.getMean());
            this.avgSimCustomerTimeInQueueCar.add(this.avgCustomerTimeInQueueCar.getMean());
            this.avgSimCarInSystemCount.add(this.getCarsInSTKQuantity());
            this.avgSimCustomersInSystemCount.add(this.getCustomersInSTKAtClosure());
            avgSimCustomerTimeWaitingCheck.add(this.avgCustomerTimeWaitingCheck.getMean());
            avgSimCustomerTimeInPayQueue.add(this.avgCustomerTimeInPayQueue.getMean());
            refreshGui();
        }
        reset();
    }

    private void endTimeInSystemToCustomers() {
        for (Actor customer :
                allCustomers) {
            if (!customer.getStateDesc().equals("Odisiel")) {

//                if (customer.getStateDesc().equals("V rade na odovz. auta")) {
//                    ((Customer)customer).setEndTimeInQueue(endTimeSTK);
//                    getAvgCustomerTimeInQueueCar().add(((Customer)customer).getWaitingTimeInQueue());
//                }

                ((Customer)customer).setEndTimeInSystem(endTimeSTK);
                getAvgCustomerTimeInSystem().add(((Customer)customer).getWaitingTimeInSystem());
            }
        }
    }

    private void reset() {
        this.timeLine.clear();
        this.currentTime = startTimeSTK;
        waitingQueue.clear();
        workShopParking.clear();
        availableReceptionTech = maxAvailableReceptionTech;
        availableMechanics = maxAvailableMechanics;
        carsQuantityTransporting = 0;
        carsAfterCheckQuantity = 0;
        payingCustomersQuantity = 0;
        carWaitingCustomersQuantity = 0;
        totalCustomers = 0;
        totalLeftCustomers = 0;
        //Lists
        allCustomers = new ArrayList<>();
        allTechnicians = new ArrayList<>(maxAvailableReceptionTech);
        allMechanics = new ArrayList<>(maxAvailableMechanics);
        newCustomerID = 1;

        //statistiky
        avgCustomerTimeInSystem.reset();
        avgCustomerTimeInQueueCar.reset();
        avgMechanicFreeCount.reset();
        avgTechnicianFreeCount.reset();
        avgCustomerCountSystem.reset();
        avgCustomerCountQueueCar.reset();
        this.avgCustomerTimeInPayQueue.reset();
        this.avgCustomerTimeWaitingCheck.reset();
    }

    @Override
    protected void beforeSimulation() {
        //Generators
        this.carTypeGen = new CarType_Gen(seedGenerator);
        this.deliveryCarTime_Gen = new Empiric_Gen(seedGenerator, DELIVERY_DISTRIBUTION);
        this.truckTime_Gen = new Empiric_Gen(seedGenerator, TRUCK_DISTRIBUTION);
        this.personalCarTime_Gen = new Discrete_Gen(seedGenerator, 1860,2700);
        this.arrival_Gen = new Exponential_Gen(seedGenerator, (double)3600/23);
        this.payment_Gen = new Continuous_Gen(seedGenerator, 65.0, 177.0);
        this.carToWorkShopParking_Gen = new Triangular_Gen(seedGenerator, 180.0, 695.0, 431.0);
        //----
        this.waitingQueue = new PriorityQueue<>();
        this.workShopParking = new BoundedQueue<>(maxParkingCapacity);
        this.carsQuantityTransporting = 0;
        //Stats
        this.avgCustomerTimeInQueueCar = new Arithmetic_Mean();
        this.avgCustomerTimeInSystem = new Arithmetic_Mean();
        this.avgMechanicFreeCount = new Weighted_Arithmetic_Mean(this);
        this.avgTechnicianFreeCount = new Weighted_Arithmetic_Mean(this);
        this.avgCustomerCountQueueCar = new Weighted_Arithmetic_Mean(this);
        this.avgCustomerCountSystem  = new Weighted_Arithmetic_Mean(this);
        this.avgCustomerTimeInPayQueue = new Arithmetic_Mean();
        this.avgCustomerTimeWaitingCheck = new Arithmetic_Mean();
        //
        //Simulation Stats
        this.avgSimCustomerTimeInSystem = new Arithmetic_Mean();
        this.avgSimCustomerTimeInQueueCar = new Arithmetic_Mean();
        this.avgSimCustomerCountQueueCar = new Arithmetic_Mean();
        this.avgSimCustomerCountSystem = new Arithmetic_Mean();
        this.avgSimTechnicianFreeCount = new Arithmetic_Mean();
        this.avgSimMechanicFreeCount = new Arithmetic_Mean();
        this.avgSimCarInSystemCount = new Arithmetic_Mean();
        this.avgSimCustomersInSystemCount = new Arithmetic_Mean();
        this.avgSimCustomerTimeWaitingCheck = new Arithmetic_Mean();
        this.avgSimCustomerTimeInPayQueue = new Arithmetic_Mean();
        //
        reset();
    }

    @Override
    protected void beforeReplication() {
        for (int i = 0; i < maxAvailableReceptionTech; i++) {
            allTechnicians.add(new Technician(i+1));
        }
        for (int i = 0; i < maxAvailableMechanics; i++) {
            allMechanics.add(new Mechanic(i+1));
        }

        getAvgTechnicianFreeCount().add(maxAvailableReceptionTech);
        getAvgMechanicFreeCount().add(maxAvailableMechanics);

        Customer_Arrival_Event arrivalEvent = new Customer_Arrival_Event(startTimeSTK, this, null);
        this.addEvent(arrivalEvent);

//        Leave_STK_Event leaveEvent = new Leave_STK_Event(startTimeSTK, this);
//        this.addEvent(leaveEvent);
    }
    public Exponential_Gen getCustomerArrivalGenerator() {
        return arrival_Gen;
    }

    public CarType_Gen getCarTypeGenerator() {
        return this.carTypeGen;
    }

    public void returnTechnicianToReception(Technician tech) {
        this.availableReceptionTech++;
        tech.setState(StateTechnician.FREE, null);
    }
    public double getStartTime() {return startTimeSTK;}
    public Technician getTechnicianFromReception() {
//
        for (Actor technician : allTechnicians) {
            if (((Technician) technician).getState() == StateTechnician.FREE) {this.availableReceptionTech--;return (Technician)technician;}
        }
        return null;
    }
    public int getAvailableReceptionTech() {
        return availableReceptionTech;
    }

    public boolean isTechnicianAvailable() {
        return availableReceptionTech > 0;
    }

    public void returnMechanic(Mechanic mech) {
        this.availableMechanics++;
        mech.setState(StateMechanic.FREE, null);
    }

    public Mechanic getMechanic() {
//
        for (Actor mechanic : allMechanics) {
            if (((Mechanic) mechanic).getState() == StateMechanic.FREE) {this.availableMechanics--;return (Mechanic) mechanic;}
        }
        return null;
    }

    public boolean isMechanicAvailable() {
        return availableMechanics > 0;
    }

    public void setAvailableReceptionTech(int availableReceptionTech) {
        this.availableReceptionTech = availableReceptionTech;
        this.maxAvailableReceptionTech = availableReceptionTech;
    }

    public void setAvailableMechanics(int availableMechanics) {
        this.maxAvailableMechanics = availableMechanics;
        this.availableMechanics = availableMechanics;
    }
    public Continuous_Gen getPayment_Gen() {
        return payment_Gen;
    }

    public Triangular_Gen getCarToWorkShopParking_Gen() {
        return carToWorkShopParking_Gen;
    }

    public void addCustomerToQueue(Customer customer) {
        this.waitingQueue.add(customer);
    }

    public boolean isCustomerInQueue() {
        return !this.waitingQueue.isEmpty();
    }

    public Customer getCustomerFromQueue() {
        return this.waitingQueue.poll();
    }

    public ArrayList<Customer> getAllQueueCarWaiting() {
        ArrayList<Customer> waitingQueueCar = new ArrayList<>();

        for (Customer customer : waitingQueue) {
            if (!customer.wantToPay()) {
                waitingQueueCar.add(customer);
            }
        }

        return waitingQueueCar;
    }

    public void addCarToWorkShopParking(Car car) {
        this.workShopParking.add(car);
    }

    public boolean isCarWaiting() {
        return !this.workShopParking.isEmpty();
    }

    public Car getCarFromQueue() {
        return this.workShopParking.poll();
    }

    public int getSampleByCarType(CarType carType) {
        if (carType == CarType.PERSONAL) {
            return personalCarTime_Gen.getSample();
        } else if (carType == CarType.DELIVERY) {
            return deliveryCarTime_Gen.getSample();
        } else {
            return truckTime_Gen.getSample();
        }
    }

    public int getAvailableMechanics() {
        return availableMechanics;
    }

    public int getWorkShopParkingCount() {
        return this.workShopParking.size();
    }

    public boolean isParkingQueueFull() {return this.workShopParking.isFull();}
    public int parkingQueueSize() {return this.workShopParking.size();}
    public void incCarsTransportingQuantity() {
        this.carsQuantityTransporting++;}
    public void descCarsTransportingQuantity() {
        this.carsQuantityTransporting--;}

    public boolean isParkingPossible() {
//        if (parkingQueueSize()+this.carsQuantityTransporting > 5) {
//            System.out.println("Chzba");
//        }
        return parkingQueueSize()+this.carsQuantityTransporting < maxParkingCapacity ;// + availableMechanics;
    }

    public int getCarsQuantityTransporting() {
        return carsQuantityTransporting;
    }

    public double getMaxTime() {
        return endTimeSTK;
    }

    public static double getLatestArrivalTime() {
        return latestArrivalTime;
    }

    public int getWaitingCustomersQuantity() {
        return this.waitingQueue.size();
    }

    public int getWaitingPayCustomersQuantity() {
        int value = 0;
        for (Customer customer: this.waitingQueue) {
            if (customer.wantToPay()) {value++;}
        }
        return value;
    }

    public int getWaitingNewCustomersQuantity() {
        int value = 0;
        for (Customer customer: this.waitingQueue) {
            if (!customer.wantToPay()) {value++;}
        }
        return value;
    }

    public int getCarWaitingCustomersQuantity() {
        return this.carWaitingCustomersQuantity;
    }

    public Customer showCustomerFromQueue() {
        return this.waitingQueue.peek();
    }

    public void decCarsAfterCheckQuantity() {
        this.carsAfterCheckQuantity--;
    }
    public void incCarsAfterCheckQuantity() {
        this.carsAfterCheckQuantity++;
    }

    public void decPayingCustomersQuantity() {
        this.payingCustomersQuantity--;
    }
    public void incPayingCustomersQuantity() {
        this.payingCustomersQuantity++;
    }

    public void decCarWaitingCustomersQuantity() {
        this.carWaitingCustomersQuantity--;
    }
    public void incCarWaitingCustomersQuantity() {
        this.carWaitingCustomersQuantity++;
    }

    public void incTotalCustomers() {
        this.totalCustomers++;
    }
    public void incTotalLeftCustomers() {
        this.totalLeftCustomers++;
    }

    public int getCarsInSTKQuantity() {
        return this.workShopParking.size() + (maxAvailableMechanics-availableMechanics) + this.carsQuantityTransporting + this.carsAfterCheckQuantity;
    }

    public int getCustomersInSTKAtClosure(){
        return this.workShopParking.size() + (maxAvailableMechanics-availableMechanics) + this.carsQuantityTransporting + this.carsAfterCheckQuantity
                + waitingQueue.size();
    }

    public int getPayingCustomersQuantity() {
        return this.payingCustomersQuantity;
    }

    public int getCarsAfterCheckQuantity() {
        return carsAfterCheckQuantity;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalLeftCustomers() {
        return totalLeftCustomers;
    }

    public Arithmetic_Mean getAvgCustomerTimeInSystem() {
        return avgCustomerTimeInSystem;
    }

    public Arithmetic_Mean getAvgCustomerTimeInQueueCar() {
        return avgCustomerTimeInQueueCar;
    }

    public ArrayList<ArrayList<String>> getAllCustomers() {
        return getStringList(allCustomers);
    }

    private ArrayList<ArrayList<String>> getStringList(Collection<Actor> allActors) {
        ArrayList<ArrayList<String>> stringList = new ArrayList<>();

        for (Actor actor: allActors) {
            stringList.add(new ArrayList<>(Arrays.asList(actor.getName(), actor.getStateDesc(), actor.getCarDesc())));
        }

        return stringList;
    }

    public ArrayList<ArrayList<String>> getAllTechnicians() {
        return getStringList(allTechnicians);
    }

    public ArrayList<ArrayList<String>> getAllMechanics() {
        return getStringList(allMechanics);
    }

    public ArrayList<ArrayList<String>> getAllInQueue() {
        PriorityQueue<Customer> sortedQueue = new PriorityQueue<>(waitingQueue);
        List<Actor> sortedList = new ArrayList<>();
        while (!sortedQueue.isEmpty()) {
            sortedList.add(sortedQueue.poll());
        }
        return getStringList(sortedList);
    }
    public ArrayList<ArrayList<String>> getAllCarsWaiting() {
        ArrayList<ArrayList<String>> stringList = new ArrayList<>();

        int i = 1;
        for (Car car: workShopParking.getData()) {
            stringList.add(new ArrayList<>(Arrays.asList("" + i + ".", car.getCarName())));
            i++;
        }

        return stringList;
    }
    public int getCustomerId() {
        return newCustomerID++;
    }

    public void addCustomerToAllCustomers(Customer customer) {
        this.allCustomers.add(customer);
    }

    public void removeCustomerFromAllCustomers(Customer customer) {
        this.allCustomers.remove(customer);
    }

    public Technician getTechnicianByCustomer(Customer customer) {
        for (Actor tech : allTechnicians) {
            if(((Technician)tech).getServingCustomer() == customer) {return (Technician)tech;}
        }
        return null;
    }

    public Mechanic getMechanicByCustomer(Customer customer) {
        for (Actor mech : allMechanics) {
            if(((Mechanic)mech).getServingCustomer() == customer) {return (Mechanic)mech;}
        }
        return null;
    }

    public int getCountQueueCar() {
        int i = 0;
        for (Customer customer: waitingQueue) {
            if (!customer.wantToPay()) {
                i++;
            }
        }
        return i;
    }

    public Weighted_Arithmetic_Mean getAvgCustomerCountQueueCar() {
        return avgCustomerCountQueueCar;
    }

    public Weighted_Arithmetic_Mean getAvgCustomerCountSystem() {
        return avgCustomerCountSystem;
    }

    public Weighted_Arithmetic_Mean getAvgTechnicianFreeCount() {
        return avgTechnicianFreeCount;
    }

    public Weighted_Arithmetic_Mean getAvgMechanicFreeCount() {
        return avgMechanicFreeCount;
    }

    public Arithmetic_Mean getAvgCustomerTimeInPayQueue() {
        return avgCustomerTimeInPayQueue;
    }

    public Arithmetic_Mean getAvgCustomerTimeWaitingCheck() {
        return avgCustomerTimeWaitingCheck;
    }

    public Arithmetic_Mean getAvgSimCustomerTimeInSystem() {
        return avgSimCustomerTimeInSystem;
    }

    public Arithmetic_Mean getAvgSimCustomerTimeInQueueCar() {
        return avgSimCustomerTimeInQueueCar;
    }

    public Arithmetic_Mean getAvgSimCustomerCountQueueCar() {
        return avgSimCustomerCountQueueCar;
    }

    public Arithmetic_Mean getAvgSimCustomerCountSystem() {
        return avgSimCustomerCountSystem;
    }

    public Arithmetic_Mean getAvgSimTechnicianFreeCount() {
        return avgSimTechnicianFreeCount;
    }

    public Arithmetic_Mean getAvgSimMechanicFreeCount() {
        return avgSimMechanicFreeCount;
    }

    public Arithmetic_Mean getAvgSimCarInSystemCount() {
        return avgSimCarInSystemCount;
    }

    public Arithmetic_Mean getAvgSimCustomersInSystemCount() {
        return avgSimCustomersInSystemCount;
    }

    public Arithmetic_Mean getAvgSimCustomerTimeInPayQueue() {
        return avgSimCustomerTimeInPayQueue;
    }

    public Arithmetic_Mean getAvgSimCustomerTimeWaitingCheck() {
        return avgSimCustomerTimeWaitingCheck;
    }

    public void removeCustomerFromQueue(Customer customer) {
        waitingQueue.remove(customer);
    }
}
